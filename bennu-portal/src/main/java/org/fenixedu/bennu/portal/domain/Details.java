package org.fenixedu.bennu.portal.domain;

import java.io.Serializable;

import org.fenixedu.commons.i18n.LocalizedString;

public abstract class Details implements Serializable {

    private static final long serialVersionUID = 1L;

    public abstract LocalizedString getTitle();

    public abstract LocalizedString getDescription();
}
