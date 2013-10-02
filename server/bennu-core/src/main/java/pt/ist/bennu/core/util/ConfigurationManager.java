/*
 * ConfigurationManager.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.annotation.BennuCoreAnnotationInitializer;
import pt.ist.bennu.core.util.rest.RestHost;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.core.Project;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;

public class ConfigurationManager {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);

    private static final Properties properties = new Properties();

    private static CasConfig casConfig;

    private static Map<String, String> resourceModuleMap = new HashMap<>();

    private static Map<String, RestHost> serverHosts;

    private static String thisServerRestSecret;

    private static Set<Locale> locales;

    static {
        try (InputStream inputStream = ConfigurationManager.class.getResourceAsStream("/configuration.properties")) {
            if (inputStream == null) {
                throw new Error("configuration.properties not found in classpath.");
            }
            properties.load(inputStream);

            initializeLocales();
            initializeCasConfig();

            for (Project artifact : getArtifacts()) {
                String projectResource = "/" + artifact.getName() + "/project.properties";
                String url = BennuCoreAnnotationInitializer.class.getResource(projectResource).toExternalForm();
                if (url.startsWith("jar")) {
                    resourceModuleMap.put(url.substring("jar:".length(), url.length() - projectResource.length() - 1),
                            artifact.getName());
                } else {
                    resourceModuleMap.put(url.replace(projectResource, ""), artifact.getName());
                }
            }
            serverHosts = new HashMap<>();
            for (final Object key : properties.keySet()) {
                final String property = (String) key;
                initializeRestServerHosts(property);
            }

            initializeThisServerRestSecret();

        } catch (IOException e) {
            throw new Error("configuration.properties could not be read.", e);
        }
    }

    private static void initializeLocales() {
        final String localesString = getProperty("locales");

        if (StringUtils.isEmpty(localesString)) {
            throw new Error("Please add locales property to configuration.properties");
        }

        locales = new HashSet<>();
        String defaultLocaleString = getProperty("locale.default");
        if (!Strings.isNullOrEmpty(defaultLocaleString)) {
            Locale.setDefault(Locale.forLanguageTag(defaultLocaleString.trim()));
        }

        Locale defaultLocale = Locale.getDefault();
        for (String locale : localesString.trim().split("\\s*,\\s*")) {
            locales.add(Locale.forLanguageTag(locale));
        }
        if (!locales.contains(defaultLocale)) {
            throw new Error(String.format(
                    "Please make sure the defaultLocale: %s is part of the supported locales in configuration.properties",
                    defaultLocale));
        }

        logger.info("Supported Locales : {}",
                Joiner.on(",").join(FluentIterable.from(locales).transform(new Function<Locale, String>() {
                    @Override
                    public String apply(Locale input) {
                        return input.toLanguageTag();
                    }
                }).toArray(String.class)));
    }

    public static List<Project> getArtifacts() {
        return FenixFramework.getProject().getProjects();
    }

    public static void initializeCasConfig() {
        if (getBooleanProperty("cas.enable", false)) {
            final String casLoginUrl = getProperty("cas.loginUrl");
            final String casLogoutUrl = getProperty("cas.logoutUrl");
            final String casValidateUrl = getProperty("cas.ValidateUrl");
            final String serviceUrl = getProperty("cas.serviceUrl");
            casConfig = new CasConfig(casLoginUrl, casLogoutUrl, casValidateUrl, serviceUrl);
        } else {
            casConfig = new CasConfig();
        }
    }

    private static void initializeThisServerRestSecret() {
        thisServerRestSecret = getProperty("rest.secret");
        if (!StringUtils.isBlank(thisServerRestSecret)) {
            logger.info("loading rest.secret");
        }
    }

    private static void initializeRestServerHosts(String property) {
        final int i = property.indexOf(".rest.url");
        if (i >= 0) {
            final String hostKey = property.substring(0, i);
            final String hostUrl = getProperty(property);
            final String hostSecret = getProperty(hostKey + ".rest.secret");
            logger.info("add Jersey host {} {}", hostKey, hostUrl);
            serverHosts.put(hostKey, new RestHost(hostUrl, hostSecret));
        }
    }

    public static RestHost getHost(String hostKey) {
        return serverHosts.get(hostKey);
    }

    public static String getThisServerSecret() {
        return thisServerRestSecret;
    }

    public static String getProperty(final String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(final String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static boolean getBooleanProperty(final String key, boolean defaultValue) {
        return properties.containsKey(key) ? Boolean.parseBoolean(properties.getProperty(key)) : defaultValue;
    }

    public static Integer getIntegerProperty(final String key) {
        return Integer.valueOf(properties.getProperty(key));
    }

    public static Integer getIntegerProperty(final String key, final Integer defaultValue) {
        return properties.containsKey(key) ? Integer.valueOf(properties.getProperty(key)) : defaultValue;
    }

    public static void setProperty(final String key, final String value) {
        properties.setProperty(key, value);
    }

    public static Properties getProperties() {
        return properties;
    }

    public static class CasConfig {

        protected boolean casEnabled = false;
        protected String casLoginUrl = null;
        protected String casLogoutUrl = null;
        protected String casValidateUrl = null;
        protected String serviceUrl = null;

        public CasConfig() {
        }

        public CasConfig(String casLoginUrl, String casLogoutUrl, String casValidateUrl, String serviceUrl) {
            this.casEnabled = true;
            this.casLoginUrl = casLoginUrl;
            this.casLogoutUrl = casLogoutUrl;
            this.casValidateUrl = casValidateUrl;
            this.serviceUrl = serviceUrl;
        }

        public String getServiceUrl() {
            return serviceUrl;
        }

        public boolean isCasEnabled() {
            return casEnabled;
        }

        public String getCasLoginUrl(HttpServletRequest request) {
            return casLoginUrl + "https://" + request.getServerName();
        }

        public String getCasLoginUrl() {
            return casLoginUrl;
        }

        public String getCasLogoutUrl() {
            return casLogoutUrl;
        }

        public String getCasValidateUrl() {
            return casValidateUrl;
        }

        protected void parameterizeLoginUrl(HttpServletRequest request) {
        }
    }

    public static CasConfig getCasConfig() {
        return casConfig;
    }

    public static String getModuleOf(Class<?> type) {
        String typeLocation = type.getProtectionDomain().getCodeSource().getLocation().toExternalForm();
        if (resourceModuleMap.containsKey(typeLocation)) {
            return resourceModuleMap.get(typeLocation);
        }
        for (String path : resourceModuleMap.keySet()) {
            if (typeLocation.startsWith(path)) {
                return resourceModuleMap.get(path);
            }
        }
        throw new Error("Type: " + type.getName() + " not found on any module");
    }

    public static Set<Locale> getSupportedLocales() {
        return locales;
    }

    public static Boolean isSupportedLanguage(String languageTag) {
        return locales.contains(Locale.forLanguageTag(languageTag));
    }

    public static Properties rawProperties() {
        return (Properties) properties.clone();
    }

    public static String getDefaultSupportEmailAddress() {
        return getProperty("default.support.email.address");
    }
}
