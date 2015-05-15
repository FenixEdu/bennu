package org.fenixedu.bennu.portal.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.servlet.PortalInitializer;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.io.ByteStreams;

/**
 * A {@link PortalConfiguration} contains the configuration for the installed application, as well as the entry point for the
 * installed functionality tree.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 * 
 */
public final class PortalConfiguration extends PortalConfiguration_Base {

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
    public String getTheme() {
        String theme = super.getTheme();
        return PortalInitializer.isThemeAvailable(theme) ? theme : "default";
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

}
