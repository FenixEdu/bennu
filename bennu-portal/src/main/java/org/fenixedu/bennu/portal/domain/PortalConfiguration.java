package org.fenixedu.bennu.portal.domain;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public final class PortalConfiguration extends PortalConfiguration_Base {
    private PortalConfiguration() {
        super();
        setRoot(Bennu.getInstance());
        setApplicationTitle(new LocalizedString(I18N.getLocale(), "Application Title"));
        setApplicationSubTitle(new LocalizedString(I18N.getLocale(), "Application Subtitle"));
        setApplicationCopyright(new LocalizedString(I18N.getLocale(), "Organization Copyright"));
        setHtmlTitle(getApplicationTitle());
        setTheme("default");
        setMenu(new MenuItem());
    }

    public static PortalConfiguration getInstance() {
        if (Bennu.getInstance().getConfiguration() == null) {
            return initialize();
        }
        return Bennu.getInstance().getConfiguration();
    }

    @Atomic(mode = TxMode.WRITE)
    private static PortalConfiguration initialize() {
        if (Bennu.getInstance().getConfiguration() == null) {
            return new PortalConfiguration();
        }
        return Bennu.getInstance().getConfiguration();
    }

    @Override
    public String getSupportEmailAddress() {
        return super.getSupportEmailAddress() != null ? super.getSupportEmailAddress() : CoreConfiguration.getConfiguration()
                .defaultSupportEmailAddress();
    }

    @Atomic(mode = TxMode.WRITE)
    public void delete() {
        getMenu().delete();
        setRoot(null);
        deleteDomainObject();
    }
}
