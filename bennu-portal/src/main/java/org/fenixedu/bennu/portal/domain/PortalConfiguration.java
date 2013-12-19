package org.fenixedu.bennu.portal.domain;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class PortalConfiguration extends PortalConfiguration_Base {
    public PortalConfiguration() {
        super();
        setApplicationTitle(new LocalizedString(I18N.getLocale(), "Application Title"));
        setApplicationSubTitle(new LocalizedString(I18N.getLocale(), "Application Subtitle"));
        setApplicationCopyright(new LocalizedString(I18N.getLocale(), "Organization Copyright"));
        setHtmlTitle(getApplicationTitle());
        setTheme("default");
        setMenu(new MenuItem());
    }

    public static PortalConfiguration getInstance() {
        if (Bennu.getInstance().getConfiguration() == null) {
            initialize();
        }
        return Bennu.getInstance().getConfiguration();
    }

    @Override
    public String getSupportEmailAddress() {
        return super.getSupportEmailAddress() != null ? super.getSupportEmailAddress() : CoreConfiguration
                .getConfiguration().defaultSupportEmailAddress();
    }

    @Atomic(mode = TxMode.WRITE)
    protected static void initialize() {
        PortalConfiguration config = new PortalConfiguration();
        config.setRoot(Bennu.getInstance());
    }

    @Atomic(mode = TxMode.WRITE)
    public void delete() {
        setRoot(null);
        deleteDomainObject();
    }
}
