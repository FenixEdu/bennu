package org.fenixedu.bennu.core.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.IllformedLocaleException;
import java.util.Locale;
import java.util.Locale.Builder;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;

import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class CoreConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(CoreConfiguration.class);

    public static class CasConfig {
        protected CasConfig(Boolean casEnabled, String casServerUrl, String casServiceUrl) {
            this.casEnabled = casEnabled;
            this.casServerUrl = casServerUrl;
            this.casServiceUrl = casServiceUrl;
        }

        private final Boolean casEnabled;

        private final String casServerUrl;

        private final String casServiceUrl;

        public Boolean isCasEnabled() {
            return casEnabled;
        }

        public String getCasServiceUrl() {
            return casServiceUrl;
        }

        public String getCasLoginUrl(HttpServletRequest request) {
            return getCasLoginUrl("https://" + request.getServerName());
        }

        public String getCasLoginUrl() {
            return getCasLoginUrl(casServiceUrl);
        }

        public String getCasLoginUrl(String service) {
            return casServerUrl + "/login?service=" + service;
        }

        public String getCasLogoutUrl() {
            return casServerUrl + "/logout";
        }

        public String getCasValidateUrl() {
            return casServerUrl + "/serviceValidate";
        }
    }

    @ConfigurationManager(description = "Bennu Core Configurations")
    public interface ConfigurationProperties {
        @ConfigurationProperty(
                key = "locale.default",
                description = "Default System Locale. If empty falls back to java system default. Must be included in locales.supported")
        public String defaultLocale();

        @ConfigurationProperty(
                key = "default.support.email.address",
                description = "Default email for support. This is intended to be the fall-back for when no application specific email is configured.")
        public String defaultSupportEmailAddress();

        @ConfigurationProperty(
                key = "development.mode",
                description = "Whether development mode is on. Throughout the application the behaviour can change according to this setting.",
                defaultValue = "true")
        public Boolean developmentMode();

        @ConfigurationProperty(
                key = "locales.supported",
                description = "Locales that should be supported in ResourceBundles and other I18N mechanisms. If not specified falls back to a list with only the java system default.")
        public String supportedLocales();

        @ConfigurationProperty(key = "cas.enabled", defaultValue = "false")
        public Boolean casEnabled();

        @ConfigurationProperty(key = "cas.serverUrl", defaultValue = "http://localhost:8080/cas")
        public String casServerUrl();

        @ConfigurationProperty(key = "cas.serviceUrl")
        public String casServiceUrl();

        @ConfigurationProperty(key = "application.url", description = "Full application url",
                defaultValue = "http://localhost:8080")
        public String applicationUrl();

        @ConfigurationProperty(key = "multipart.maxFileSize", description = "maximum size allowed for uploaded files",
                defaultValue = "2147483648")
        public Long multipartMaxFilesize();

        @ConfigurationProperty(key = "multipart.maxRequestSize",
                description = "maximum size allowed for multipart/form-data requests", defaultValue = "2252341248")
        public Long multipartMaxRequestSize();

        @ConfigurationProperty(key = "multipart.fileSizeThreshold",
                description = "the size threshold after which files will be written to disk", defaultValue = "67108864")
        public Integer multipartFileSizeThreshold();

        @ConfigurationProperty(key = "api.csrf.filter.enabled",
                description = "Enables the CSRF protection in POST, PUT and DELETE requests to API endpoints.",
                defaultValue = "false")
        public Boolean apiCSRFFilterEnabled();

        /**
         * Gets the maximum size allowed for multipart/form-data requests.
         * 
         * @return the maximum size allowed for multipart/form-data requests
         */

        /**
         * Gets the size threshold after which files will be written to disk.
         * 
         * @return the size threshold after which files will be written to disk
         */
    }

    private static CasConfig casConfig = new CasConfig(getConfiguration().casEnabled(), getConfiguration().casServerUrl(),
            getConfiguration().casServiceUrl());

    private static Set<Locale> supportedLocales = new HashSet<>();

    static {
        String supportedLocalesString = getConfiguration().supportedLocales();
        if (!Strings.isNullOrEmpty(supportedLocalesString)) {
            for (String locale : supportedLocalesString.split("\\s*,\\s*")) {
                try {
                    supportedLocales.add(new Builder().setLanguageTag(locale).build());
                } catch (IllformedLocaleException e) {
                    logger.error("Invalid supported locale: {}", locale);
                }
            }
        } else {
            supportedLocales.add(Locale.getDefault());
        }
        supportedLocales = Collections.unmodifiableSet(supportedLocales);
        String defaultLocale = getConfiguration().defaultLocale();
        if (!Strings.isNullOrEmpty(defaultLocale)) {
            try {
                Locale locale = new Builder().setLanguageTag(defaultLocale).build();
                if (supportedLocales.contains(locale)) {
                    Locale.setDefault(locale);
                } else {
                    logger.error(
                            "Specified default.locale: {} not part of the supported locales: {}. Keeping java's system default: {}",
                            locale.toLanguageTag(), formatSupportedLocales(), Locale.getDefault().toLanguageTag());
                }
            } catch (IllformedLocaleException e) {
                logger.error("Invalid default locale: {}", defaultLocale);
            }
        }
        if (!supportedLocales().contains(Locale.getDefault())) {
            logger.error(
                    "Current java's default locale: {} not part of the supported locales: {}. Choosing first of the list: {}.",
                    Locale.getDefault().toLanguageTag(), formatSupportedLocales(), supportedLocales().iterator().next()
                            .toLanguageTag());
            Locale.setDefault(supportedLocales().iterator().next());
        }
    }

    private static String formatSupportedLocales() {
        return supportedLocales().stream().map(Locale::toLanguageTag).collect(Collectors.joining(","));
    }

    public static CasConfig casConfig() {
        return casConfig;
    }

    public static Set<Locale> supportedLocales() {
        return supportedLocales;
    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }

    public static MultipartConfigElement getMultipartConfigElement() {
        final String location = System.getProperty("java.io.tmpdir");
        final Long maxFileSize = getConfiguration().multipartMaxFilesize();
        final Long maxRequestSize = getConfiguration().multipartMaxRequestSize();
        final Integer fileSizeThreshold = getConfiguration().multipartFileSizeThreshold();
        return new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);
    }
}
