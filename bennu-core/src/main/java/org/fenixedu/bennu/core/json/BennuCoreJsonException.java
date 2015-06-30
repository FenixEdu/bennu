package org.fenixedu.bennu.core.json;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;

public class BennuCoreJsonException extends BennuCoreDomainException {
    private static final long serialVersionUID = 895004129903164510L;

    public static BennuCoreJsonException valueIsNotAnInteger(String value) {
        return new BennuCoreJsonException("error.bennu.core.valueIsNotAnInteger", value);
    }

    protected BennuCoreJsonException(String key, String... args) {
        super(key, args);
    }

    protected BennuCoreJsonException(Status status, String key, String... args) {
        super(status, key, args);
    }

    protected BennuCoreJsonException(Throwable cause, String key, String... args) {
        super(cause, key, args);
    }

    protected BennuCoreJsonException(Throwable cause, Status status, String key, String... args) {
        super(cause, status, key, args);
    }
}
