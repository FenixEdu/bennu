package pt.ist.bennu.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.annotation.ConfigurationProperty;

import com.google.common.reflect.AbstractInvocationHandler;
import com.google.common.reflect.Reflection;

public class ConfigurationInvocationHandler extends AbstractInvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationInvocationHandler.class);

    public static final String NULL_DEFAULT = "AbQAGOvdWgQgHLOH5hSk";

    protected static final Properties properties = new Properties();

    private static final Map<Class<?>, Object> configs = new HashMap<>();

    static {
        try (InputStream inputStream = ConfigurationInvocationHandler.class.getResourceAsStream("/configuration.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                logger.warn("configuration.properties not found in classpath. Relying on default values");
            }
        } catch (IOException e) {
            throw new Error("configuration.properties could not be read.", e);
        }
    }

    private final Map<String, Object> cache = new HashMap<>();

    @Override
    protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if (cache.containsKey(name)) {
            return cache.get(name);
        }
        Class<?> type = method.getReturnType();
        ConfigurationProperty property = method.getAnnotation(ConfigurationProperty.class);
        if (property != null) {
            if (property.key().contains("*")) {
                Map<String, String> value = obtainValueMap(property.key(), property.defaultValue());
                logger.debug("Setting {} to {}", name, value);
                cache.put(name, value);
                return value;
            }
            Object value = obtainValue(type, property.key(), property.defaultValue());
            logger.debug("Setting {} to {}", name, value);
            cache.put(name, value);
            return value;
        }
        throw new Error("Method " + name + "must be annotated with @ConfigurationProperty");
    }

    private Object obtainValue(Class<?> type, String key, String defaultValue) {
        Object value = properties.getProperty(key, defaultValue.equals(NULL_DEFAULT) ? null : defaultValue);
        if (value == null) {
            return null;
        }
        if (Boolean.class.isAssignableFrom(type)) {
            value = Boolean.parseBoolean((String) value);
        } else if (Byte.class.isAssignableFrom(type)) {
            value = Byte.parseByte((String) value);
        } else if (Short.class.isAssignableFrom(type)) {
            value = Short.parseShort((String) value);
        } else if (Integer.class.isAssignableFrom(type)) {
            value = Integer.parseInt((String) value);
        } else if (Long.class.isAssignableFrom(type)) {
            value = Long.parseLong((String) value);
        } else if (Float.class.isAssignableFrom(type)) {
            value = Float.parseFloat((String) value);
        } else if (Double.class.isAssignableFrom(type)) {
            value = Double.parseDouble((String) value);
        }
        return value;
    }

    private Map<String, String> obtainValueMap(String patternString, String defaultValue) {
        Map<String, String> value = new HashMap<>();
        Pattern pattern = Pattern.compile(patternString.replace("*", "(.*)"));
        for (Object property : properties.keySet()) {
            String propertyName = property.toString();
            Matcher matcher = pattern.matcher(propertyName);
            if (matcher.matches()) {
                String key = matcher.group(1);
                value.put(key, properties.getProperty(propertyName, defaultValue.equals(NULL_DEFAULT) ? null : defaultValue));
            }
        }
        return value;
    }

    public static Properties rawProperties() {
        return (Properties) properties.clone();
    }

    public static <T> T getConfiguration(Class<T> propertiesType) {
        if (!configs.containsKey(propertiesType)) {
            configs.put(propertiesType, Reflection.newProxy(propertiesType, new ConfigurationInvocationHandler()));
        }
        return (T) configs.get(propertiesType);
    }
}
