package org.fenixedu.bennu.portal.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.servlet.PortalInitializer;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.qubit.terra.portal.domain.menus.Portal;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

/**
 * A {@link PortalConfiguration} contains the configuration for the installed application, as well as the entry point for the
 * installed functionality tree.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 * 
 */
public class PortalConfiguration extends PortalConfiguration_Base implements Portal {

    private static final Logger logger = LoggerFactory.getLogger(PortalConfiguration.class);

    private PortalConfiguration() {
        super();
        setRoot(Bennu.getInstance());
        setApplicationTitle(new LocalizedString(I18N.getLocale(), "Application Title"));
        setApplicationSubTitle(new LocalizedString(I18N.getLocale(), "Application Subtitle"));
        setApplicationCopyright(new LocalizedString(I18N.getLocale(), "Organization Copyright"));
        setHtmlTitle(getApplicationTitle());
        setTheme("default");
        try (InputStream stream =
                PortalConfiguration.class.getClassLoader().getResourceAsStream("META-INF/resources/img/logo_bennu.svg")) {
            if (stream == null) {
                logger.error("Default logo not found in: img/logo_bennu.svg");
            } else {
                setLogo(ByteStreams.toByteArray(stream));
                setLogoType("image/svg+xml");
            }
        } catch (IOException e) {
            logger.error("Default logo could not be read from: img/logo_bennu.svg");
        }
        try (InputStream stream =
                PortalConfiguration.class.getClassLoader().getResourceAsStream("META-INF/resources/img/favicon_bennu.png")) {
            if (stream == null) {
                logger.error("Default favicon not found in: img/favicon_bennu.png");
            } else {
                setFavicon(ByteStreams.toByteArray(stream));
                setFaviconType("image/png");
            }
        } catch (IOException e) {
            logger.error("Default logo could not be read from: img/favicon_bennu.png");
        }
        new MenuContainer(this);
    }

    @Override
    public com.qubit.terra.framework.tools.primitives.LocalizedString getAppName() {
        return BundleUtil.convertToPlatformLocalizedString(super.getApplicationTitle());
    }

    @Override
    public com.qubit.terra.framework.tools.primitives.LocalizedString getHtmlName() {
        return BundleUtil.convertToPlatformLocalizedString(super.getHtmlTitle());
    }

    @Override
    public com.qubit.terra.framework.tools.primitives.LocalizedString getAppSecondaryName() {
        return BundleUtil.convertToPlatformLocalizedString(super.getApplicationSubTitle());
    }

    @Override
    public com.qubit.terra.framework.tools.primitives.LocalizedString getCopyright() {
        return BundleUtil.convertToPlatformLocalizedString(super.getApplicationCopyright());
    }

    @Override
    public String getPortalTheme() {
        return getTheme();
    }

    @Override
    public String getTheme() {
        String theme = super.getTheme();
        return PortalInitializer.isThemeAvailable(theme) ? theme : "default";
    }

    @Override
    public String getAppSystemEmailAddress() {
        return super.getSystemEmailAddress();
    }

    @Override
    public byte[] getPortalLogo() {
        return getLogo();
    }

    @Override
    public String getPortalLogoChecksum() {
        return getLogoChecksum();
    }

    @Override
    public String getPortalLogoType() {
        return getLogoType();
    }

    @Override
    public String getPortalLogoLinkUrl() {
        return getLogoLinkUrl();
    }

    @Override
    public String getPortalLogoTooltip() {
        return getLogoTooltip();
    }

    @Override
    public byte[] getPortalFavicon() {
        return getFavicon();
    }

    @Override
    public String getPortalFaviconType() {
        return getFaviconType();
    }

    @Override
    public String getPortalDocumentationBaseUrl() {
        return getDocumentationBaseUrl();
    }

    @Override
    public String getAppLoginPath() {
        return getLoginPath();
    }

    @Override
    public String getAppRecoveryLinkPath() {
        return getRecoveryLinkPath();
    }

    @Override
    public String getAppSignUpPath() {
        return getSignUpPath();
    }

    @Override
    public Boolean detectBrowserLocalInLoginPage() {
        return getDetectBrowserLocalInLoginPage();
    }

    @Override
    public byte[] getLogo() {
        return super.getLogo();
    }

    @Atomic(mode = TxMode.WRITE)
    private static PortalConfiguration initialize() {
        if (Bennu.getInstance().getConfiguration() == null) {
            return new PortalConfiguration();
        }
        return Bennu.getInstance().getConfiguration();
    }

    /**
     * Returns the singleton instance of {@link PortalConfiguration} for this application.
     * 
     * @return
     *         The one and only instance of {@link PortalConfiguration}
     */
    public static PortalConfiguration getInstance() {
        if (Bennu.getInstance().getConfiguration() == null) {
            return initialize();
        }
        return Bennu.getInstance().getConfiguration();
    }

    @Override
    public String getSupportEmailAddress() {
        return super.getSupportEmailAddress() != null ? super.getSupportEmailAddress() : CoreConfiguration.getConfiguration()
                .defaultSupportEmailAddress();
    }

    /**
     * Returns the root {@link MenuContainer} of this application.
     */
    @Override
    public MenuContainer getMenu() {
        //FIXME: remove when the framework enables read-only slots
        return super.getMenu();
    }

    @Override
    public Set<MenuContainer> getSubRootSet() {
        return Collections.unmodifiableSet(super.getSubRootSet());
    }

    public Optional<MenuContainer> findSubRoot(String key) {
        return super.getSubRootSet().stream().filter(root -> root.getPath().equals(key)).findAny();
    }

    /**
     * Returns the checksum of the current application's logo.
     * 
     * This value is meant to be used as a mechanism for cache busting, as as such, its correctness is not guarranteed, and it may
     * even be {@code null}.
     * 
     * @return
     *         The checksum of the application's logo. May be null
     */
    @Override
    public String getLogoChecksum() {
        //FIXME: remove when the framework enables read-only slots
        return super.getLogoChecksum();
    }

    @Override
    public void setLogo(byte[] logo) {
        super.setLogo(logo);
        setLogoChecksum(logo == null ? null : Hashing.sha1().hashBytes(logo).toString().substring(0, 12));
    }

    @Override
    public String getAppSupportEmailAddress() {
        return getSupportEmailAddress();
    }

    @Override
    public com.qubit.terra.portal.domain.menus.MenuContainer getRootMenu() {
        return getMenu();
    }

}
