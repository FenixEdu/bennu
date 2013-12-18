package pt.ist.bennu.portal.domain;

import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.bennu.core.i18n.BundleUtil;

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
    public LocalizedString getDescription() {
        return BundleUtil.getLocalizedString(bundle, description);
    }

    @Override
    public LocalizedString getTitle() {
        return BundleUtil.getLocalizedString(bundle, title);
    }

}
