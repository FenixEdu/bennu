package org.fenixedu.bennu.toolkit.components;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.fenixedu.commons.i18n.LocalizedString;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public abstract class Component {

    private final static Logger LOGGER = LoggerFactory.getLogger(Component.class);

    private static Map<String, Component> COMPONENTS = Maps.newHashMap();

    public abstract Element process(Element element);

    public static Component getComponent(String key) {
        return Component.COMPONENTS.get(key);
    }

    public static Collection<Component> getComponents() {
        return COMPONENTS.values();
    }

    public static String process(String origin) {
        Document doc = Jsoup.parse(origin);
        Elements components = doc.select("[bennu-component]");

        for (Element component : components) {
            String key = component.attr("bennu-component");
            Optional.ofNullable(COMPONENTS.get(key)).ifPresent(x -> component.replaceWith(x.process(component)));
        }

        return doc.toString();
    }

    public static void register(Class<?> type) {
        ToolkitComponent annotation = type.getAnnotation(ToolkitComponent.class);

        try {
            COMPONENTS.put(annotation.key(), (Component) type.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            LOGGER.error("Error while instancing a toolkit component", e);
        }
    }

    public static LocalizedString process(LocalizedString origin) {
        return origin.map(Component::process);
    }

}
