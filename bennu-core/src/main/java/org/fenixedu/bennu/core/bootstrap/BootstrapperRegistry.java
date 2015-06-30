package org.fenixedu.bennu.core.bootstrap;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrap;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrapper;
import org.fenixedu.bennu.core.bootstrap.annotations.Field;
import org.fenixedu.bennu.core.bootstrap.annotations.Section;
import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Central place for {@link Bootstrapper} to register themselves.
 * 
 * Upon application startup, all the Bootstrappers should be registered here.
 * 
 */
public class BootstrapperRegistry {
    private static final String UNDECLARED_SECTION_ERROR = "The parameters for '%s' must be declared as a @Bootstrapper sections";
    private static final String BOOTSTRAP_PARAMETER_ERROR = "All the parameters for '%s' must have a @Section annotation";
    private static final String STATIC_METHOD_ERROR = "Bootstrap method '%s' must be static.";

    private static Set<Class<?>> bootstrappers = Sets.newConcurrentHashSet();

    /**
     * Registers the {@link BootstrapFilter} on a given {@link ServletContext}.
     * 
     * @param servletContext
     *            The servletContext where the filter will be registered
     */
    @Atomic(mode = TxMode.READ)
    public static void registerBootstrapFilter(ServletContext servletContext) {
        if (Bennu.getInstance().getUserSet().isEmpty()) {
            FilterRegistration registration = servletContext.addFilter("BootstrapFilter", BootstrapFilter.class);
            registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
        }
    }

    /**
     * Registers a new {@link Bootstrapper}.
     * 
     * @param bootstrapper
     *            The bootstrapper to register
     * @throws IllegalArgumentException
     *             If the bootstrapper method is not static or the sections are not declared as such.
     */
    public static void register(Class<?> bootstrapper) {
        validateBootstrapper(bootstrapper);
        bootstrappers.add(bootstrapper);
    }

    /**
     * Returns all classes registered with a {@link Bootstrapper} annotation.
     * 
     * @return all the registered bootstrapper classes.
     */
    public static List<Class<?>> getBootstrappers() {
        return new BootstrappersSorter(bootstrappers).sort();
    }

    /**
     * Returns all methods registered with a {@link Bootstrap} annotation.
     * 
     * @return all the registered bootstrap methods.
     */
    public static List<Method> getBootstrapMethods() {
        List<Method> methods = Lists.newArrayList();
        for (Class<?> bootstrapper : getBootstrappers()) {
            for (Method bootstrapMethod : bootstrapper.getMethods()) {
                if (bootstrapMethod.isAnnotationPresent(Bootstrap.class)) {
                    methods.add(bootstrapMethod);
                }
            }
        }
        return methods;
    }

    /**
     * Returns all classes registered with a {@link Section} annotation.
     * 
     * @return all the registered section classes.
     */
    public static List<Class<?>> getSections() {
        List<Class<?>> sections = Lists.newArrayList();
        for (Class<?> bootstrapper : getBootstrappers()) {
            for (Class<?> section : getSections(bootstrapper)) {
                if (!sections.contains(section)) {
                    sections.add(section);
                }
            }
        }
        return sections;
    }

    /**
     * Returns all the section classes of a given {@link Bootstrapper}.
     * 
     * @param bootstrapper
     *            the bootstrapper class
     * 
     * @return all the registered section classes of a the bootstrapper.
     */
    public static List<Class<?>> getSections(Class<?> bootstrapper) {
        return Lists.newArrayList(bootstrapper.getAnnotation(Bootstrapper.class).sections());
    }

    /**
     * Returns all the section methods with a {@link Field} annotation
     * 
     * @param section
     *            The section class.
     * 
     * @return all the registered fields for a given section.
     */
    public static List<Method> getSectionFields(Class<?> section) {
        List<Method> fields = Lists.newArrayList();
        for (Method method : section.getMethods()) {
            if (method.isAnnotationPresent(Field.class)) {
                fields.add(method);
            }
        }
        Collections.sort(fields, FIELDS_COMPARATOR);
        return fields;
    }

    private static void validateBootstrapper(Class<?> bootstrapperClass) {
        for (Method bootstrapMethod : bootstrapperClass.getMethods()) {
            if (bootstrapMethod.isAnnotationPresent(Bootstrap.class)) {
                boolean isStaticMethod = Modifier.isStatic(bootstrapMethod.getModifiers());
                String methodName = bootstrapMethod.getDeclaringClass().getName() + "." + bootstrapMethod.getName();
                checkArgument(isStaticMethod, String.format(STATIC_METHOD_ERROR, methodName));

                Bootstrapper bootstrapper = bootstrapMethod.getDeclaringClass().getAnnotation(Bootstrapper.class);
                for (Class<?> section : bootstrapper.sections()) {
                    checkArgument(section.isAnnotationPresent(Section.class),
                            String.format(BOOTSTRAP_PARAMETER_ERROR, methodName));
                }

                boolean allDeclaredSections =
                        Lists.newArrayList(bootstrapper.sections()).containsAll(
                                Lists.newArrayList(bootstrapMethod.getParameterTypes()));
                checkArgument(allDeclaredSections, String.format(UNDECLARED_SECTION_ERROR, methodName));
            }
        }
    }

    private static final Comparator<Method> FIELDS_COMPARATOR = new Comparator<Method>() {
        @Override
        public int compare(Method method1, Method method2) {
            return method1.getAnnotation(Field.class).order() - method2.getAnnotation(Field.class).order();
        }
    };

    private static class BootstrappersSorter {
        private Set<Class<?>> visited = Sets.newHashSet();
        private List<Class<?>> ordered = Lists.newArrayList();
        private Set<Class<?>> unnordered;

        public BootstrappersSorter(Set<Class<?>> unnorderedBootstrappers) {
            this.unnordered = Sets.newHashSet(unnorderedBootstrappers);
        }

        private void dfs(Class<?> node) {
            unnordered.remove(node);
            visited.add(node);
            for (Class<?> neighboorClass : node.getAnnotation(Bootstrapper.class).after()) {
                if (!visited.contains(neighboorClass) && !ordered.contains(neighboorClass)) {
                    dfs(neighboorClass);
                }
            }
            visited.remove(node);
            ordered.add(node);
        }

        public List<Class<?>> sort() {
            while (!unnordered.isEmpty()) {
                dfs(unnordered.iterator().next());
            }
            return ordered;
        }
    }

}
