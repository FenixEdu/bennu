package org.fenixedu.bennu.core.groups;

/**
 * API for argument parsers. for any parser {@code parse(serialize(t)) == t} must be true.
 * 
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 * @param <T> The type being parsed.
 */
public interface ArgumentParser<T> {
    /**
     * Parse the value from it's serialized form. The argument is never null or empty.
     * 
     * @param argument The serialized version of the type.
     * @return
     */
    public T parse(String argument);

    /**
     * Serialize the argument. The argument is never null.
     * 
     * @param argument The instance to serialize
     * @return The serialized version of the type.
     */
    public String serialize(T argument);

    /**
     * @return type being parsed.
     */
    public Class<T> type();
}