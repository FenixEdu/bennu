package org.fenixedu.bennu.portal.domain;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.fenixedu.bennu.core.bootstrap.AdminUserBootstrapper;
import org.fenixedu.bennu.core.bootstrap.BootstrapError;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrap;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrapper;
import org.fenixedu.bennu.core.bootstrap.annotations.Field;
import org.fenixedu.bennu.core.bootstrap.annotations.Section;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.PortalBootstrapper.PortalSection;
import org.fenixedu.bennu.portal.model.ApplicationRegistry;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.common.base.Strings;

@Bootstrapper(bundle = "resources.BennuPortalResources", name = "bootstrapper.name", sections = PortalSection.class,
        after = AdminUserBootstrapper.class)
public class PortalBootstrapper {

    @Bootstrap
    public static List<BootstrapError> bootstrapPortal(PortalSection portalSection) {
        PortalConfiguration portalConfiguration = PortalConfiguration.getInstance();
        new MenuContainer(portalConfiguration.getMenu(), ApplicationRegistry.getAppByKey("bennu-admin"));
        portalConfiguration.setApplicationCopyright(makeLocalized(portalSection.getOrganizationName()));
        portalConfiguration.setApplicationTitle(makeLocalized(portalSection.getApplicationTitle()));
        portalConfiguration.setApplicationSubTitle(makeLocalized(portalSection.getApplicationTitle()));
        portalConfiguration.setDocumentationBaseUrl(portalSection.getDocumentationUrl());
        portalConfiguration.setSupportEmailAddress(portalSection.getSupportEmailAddress());
        if (!Strings.isNullOrEmpty(portalConfiguration.getSupportEmailAddress())) {
            SupportConfiguration defaultSupport = new SupportConfiguration(
                    BundleUtil.getLocalizedString("resources.BennuPortalResources", "bootstrapper.default.support"),
                    portalConfiguration.getSupportEmailAddress());
            portalConfiguration.getMenu().setSupport(defaultSupport);
        }
        return Collections.emptyList();
    }

    private static LocalizedString makeLocalized(String value) {
        LocalizedString.Builder builder = new LocalizedString.Builder();
        for (Locale locale : CoreConfiguration.supportedLocales()) {
            builder.with(locale, value);
        }
        return builder.build();
    }

    @Section(name = "bootstrapper.portalSection.name", description = "bootstrapper.portalSection.description",
            bundle = "resources.BennuPortalResources")
    public static interface PortalSection {

        @Field(name = "bootstrapper.portalSection.installationName", defaultValue = "FenixEdu Demo App", order = 4)
        public String getApplicationTitle();

        @Field(name = "bootstrapper.portalSection.organizationName", defaultValue = "FenixEdu", order = 3)
        public String getOrganizationName();

        @Field(name = "bootstrapper.portalSection.documentationUrl", defaultValue = "https://fenixedu.org/dev", order = 5)
        public String getDocumentationUrl();

        @Field(name = "bootstrapper.portalSection.supportEmail", defaultValue = "support@fenixedu.org", order = 6)
        public String getSupportEmailAddress();

    }
}
