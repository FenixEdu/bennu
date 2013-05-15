package pt.ist.bennu.dispatch.model;

import pt.ist.dsi.commons.i18n.LocalizedString;

public class MultiLanguageDetails extends Details {

    private static final long serialVersionUID = 1L;

    private final LocalizedString title;
    private final LocalizedString description;

    public MultiLanguageDetails(LocalizedString title, LocalizedString description) {
        super();
        this.title = title;
        this.description = description;
    }

    @Override
    public LocalizedString getTitle() {
        return title;
    }

    @Override
    public LocalizedString getDescription() {
        return description;
    }

}
