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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.annotation.CustomGroupArgument;
import pt.ist.bennu.core.annotation.CustomGroupConstructor;
import pt.ist.bennu.core.annotation.CustomGroupOperator;
import pt.ist.bennu.core.domain.User;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;

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

    public static interface Argument<A, G extends CustomGroup> extends Serializable {
        public A parse(String argument);

        public String extract(G group);
    }

    public static class Operator<G extends CustomGroup> implements Serializable {
        private final String operator;

        private final Class<? extends G> type;

        private final Constructor<G> constructor;

        private final List<Argument<?, G>> arguments;

        private final Method groupsForUser;

        public Operator(String operator, Class<? extends G> type, Constructor<G> constructor, List<Argument<?, G>> arguments,
                Method groupsForUser) {
            this.operator = operator;
            this.type = type;
            this.arguments = arguments;
            this.constructor = constructor;
            this.groupsForUser = groupsForUser;
        }

        public Class<? extends G> getType() {
            return type;
        }

        public G parse(String[] parameters) {
            final Object[] parsed = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                parsed[i] = this.arguments.get(i).parse(parameters[i]);
            }
            G group = select(type, new Predicate<G>() {
                @Override
                public boolean apply(G input) {
                    Object[] extracted = new Object[arguments.size()];
                    for (int i = 0; i < arguments.size(); i++) {
                        extracted[i] = arguments.get(i).extract(input);
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
            return params.isEmpty() ? operator : operator + "(" + Joiner.on(", ").join(params) + ")";
        }

        public Set<Group> groupsForUser(User user) {
            try {
                return (Set<Group>) groupsForUser.invoke(null, user);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new Error(e);
            }
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

    public static Set<Group> groupsForUser(User user) {
        Set<Group> groups = new HashSet<>();
        for (Operator<?> operator : operators.values()) {
            groups.addAll(operator.groupsForUser(user));
        }
        return groups;
    }

    public static void registerOperator(Class<? extends CustomGroup> type) {
        CustomGroupOperator operatorAnnotation = type.getAnnotation(CustomGroupOperator.class);
        Constructor<CustomGroup> constructor = findConstructor(type);
        List<Argument<?, CustomGroup>> arguments = findArguments(type);
        Method groupsForUser = findGroupsForUser(type);
        Operator<CustomGroup> operator = new Operator<>(operatorAnnotation.value(), type, constructor, arguments, groupsForUser);
        operators.put(operatorAnnotation.value(), operator);
        types.put(type, operator);
        if (logger.isInfoEnabled()) {
            logger.info("Registering group language operator: " + operatorAnnotation.value());
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

    private static List<Argument<?, CustomGroup>> findArguments(Class<? extends CustomGroup> type) {
        try {
            SortedMap<Integer, Argument<?, CustomGroup>> arguments = new TreeMap<>();
            for (Method method : type.getMethods()) {
                CustomGroupArgument annotation = method.getAnnotation(CustomGroupArgument.class);
                if (annotation != null) {
                    arguments.put(annotation.index(), (Argument<?, CustomGroup>) method.invoke(null));
                }
            }
            return new Vector<>(arguments.values());
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new Error("CustomGroup: " + type.getName() + " failed to access it's arguments");
        }
    }

    private static Method findGroupsForUser(Class<? extends CustomGroup> type) {
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
