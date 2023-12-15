package org.fenixedu.bennuAdmin.util;

import org.fenixedu.bennu.BennuAdminConfiguration;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.http.HttpStatus;

public class BennuAdminError extends Error {
    public final HttpStatus status;
    public final String[] args;
    public final Throwable cause;
    public LocalizedString description;
    public String bundle = BennuAdminConfiguration.BUNDLE;

    public BennuAdminError(final HttpStatus status, final String message, final String... args) {
        super(message);
        this.status = status;
        this.args = args;
        this.cause = null;
    }

    public BennuAdminError(final String bundle, final HttpStatus status, final String message, final String... args) {
        this(status, message, args);
        this.bundle = bundle;
    }

    public BennuAdminError(final HttpStatus status, final Throwable cause, final String message, final String... args) {
        super(message, cause);
        this.status = status;
        this.args = args;
        this.cause = cause;
    }

    public BennuAdminError(final String bundle, final HttpStatus status, final Throwable cause, final String message, final String... args) {
        this(status, cause, message, args);
        this.bundle = bundle;
    }
}
