package pt.ist.bennu.dispatch.model;

import java.io.Serializable;

import pt.ist.bennu.core.util.MultiLanguageString;

public abstract class Details implements Serializable {

    private static final long serialVersionUID = 1L;

    public abstract MultiLanguageString getTitle();

    public abstract MultiLanguageString getDescription();
}
