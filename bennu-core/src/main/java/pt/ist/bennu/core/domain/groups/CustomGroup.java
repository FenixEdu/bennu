package pt.ist.bennu.core.domain.groups;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.domain.groups.annotation.CustomGroupArgument;
import pt.ist.bennu.core.domain.groups.annotation.CustomGroupConstructor;
import pt.ist.bennu.core.domain.groups.annotation.CustomGroupOperator;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;

public abstract class CustomGroup extends CustomGroup_Base {
	private static final Logger logger = LoggerFactory.getLogger(CustomGroup.class);

	public static interface Argument<A, G extends CustomGroup> extends Serializable {
		public A parse(String argument);

		public String extract(G group);
	}

	public static class Operator<G extends CustomGroup> implements Serializable {
		private final String operator;

		private final Class<? extends G> type;

		private final Constructor<G> constructor;

		private final Argument<?, G>[] arguments;

		public Operator(String operator, Class<? extends G> type, Constructor<G> constructor, Argument<?, G>[] arguments) {
			this.operator = operator;
			this.type = type;
			this.arguments = arguments;
			this.constructor = constructor;
		}

		public Class<? extends G> getType() {
			return type;
		}

		public G parse(String[] parameters) {
			final Object[] parsed = new Object[parameters.length];
			for (int i = 0; i < parameters.length; i++) {
				parsed[i] = this.arguments[i].parse(parameters[i]);
			}
			G group = select(type, new Predicate<G>() {
				@Override
				public boolean apply(@Nullable G input) {
					Object[] extracted = new Object[arguments.length];
					for (int i = 0; i < arguments.length; i++) {
						extracted[i] = arguments[i].extract(input);
					}
					return Arrays.equals(parsed, extracted);
				}
			});
			try {
				return group != null ? group : constructor.newInstance(parsed);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new Error(e);
			}
		}

		@SuppressWarnings("unchecked")
		public String expression(CustomGroup group) {
			List<String> params = new ArrayList<>();
			for (Argument<?, G> argument : arguments) {
				params.add(argument.extract((G) group));
			}
			return params.isEmpty() ? operator : (operator + "(" + Joiner.on(", ").join(params) + ")");
		}
	}

	protected CustomGroup() {
		super();
	}

	@Override
	public String expression() {
		return types.get(this.getClass()).expression(this);
	}

	@SuppressWarnings("unchecked")
	public static <T extends CustomGroup> T parse(String operator, String[] parameters) {
		return (T) operators.get(operator).parse(parameters);
	}

	/* CustomGroup language registry */

	private static final Map<String, Operator<?>> operators = new HashMap<>();

	private static final Map<Class<? extends CustomGroup>, Operator<?>> types = new HashMap<>();

	public static void registerOperator(Class<? extends CustomGroup> type) {
		CustomGroupOperator operatorAnnotation = type.getAnnotation(CustomGroupOperator.class);
		Constructor<CustomGroup> constructor = findConstructor(type);
		Argument<?, CustomGroup>[] arguments = findArguments(type);
		Operator<CustomGroup> operator = new Operator<>(operatorAnnotation.operator(), type, constructor, arguments);
		operators.put(operatorAnnotation.operator(), operator);
		types.put(type, operator);
		if (logger.isInfoEnabled()) {
			logger.info("Registering group language operator: " + operatorAnnotation.operator());
		}
	}

	@SuppressWarnings("unchecked")
	private static Constructor<CustomGroup> findConstructor(Class<? extends CustomGroup> type) {
		for (Constructor<?> constructor : type.getConstructors()) {
			CustomGroupConstructor annotation = constructor.getAnnotation(CustomGroupConstructor.class);
			if (annotation != null) {
				return (Constructor<CustomGroup>) constructor;
			}
		}
		throw new Error("CustomGroup: " + type.getName() + " does not have a proper constructor");
	}

	private static Argument<?, CustomGroup>[] findArguments(Class<? extends CustomGroup> type) {
		try {
			SortedMap<Integer, Argument<?, CustomGroup>> arguments = new TreeMap<>();
			for (Method method : type.getMethods()) {
				CustomGroupArgument annotation = method.getAnnotation(CustomGroupArgument.class);
				if (annotation != null) {
					arguments.put(annotation.index(), (Argument<?, CustomGroup>) method.invoke(null));
				}
			}
			return (Argument<?, CustomGroup>[]) arguments.values().toArray();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new Error("CustomGroup: " + type.getName() + " failed to access it's arguments");
		}
	}
}
