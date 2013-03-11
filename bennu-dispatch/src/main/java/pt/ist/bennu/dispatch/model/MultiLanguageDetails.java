package pt.ist.bennu.dispatch.model;

import pt.ist.bennu.core.util.MultiLanguageString;

public class MultiLanguageDetails extends Details {

    private static final long serialVersionUID = 1L;

    private final MultiLanguageString title;
    private final MultiLanguageString description;

    public MultiLanguageDetails(MultiLanguageString title, MultiLanguageString description) {
        super();
        this.title = title;
        this.description = description;
    }

    @Override
    public MultiLanguageString getTitle() {
        return title;
    }

    @Override
    public MultiLanguageString getDescription() {
        return description;
    }

}
