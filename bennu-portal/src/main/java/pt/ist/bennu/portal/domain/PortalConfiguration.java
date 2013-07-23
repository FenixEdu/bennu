package pt.ist.bennu.portal.domain;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.dsi.commons.i18n.I18N;
import pt.ist.dsi.commons.i18n.LocalizedString;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class PortalConfiguration extends PortalConfiguration_Base {
    public PortalConfiguration() {
        super();
        setApplicationTitle(new LocalizedString(I18N.getLocale(), "Application Title"));
        setApplicationSubTitle(new LocalizedString(I18N.getLocale(), "Application Subtitle"));
        setApplicationCopyright(new LocalizedString(I18N.getLocale(), "Organization Copyright"));
        setHtmlTitle(getApplicationTitle());
        setTheme("dot");
        setMenu(new MenuItem());
    }

    public static PortalConfiguration getInstance() {
        if (Bennu.getInstance().getConfiguration() == null) {
            initialize();
        }
        return Bennu.getInstance().getConfiguration();
    }

    @Atomic(mode = TxMode.WRITE)
    protected static void initialize() {
        PortalConfiguration config = new PortalConfiguration();
        config.setRoot(Bennu.getInstance());
    }
}
