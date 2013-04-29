package pt.ist.bennu.dispatch.model;

import pt.ist.bennu.core.i18n.BundleUtil;
import pt.ist.bennu.core.i18n.InternationalString;

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
    public InternationalString getDescription() {
        return BundleUtil.getInternationalString(bundle, description);
    }

    @Override
    public InternationalString getTitle() {
        return BundleUtil.getInternationalString(bundle, title);
    }

}
