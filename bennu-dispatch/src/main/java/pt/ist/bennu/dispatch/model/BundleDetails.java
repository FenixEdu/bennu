package pt.ist.bennu.dispatch.model;

import pt.ist.bennu.core.util.BundleUtil;
import pt.ist.bennu.core.util.MultiLanguageString;

public class BundleDetails extends Details {

    private static final long serialVersionUID = 1L;

    private final String bundle;

    private final String title;

    private final String description;

    public BundleDetails(String bundle, String title, String description) {
        this.bundle = bundle;
        this.title = title;
        this.description = description;
    }

    @Override
    public MultiLanguageString getDescription() {
        return BundleUtil.getMultilanguageString(bundle, description);
    }

    @Override
    public MultiLanguageString getTitle() {
        return BundleUtil.getMultilanguageString(bundle, title);
    }

}
