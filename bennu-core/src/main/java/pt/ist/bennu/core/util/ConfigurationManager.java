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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.annotation.BennuCoreAnnotationInitializer;
import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.util.rest.RestHost;
import pt.ist.fenixframework.Config;
import pt.ist.fenixframework.artifact.FenixFrameworkArtifact;
import pt.ist.fenixframework.project.DmlFile;
import pt.ist.fenixframework.project.exception.FenixFrameworkProjectException;

public class ConfigurationManager {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationManager.class);

    private static final Properties properties = new Properties();

    private static Map<String, CasConfig> casConfigByHost;

    private static Config config = null;

    private static List<URL> urls = new ArrayList<>();

    private static Map<String, String> resourceModuleMap = new HashMap<>();

    private static Map<String, RestHost> serverHosts;

    private static String thisServerRestSecret;

    static {
        try (InputStream inputStream = ConfigurationManager.class.getResourceAsStream("/configuration.properties")) {
            if (inputStream == null) {
                throw new Error("configuration.properties not found in classpath.");
            }
            properties.load(inputStream);

            config = new Config() {
                {
                    domainModelPaths = new String[0];
                    dbAlias = getProperty("db.alias");
                    dbUsername = getProperty("db.user");
                    dbPassword = getProperty("db.pass");
                    appName = getProperty("app.name");
                    updateRepositoryStructureIfNeeded = true;
                    rootClass = Bennu.class;
                    errorfIfDeletingObjectNotDisconnected = true;
                }

                @Override
                public List<URL> getDomainModelURLs() {
                    return getUrls();
                }
            };

            try {
                List<DmlFile> dmlFiles = new ArrayList<>();
                for (FenixFrameworkArtifact artifact : getArtifacts()) {
                    dmlFiles.addAll(artifact.getDmls());
                    String projectResource = "/" + artifact.getName() + "/project.properties";
                    String url = BennuCoreAnnotationInitializer.class.getResource(projectResource).toExternalForm();
                    if (url.startsWith("jar")) {
                        resourceModuleMap.put(url.substring("jar:".length(), url.length() - projectResource.length() - 1),
                                artifact.getName());
                    } else {
                        resourceModuleMap.put(url.replace(projectResource, ""), artifact.getName());
                    }
                }
                for (DmlFile dml : dmlFiles) {
                    urls.add(dml.getUrl());
                }
            } catch (FenixFrameworkProjectException | IOException e) {
                throw new Error(e);
            }

            casConfigByHost = new HashMap<>();
            serverHosts = new HashMap<>();
            for (final Object key : properties.keySet()) {
                final String property = (String) key;
                initializeCasConfig(property);
                initializeRestServerHosts(property);
            }

            initializeThisServerRestSecret();

        } catch (IOException e) {
            throw new Error("configuration.properties could not be read.", e);
        }
    }

    public static List<FenixFrameworkArtifact> getArtifacts() throws MalformedURLException, IOException,
            FenixFrameworkProjectException {
        return FenixFrameworkArtifact.fromName(getProperty("app.name")).getArtifacts();
    }

    public static void initializeCasConfig(final String property) {
        int i = property.indexOf(".cas.enable");
        if (i >= 0) {
            final String hostname = property.substring(0, i);
            if (getBooleanProperty(property, false)) {
                final String casLoginUrl = getProperty(hostname + ".cas.loginUrl");
                final String casLogoutUrl = getProperty(hostname + ".cas.logoutUrl");
                final String casValidateUrl = getProperty(hostname + ".cas.ValidateUrl");
                final String serviceUrl = getProperty(hostname + ".cas.serviceUrl");

                final CasConfig casConfig = new CasConfig(casLoginUrl, casLogoutUrl, casValidateUrl, serviceUrl);
                casConfigByHost.put(hostname, casConfig);
            } else {
                final CasConfig casConfig = new CasConfig();
                casConfigByHost.put(hostname, casConfig);
            }
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

    public static List<URL> getUrls() {
        return urls;
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

    public static void setProperty(final String key, final String value) {
        properties.setProperty(key, value);
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

        public String getCasLogoutUrl() {
            return casLogoutUrl;
        }

        public String getCasValidateUrl() {
            return casValidateUrl;
        }

        protected void parameterizeLoginUrl(HttpServletRequest request) {
        }
    }

    public static CasConfig getCasConfig(String hostname) {
        for (final Entry<String, CasConfig> entry : casConfigByHost.entrySet()) {
            if (entry.getKey().startsWith(hostname)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static Config getFenixFrameworkConfig() {
        return config;
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
}
