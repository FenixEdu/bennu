package pt.ist.bennu.dispatch.model;

import pt.ist.bennu.core.i18n.InternationalString;

public class MultiLanguageDetails extends Details {

    private static final long serialVersionUID = 1L;

    private final InternationalString title;
    private final InternationalString description;

    public MultiLanguageDetails(InternationalString title, InternationalString description) {
        super();
        this.title = title;
        this.description = description;
    }

    @Override
    public InternationalString getTitle() {
        return title;
    }

    @Override
    public InternationalString getDescription() {
        return description;
    }

}
