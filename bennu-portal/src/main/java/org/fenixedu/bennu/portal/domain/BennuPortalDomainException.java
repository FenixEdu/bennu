package org.fenixedu.bennu.portal.domain;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;

public class BennuPortalDomainException extends DomainException {

    private static final long serialVersionUID = 7216962354662319331L;

    protected BennuPortalDomainException(String key, String... args) {
        super("resources.BennuPortalResources", key, args);
    }

    public static BennuPortalDomainException cannotDeleteRootContainer() {
        return new BennuPortalDomainException("error.cannot.delete.root.container");
    }

    public static BennuPortalDomainException childWithPathAlreadyExists(String path) {
        return new BennuPortalDomainException("error.child.with.path.already.exists", path);
    }

}
