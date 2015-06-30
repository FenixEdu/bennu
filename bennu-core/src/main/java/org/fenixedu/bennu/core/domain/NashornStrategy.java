package org.fenixedu.bennu.core.domain;

import java.util.Objects;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * A {@link NashornStrategy} is a special value-type that allows using JavaScript to store interface implementations.
 * 
 * The implementation is provided by Nashorn. For a reference of Nashorn, see <a
 * href="http://docs.oracle.com/javase/8/docs/technotes/guides/scripting/nashorn/">Oracle's
 * Nashorn Guide</a>.
 * 
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
public final class NashornStrategy<T> {

    private final String code;

    private final Class<?> type;

    private final T strategy;

    /**
     * Create a new {@link NashornStrategy} of the given type, implemented by the given code.
     * 
     * @param type
     *            The interface type to implement.
     * @param code
     *            The code that implements the interface.
     * 
     * @throws IllegalArgumentException
     *             If {@code type} is not an interface, or if the given JavaScript code is invalid, or doesn't implement the given
     *             interface.
     * @throws NullPointerException
     *             If either argument is null.
     */
    public NashornStrategy(Class<?> type, String code) {
        this.type = Objects.requireNonNull(type, "type");
        this.code = Objects.requireNonNull(code, "code");

        if (!type.isInterface()) {
            throw new IllegalArgumentException("Type " + type.getName() + " is not an interface!");
        }

        this.strategy = getImplementation(code, type);

        if (this.strategy == null) {
            throw new IllegalArgumentException("The given code does not implement the interface!");
        }

    }

    @SuppressWarnings("unchecked")
    private T getImplementation(String code, Class<?> type) {
        try {
            ScriptEngineManager engineManager = new ScriptEngineManager();
            ScriptEngine engine = engineManager.getEngineByName("nashorn");
            if (engine == null) {
                throw new Error("A javascript engine could not be found!");
            }
            Invocable invocable = (Invocable) engine;
            engine.eval(code);
            return (T) invocable.getInterface(type);
        } catch (ScriptException e) {
            throw new IllegalArgumentException("Could not compile Javascript code!", e);
        }
    }

    /**
     * Returns the code that implements the interface.
     * 
     * @return
     *         The JavaScript code that implements the interface
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the implemented interface type.
     * 
     * @return
     *         The implemented interface type
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * Returns the instance of the interface type, implemented by the
     * given JavaScript code.
     * 
     * @return
     *         The instance implemented by the given code
     */
    public T getStrategy() {
        return strategy;
    }

}
