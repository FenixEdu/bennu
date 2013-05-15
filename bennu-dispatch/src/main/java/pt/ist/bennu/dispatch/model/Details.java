package pt.ist.bennu.dispatch.model;

import java.io.Serializable;

import pt.ist.dsi.commons.i18n.LocalizedString;

public abstract class Details implements Serializable {

    private static final long serialVersionUID = 1L;

    public abstract LocalizedString getTitle();

    public abstract LocalizedString getDescription();
}
