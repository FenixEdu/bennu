package pt.ist.bennu.portal.domain.exception;

import pt.ist.bennu.core.domain.exceptions.DomainException;

public class MenuNotAvailableException extends DomainException {

    private static final long serialVersionUID = 1L;

    public MenuNotAvailableException() {
        super("x", "y");
    }

}
