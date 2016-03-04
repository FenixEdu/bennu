package org.fenixedu.bennu.core.groups;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.fenixedu.bennu.core.annotation.GroupArgument;
import org.fenixedu.bennu.core.annotation.GroupArgumentParser;
import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.joda.time.DateTime;

import com.google.common.base.Strings;

public class CustomGroupRegistry {
    public static final String ARGUMENT_NAME_AS_FIELD_NAME = "QEHyHHkCEL6F2L0wZA6R";

    private static class CustomGroupMetadata {
        private final String operator;

        private final Class<? extends CustomGroup> type;

        private final Constructor<? extends CustomGroup> constructor;

        private final Map<String, Field> fields = new HashMap<>();

        public CustomGroupMetadata(Class<? extends CustomGroup> type) {
            GroupOperator operator = type.getAnnotation(GroupOperator.class);
            this.operator = operator.value();
            if (!GroupParser.isValidIdentifier(this.operator)) {
                throw BennuCoreDomainException.invalidGroupIdentifier(this.operator);
            }
            this.type = type;
            this.constructor = findConstructor(type);
            Class<?> current = type;
            while (current != null) {
                for (Field field : current.getDeclaredFields()) {
                    GroupArgument argument = field.getAnnotation(GroupArgument.class);
                    if (argument != null) {
                        String name = argument.value().equals(ARGUMENT_NAME_AS_FIELD_NAME) ? field.getName() : argument.value();
                        if (fields.containsKey(name)) {
                            throw new Error("Duplicate field for name: '" + name + "'");
                        }
                        if (!name.isEmpty() && !GroupParser.isValidIdentifier(name)) {
                            throw BennuCoreDomainException.invalidGroupIdentifier(name);
                        }
                        fields.put(name, field);
                        field.setAccessible(true);
                    }
                }
                current = current.getSuperclass();
            }
        }

        private Constructor<? extends CustomGroup> findConstructor(Class<? extends CustomGroup> type) {
            try {
                Constructor<? extends CustomGroup> constructor = type.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor;
            } catch (NoSuchMethodException e) {
                throw new Error("Custom Group type " + type.getName() + " does not declare a default constructor", e);
            }
        }

        public CustomGroup parse(Map<String, List<String>> arguments) {
            try {
                CustomGroup group = constructor.newInstance();
                if (arguments != null) {
                    for (Entry<String, List<String>> entry : arguments.entrySet()) {
                        Field field = fields.get(entry.getKey());
                        if (field != null) {
                            Collection<String> args = entry.getValue();
                            if (args != null && !args.isEmpty()) {
                                field.set(group, parseField(field, args));
                            }
                        } else {
                            throw new Error("No field found with name '" + entry.getKey() + "' on group ");
                        }
                    }
                }
                return group;
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new Error(e);
            }
        }

        private Object parseField(Field field, Collection<String> fieldArgs) throws InstantiationException,
                IllegalAccessException {
            if (Collection.class.isAssignableFrom(field.getType())) {
                ArgumentParser<Object> parser = parsers.get(determineCollectionType(field));
                Collection<Object> value;
                if (Set.class.isAssignableFrom(field.getType())) {
                    value = new HashSet<>();
                } else if (List.class.isAssignableFrom(field.getType())) {
                    value = new ArrayList<>();
                } else {
                    throw new Error("CustomGroups cannot handle field arguments of types other than Set, List or Array. Given: "
                            + field.getType().getName());
                }
                for (String arg : fieldArgs) {
                    value.add(parser.parse(arg));
                }
                return value;
            } else if (field.getType().isArray()) {
                ArgumentParser<Object> parser = parsers.get(field.getType().getComponentType());
                Object[] value = new Object[fieldArgs.size()];
                int i = 0;
                for (String arg : fieldArgs) {
                    value[i++] = parser.parse(arg);
                }
                return value;
            }
            if (fieldArgs.size() == 1) {
                ArgumentParser<Object> parser = parsers.get(field.getType());
                return parser.parse(fieldArgs.iterator().next());
            }
            throw new Error("Invalid argument: " + field.getType().getName() + " " + field.getName());
        }

        private Class<?> determineCollectionType(Field field) {
            ParameterizedType fieldElementType = (ParameterizedType) field.getGenericType();
            Type[] args = fieldElementType.getActualTypeArguments();
            if (args.length == 1) {
                return (Class<?>) args[0];
            }
            throw new Error("Could not determine element type of collection typed argument: " + field);
        }

        public String getExpression(CustomGroup group) {
            try {
                List<String> arguments = new ArrayList<>();
                for (String name : fields.keySet()) {
                    List<String> values = serializeField(fields.get(name), group);
                    if (!values.isEmpty()) {
                        String serialized = String.join(", ", values);
                        if (Strings.isNullOrEmpty(name)) {
                            arguments.add(serialized);
                        } else if (values.size() == 1) {
                            arguments.add(name + "=" + serialized);
                        } else {
                            arguments.add(name + "=[" + serialized + "]");
                        }
                    }
                }
                return arguments.isEmpty() ? operator : operator + "(" + String.join(", ", arguments) + ")";
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new Error(e);
            }
        }

        private List<String> serializeField(Field field, CustomGroup group) throws IllegalArgumentException,
                IllegalAccessException {
            List<String> serialized = new ArrayList<>();
            if (Iterable.class.isAssignableFrom(field.getType())) {
                ArgumentParser<Object> parser = parsers.get(determineCollectionType(field));
                Iterable<?> iterable = (Iterable<?>) field.get(group);
                if (iterable != null) {
                    for (Object object : iterable) {
                        serialized.add(parser.serialize(object));
                    }
                }
            } else if (field.getType().isArray()) {
                ArgumentParser<Object> parser = parsers.get(field.getType().getComponentType());
                Object[] value = (Object[]) field.get(group);
                if (value != null) {
                    for (Object object : value) {
                        serialized.add(parser.serialize(object));
                    }
                }
            } else {
                Object value = field.get(group);
                if (value != null) {
                    ArgumentParser<Object> parser = parsers.get(field.getType());
                    serialized.add(parser.serialize(value));
                }
            }
            serialized.replaceAll(value -> GroupParser.isValidIdentifier(value) ? value : "'" + value.replace("'", "\\'") + "'");
            return serialized;
        }
    }

    /*
     * Here I chose using a TreeMap instead of a HashMap, as the String instances that are
     * passed in the group parsing process are never reused, thus the string's hashCode must
     * be continuously recomputed.
     */
    private static final TreeMap<String, CustomGroupMetadata> metadata = new TreeMap<>();

    private static final Map<Class<?>, ArgumentParser<Object>> parsers = new HashMap<>();

    private static final CustomGroupMetadata resolveMetadata(String operator) {
        CustomGroupMetadata group = metadata.get(operator);
        if (group == null) {
            throw BennuCoreDomainException.groupParsingNoGroupForOperator(operator);
        }
        return group;
    }

    public static CustomGroup parse(String operator, Map<String, List<String>> arguments) {
        return resolveMetadata(operator).parse(arguments);
    }

    public static String getExpression(CustomGroup group) {
        GroupOperator operator = group.getClass().getAnnotation(GroupOperator.class);
        return resolveMetadata(operator.value()).getExpression(group);
    }

    public static void registerCustomGroup(Class<? extends CustomGroup> type) {
        CustomGroupMetadata m = new CustomGroupMetadata(type);
        if (metadata.containsKey(m.operator)) {
            throw new Error("CustomGroup: duplicate operator name: " + m.operator);
        }
        metadata.put(m.operator, m);
    }

    public static void registerArgumentParser(Class<? extends ArgumentParser<?>> parserType) {
        try {
            @SuppressWarnings("unchecked")
            ArgumentParser<Object> parser = (ArgumentParser<Object>) parserType.newInstance();
            if (parsers.containsKey(parser.type())) {
                throw new Error("GroupArgumentParser: duplicate parser for type: " + parser.type().getName());
            }
            parsers.put(parser.type(), parser);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new Error("GroupArgumentParser: could not instantiate parser of type: " + parserType.getName());
        }
    }

    @GroupArgumentParser
    public static class ByteParser implements ArgumentParser<Byte> {
        @Override
        public Byte parse(String argument) {
            return Byte.valueOf(argument);
        }

        @Override
        public String serialize(Byte argument) {
            return argument.toString();
        }

        @Override
        public Class<Byte> type() {
            return Byte.class;
        }
    }

    @GroupArgumentParser
    public static class ShortParser implements ArgumentParser<Short> {
        @Override
        public Short parse(String argument) {
            return Short.valueOf(argument);
        }

        @Override
        public String serialize(Short argument) {
            return argument.toString();
        }

        @Override
        public Class<Short> type() {
            return Short.class;
        }
    }

    @GroupArgumentParser
    public static class IntegerParser implements ArgumentParser<Integer> {
        @Override
        public Integer parse(String argument) {
            return Integer.valueOf(argument);
        }

        @Override
        public String serialize(Integer argument) {
            return argument.toString();
        }

        @Override
        public Class<Integer> type() {
            return Integer.class;
        }
    }

    @GroupArgumentParser
    public static class LongParser implements ArgumentParser<Long> {
        @Override
        public Long parse(String argument) {
            return Long.valueOf(argument);
        }

        @Override
        public String serialize(Long argument) {
            return argument.toString();
        }

        @Override
        public Class<Long> type() {
            return Long.class;
        }
    }

    @GroupArgumentParser
    public static class FloatParser implements ArgumentParser<Float> {
        @Override
        public Float parse(String argument) {
            return Float.valueOf(argument);
        }

        @Override
        public String serialize(Float argument) {
            return argument.toString();
        }

        @Override
        public Class<Float> type() {
            return Float.class;
        }
    }

    @GroupArgumentParser
    public static class DoubleParser implements ArgumentParser<Double> {
        @Override
        public Double parse(String argument) {
            return Double.valueOf(argument);
        }

        @Override
        public String serialize(Double argument) {
            return argument.toString();
        }

        @Override
        public Class<Double> type() {
            return Double.class;
        }
    }

    @GroupArgumentParser
    public static class BooleanParser implements ArgumentParser<Boolean> {
        @Override
        public Boolean parse(String argument) {
            return Boolean.valueOf(argument);
        }

        @Override
        public String serialize(Boolean argument) {
            return argument.toString();
        }

        @Override
        public Class<Boolean> type() {
            return Boolean.class;
        }
    }

    @GroupArgumentParser
    public static class CharacterParser implements ArgumentParser<Character> {
        @Override
        public Character parse(String argument) {
            if (argument.length() == 1) {
                return Character.valueOf(argument.charAt(0));
            }
            throw new IllegalArgumentException();
        }

        @Override
        public String serialize(Character argument) {
            return argument.toString();
        }

        @Override
        public Class<Character> type() {
            return Character.class;
        }
    }

    @GroupArgumentParser
    public static class StringParser implements ArgumentParser<String> {
        @Override
        public String parse(String argument) {
            return argument;
        }

        @Override
        public String serialize(String argument) {
            return argument;
        }

        @Override
        public Class<String> type() {
            return String.class;
        }
    }

    @GroupArgumentParser
    public static class DateTimeParser implements ArgumentParser<DateTime> {
        @Override
        public DateTime parse(String argument) {
            return DateTime.parse(argument);
        }

        @Override
        public String serialize(DateTime argument) {
            return argument.toString();
        }

        @Override
        public Class<DateTime> type() {
            return DateTime.class;
        }
    }

}
