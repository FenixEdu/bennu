package org.fenixedu.bennu.portal.domain.exception;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;

public class MenuNotAvailableException extends DomainException {

    private static final long serialVersionUID = 1L;

    public MenuNotAvailableException() {
        super("x", "y");
    }

}
