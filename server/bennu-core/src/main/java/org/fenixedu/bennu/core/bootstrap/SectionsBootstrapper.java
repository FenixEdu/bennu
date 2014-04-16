package org.fenixedu.bennu.core.bootstrap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrap;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.gson.JsonObject;

public class SectionsBootstrapper {

    /**
     * Invokes all the declared methods with a {@link Bootstrap} annotation.
     * 
     * @param json
     *            the json that for each declared {@link Field} maps its key on its value.
     * 
     * @return all the errors that occurred on the bootstrap process
     */
    @Atomic(mode = TxMode.WRITE)
    public static Collection<BootstrapError> bootstrapAll(JsonObject json) throws Exception {
        List<BootstrapError> errors = new ArrayList<>();
        Class<?>[] sections = BootstrapperRegistry.getSections().toArray(new Class<?>[0]);
        Object superSection = SectionInvocationHandler.newInstance(json, sections);
        for (Method method : BootstrapperRegistry.getBootstrapMethods()) {
            Object[] args = new Object[method.getParameterTypes().length];
            Arrays.fill(args, superSection);
            errors.addAll(invokeIt(method, args));
        }
        return errors;
    }

    private static List<BootstrapError> invokeIt(Method method, Object[] args) throws Exception {
        List<BootstrapError> errors = new ArrayList<>();
        Object result = method.invoke(null, args);
        if (result != null) {
            if (Collection.class.isAssignableFrom(result.getClass())) {
                for (Object item : (Collection<?>) result) {
                    if (BootstrapError.class.isAssignableFrom(item.getClass())) {
                        errors.add((BootstrapError) item);
                    }
                }
            } else if (BootstrapError.class.isAssignableFrom(result.getClass())) {
                errors.add((BootstrapError) result);
            }
        }

        return errors;
    }

}
