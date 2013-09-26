package pt.ist.bennu.json;

public class DefaultNotAvailableException extends RuntimeException {

    private static final long serialVersionUID = -6263256235373928304L;

    public DefaultNotAvailableException(Class<?> objectClass) {
        super(objectClass.getName());
    }

}
