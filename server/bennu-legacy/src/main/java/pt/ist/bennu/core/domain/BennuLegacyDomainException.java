package pt.ist.bennu.core.domain;

import javax.ws.rs.core.Response.Status;

import pt.ist.bennu.core.domain.exceptions.DomainException;

public class BennuLegacyDomainException extends DomainException {
    protected static final String BUNDLE = "resources.SotisCoreResources";

    protected BennuLegacyDomainException(String key, String... args) {
        super(BUNDLE, key, args);
    }

    protected BennuLegacyDomainException(Status status, String key, String... args) {
        super(status, BUNDLE, key, args);
    }

    protected BennuLegacyDomainException(Throwable cause, String key, String... args) {
        super(cause, BUNDLE, key, args);
    }

    protected BennuLegacyDomainException(Throwable cause, Status status, String key, String... args) {
        super(cause, status, BUNDLE, key, args);
    }

    public static BennuLegacyDomainException roleAlreadyExists(String role) {
        return new BennuLegacyDomainException("role.already.exists", role);
    }

    public static BennuLegacyDomainException userAlreadyHasSingleUserGroup() {
        return new BennuLegacyDomainException("user.already.has.single.user.group");
    }
}
