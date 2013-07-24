package pt.ist.bennu.core.domain;

import pt.ist.bennu.portal.domain.MenuItem;
import pt.ist.bennu.portal.domain.PortalConfiguration;
import pt.ist.dsi.commons.i18n.LocalizedString;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class VirtualHostAwarePortalConfiguration extends VirtualHostAwarePortalConfiguration_Base {
    protected VirtualHostAwarePortalConfiguration() {
        super();
        setRoot(Bennu.getInstance());
    }

    public static void ensure() {
        PortalConfiguration currentConfig = Bennu.getInstance().getConfiguration();
        if (currentConfig == null || !currentConfig.getClass().equals(VirtualHostAwarePortalConfiguration.class)) {
            initVirtualHostAwarePortalConfiguration(currentConfig);
        }
    }

    @Atomic(mode = TxMode.WRITE)
    private static void initVirtualHostAwarePortalConfiguration(PortalConfiguration currentConfig) {
        if (currentConfig != null) {
            currentConfig.delete();
        }
        new VirtualHostAwarePortalConfiguration();
    }

    private PortalConfiguration getVirtualHostConfiguration() {
        return VirtualHost.getVirtualHostForThread().getConfiguration();
    }

    @Override
    public void setApplicationTitle(LocalizedString applicationTitle) {
        getVirtualHostConfiguration().setApplicationTitle(applicationTitle);
    }

    @Override
    public LocalizedString getApplicationTitle() {
        return getVirtualHostConfiguration().getApplicationTitle();
    }

    @Override
    public void setApplicationSubTitle(LocalizedString applicationSubTitle) {
        getVirtualHostConfiguration().setApplicationSubTitle(applicationSubTitle);
    }

    @Override
    public LocalizedString getApplicationSubTitle() {
        return getVirtualHostConfiguration().getApplicationSubTitle();
    }

    @Override
    public void setApplicationCopyright(LocalizedString applicationCopyright) {
        getVirtualHostConfiguration().setApplicationCopyright(applicationCopyright);
    }

    @Override
    public LocalizedString getApplicationCopyright() {
        return getVirtualHostConfiguration().getApplicationCopyright();
    }

    @Override
    public void setHtmlTitle(LocalizedString htmlTitle) {
        getVirtualHostConfiguration().setHtmlTitle(htmlTitle);
    }

    @Override
    public LocalizedString getHtmlTitle() {
        return getVirtualHostConfiguration().getHtmlTitle();
    }

    @Override
    public void setSupportEmailAddress(String supportEmailAddress) {
        getVirtualHostConfiguration().setSupportEmailAddress(supportEmailAddress);
    }

    @Override
    public String getSupportEmailAddress() {
        return getVirtualHostConfiguration().getSupportEmailAddress();
    }

    @Override
    public void setSystemEmailAddress(String systemEmailAddress) {
        getVirtualHostConfiguration().setSystemEmailAddress(systemEmailAddress);
    }

    @Override
    public String getSystemEmailAddress() {
        return getVirtualHostConfiguration().getSystemEmailAddress();
    }

    @Override
    public void setTheme(String theme) {
        getVirtualHostConfiguration().setTheme(theme);
    }

    @Override
    public String getTheme() {
        return getVirtualHostConfiguration().getTheme();
    }

    @Override
    public void setLogo(byte[] logo) {
        getVirtualHostConfiguration().setLogo(logo);
    }

    @Override
    public byte[] getLogo() {
        return getVirtualHostConfiguration().getLogo();
    }

    @Override
    public void setLogoType(String logoType) {
        getVirtualHostConfiguration().setLogoType(logoType);
    }

    @Override
    public String getLogoType() {
        return getVirtualHostConfiguration().getLogoType();
    }

    @Override
    public MenuItem getMenu() {
        return getVirtualHostConfiguration().getMenu();
    }

    @Override
    public void setMenu(MenuItem menu) {
        getVirtualHostConfiguration().setMenu(menu);
    }
}
