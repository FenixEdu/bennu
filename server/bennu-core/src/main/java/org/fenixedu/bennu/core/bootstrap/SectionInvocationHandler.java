package org.fenixedu.bennu.core.bootstrap;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.google.common.reflect.AbstractInvocationHandler;
import com.google.gson.JsonObject;

public class SectionInvocationHandler extends AbstractInvocationHandler {

    private final JsonObject json;

    private SectionInvocationHandler(JsonObject json) {
        this.json = json;
    }

    /**
     * Creates a object that acts has proxy for all the given interfaces
     * 
     * @param json
     *            the json that for each declared {@link Field} maps its key on its value.
     * @param interfaces
     *            the classes that the proxy will be capable to handle
     * @return proxy
     *         a new instance of the proxy that is capable of handling all the calls to the interfaces methods having into
     *         consideration the given field values given as json.
     */
    public static Object newInstance(JsonObject json, Class<?>... interfaces) {
        return Proxy.newProxyInstance(SectionInvocationHandler.class.getClassLoader(), interfaces, new SectionInvocationHandler(
                json));
    }

    @Override
    protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
        String fieldName = method.getDeclaringClass().getName() + "." + method.getName();
        return json.has(fieldName) ? json.get(fieldName).getAsString() : "";
    }
}
