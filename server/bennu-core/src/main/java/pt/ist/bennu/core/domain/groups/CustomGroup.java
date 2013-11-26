/*
 * CustomGroup.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.domain.groups;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.annotation.CustomGroupArgument;
import pt.ist.bennu.core.annotation.CustomGroupOperator;
import pt.ist.bennu.core.domain.User;

import com.google.common.base.Joiner;

/**
 * <p>
 * Base class for custom groups. They are defined by an operator name and arguments and are represented in the language as
 * operator(arg1, args2, ...)
 * </p>
 * 
 * <p>
 * To create a custom group extends this class and annotate that type with {@link CustomGroupOperator}. Define for each argument
 * of your group a static method that returns an {@link Argument} instance, and annotate that method with
 * {@link CustomGroupArgument}. Finally create a protected constructor receiving the types of you arguments in order and annotate
 * that constructor with {@link CustomGroupConstructor}.
 * </p>
 * 
 * <p>
 * Example:
 * 
 * <pre>
 * <code>
 * {@literal @}CustomGroupOperator("unit")
 * public class UnitGroup extends CustomGroup {
 *   {@literal @}CustomGroupConstructor
 *   public UnitGroup(Unit unit) {
 *     setUnit(unit);
 *   }
 * 
 *   {@literal @}CustomGroupArgument
 *   public static Argument<Unit, UnitGroup> unitArg() {
 *     return new Argument<Unit, UnitGroup>() {
 *       {@literal @}Override
 *       public Unit parse(String argument) {
 *         return Unit.fromAcronym(argument);
 *       }
 * 
 *       {@literal @}Override
 *       public String extract(UnitGroup group) {
 *         return group.getUnit();
 *       }
 *     };
 *   }
 * 
 *   {@literal @}Service
 *   public static UnitGroup getInstance(Unit unit) {
 *     UnitGroup group = select(UnitGroup.class, new Predicate<UnitGroup>() {
 *       {@literal @}Override
 *       public boolean apply({@literal @}Nullable UnitGroup input) {
 *         return input.getUnit().equals(unit);
 *       }
 *     });
 *     return group != null ? group : new UnitGroup(unit);
 *   }
 * }
 * </code>
 * </pre>
 * 
 * </p>
 * 
 * @see Group
 */
public abstract class CustomGroup extends CustomGroup_Base {
    private static final Logger logger = LoggerFactory.getLogger(CustomGroup.class);

    protected static interface Argument<A> extends Serializable {
        public A parse(String argument);

        public Class<? extends A> getType();
    }

    protected static interface SimpleArgument<A, G extends CustomGroup> extends Argument<A> {
        public String extract(G group);
    }

    protected static interface MultiArgument<A, G extends CustomGroup> extends Argument<A> {
        public Set<String> extract(G group);
    }

    private static class Operator<G extends CustomGroup> implements Serializable {
        private static final long serialVersionUID = 1746924894935208630L;

        private final String operator;

        private final Class<? extends G> type;

        private final Vector<Argument<Object>> arguments;

        private final Method instantiator;

        private final Method groupsForUser;

        public Operator(Class<? extends G> type) {
            this.operator = type.getAnnotation(CustomGroupOperator.class).value();
            this.type = type;
            this.arguments = findArguments();
            this.instantiator = findInstantiator();
            this.groupsForUser = findGroupsForUser();
        }

        public G parse(String[] parameters) {
            final Object[] parsed = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                parsed[i] = getArgumentForArgumentIndex(i).parse(parameters[i]);
            }
            try {
                return (G) instantiator.invoke(null, parsed);
            } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
                throw new Error("CustomGroup: error invoking " + type.getName() + ".getInstance(..)", e.getCause());
            }
        }

        private Argument<Object> getArgumentForArgumentIndex(int i) {
            if (i >= arguments.size()) {
                return arguments.get(arguments.size() - 1);
            }
            return arguments.get(i);
        }

        @SuppressWarnings("unchecked")
        public String expression(G customGroup) {
            List<String> params = new ArrayList<>();
            for (Argument<Object> argument : arguments) {
                if (argument instanceof SimpleArgument) {
                    params.add(((SimpleArgument<Object, G>) argument).extract(customGroup));
                } else {
                    params.addAll(((MultiArgument<Object, G>) argument).extract(customGroup));
                }
            }
            return params.isEmpty() ? operator : operator + "(" + Joiner.on(", ").join(params) + ")";
        }

        public Set<Group> groupsForUser(User user) {
            try {
                return (Set<Group>) groupsForUser.invoke(null, user);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                }
                if (e.getCause() instanceof Error) {
                    throw (Error) e.getCause();
                }
                throw new Error(e.getCause());
            } catch (IllegalAccessException | IllegalArgumentException e) {
                throw new Error(e);
            }
        }

        private Vector<Argument<Object>> findArguments() {
            try {
                Map<Integer, Argument<Object>> args = new TreeMap<>();
                for (Method method : type.getMethods()) {
                    CustomGroupArgument annotation = method.getAnnotation(CustomGroupArgument.class);
                    if (annotation != null) {
                        if (args.get(annotation.index()) != null) {
                            throw new Error("CustomGroup: " + type.getName()
                                    + " duplicate index, please set index property in @CustomGroupArgument annotations");
                        }
                        args.put(annotation.index(), (Argument<Object>) method.invoke(null));
                    }
                }
                final Vector<Argument<Object>> argsVector = new Vector<>();
                argsVector.addAll(args.values());
                return argsVector;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new Error("CustomGroup: " + type.getName() + " failed to access it's arguments", e);
            }
        }

        private Method findInstantiator() {
            Class<?>[] parameterTypes = new Class<?>[arguments.size()];
            for (int i = 0; i < arguments.size(); i++) {
                parameterTypes[i] = getArgumentForArgumentIndex(i).getType();
            }
            try {
                Method method = type.getDeclaredMethod("getInstance", parameterTypes);
                if (Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())) {
                    return method;
                }
            } catch (NoSuchMethodException | SecurityException e) {
            }
            throw new Error("CustomGroup: " + type.getName() + " is missing getInstance(...) static method");
        }

        private Method findGroupsForUser() {
            try {
                Method method = type.getDeclaredMethod("groupsForUser", User.class);
                if (Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())) {
                    return method;
                }
            } catch (NoSuchMethodException | SecurityException e) {
            }
            throw new Error("CustomGroup: " + type.getName() + " is missing groupsForUser(User) static method");
        }
    }

    protected CustomGroup() {
        super();
    }

    @Override
    public String expression() {
        return getOperator(this.getClass()).expression(this);
    }

    @SuppressWarnings("unchecked")
    public static <T extends CustomGroup> T parse(String operator, String[] parameters) {
        return (T) getOperator(operator).parse(parameters);
    }

    public static Set<Group> groupsForUser(User user) {
        Set<Group> groups = new HashSet<>();
        for (Operator<?> operator : operators.values()) {
            groups.addAll(operator.groupsForUser(user));
        }
        return groups;
    }

    /* CustomGroup language registry */

    private static final Map<String, Operator<?>> operators = new HashMap<>();

    private static final Map<Class<? extends CustomGroup>, Operator<?>> types = new HashMap<>();

    private static <O extends Operator<CustomGroup>> O getOperator(String operator) {
        return (O) operators.get(operator);
    }

    private static <G extends CustomGroup> Operator<G> getOperator(Class<? extends G> operator) {
        return (Operator<G>) types.get(operator);
    }

    public static void registerOperator(Class<? extends CustomGroup> type) {
        Operator<CustomGroup> operator = new Operator<>(type);
        if (operators.containsKey(operator.operator)) {
            throw new Error("CustomGroup: Duplicate operator name: " + operator.operator);
        }
        operators.put(operator.operator, operator);
        types.put(type, operator);
        if (logger.isInfoEnabled()) {
            logger.debug("Registering group language operator: " + operator.operator);
        }
    }
}
