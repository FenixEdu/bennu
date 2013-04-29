package pt.ist.bennu.dispatch.model;

import java.io.Serializable;

import pt.ist.bennu.core.i18n.InternationalString;

public abstract class Details implements Serializable {

    private static final long serialVersionUID = 1L;

    public abstract InternationalString getTitle();

    public abstract InternationalString getDescription();
}
