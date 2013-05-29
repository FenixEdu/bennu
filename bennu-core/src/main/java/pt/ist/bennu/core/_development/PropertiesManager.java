/*
 * @(#)PropertiesManager.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Bennu Web Application Infrastructure.
 *
 *   The Bennu Web Application Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
 *
 *   Bennu is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Bennu. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package pt.ist.bennu.core._development;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import pt.ist.fenixWebFramework.Config;
import pt.ist.fenixWebFramework.Config.CasConfig;
import pt.utl.ist.fenix.tools.util.MultiProperty;

/**
 * The <code>PropertiesManager</code> class is a application wide utility for
 * accessing the applications configuration and properties. If you need to to
 * add compile-time properties to the application and access them during the
 * applications execution then just add them to the configuration.properties
 * file of your module.
 * 
 * @author João Figueiredo
 * @author Luis Cruz
 * @author Paulo Abrantes
 * @author Susana Fernandes
 * 
 * @version 1.0
 */
public class PropertiesManager extends pt.utl.ist.fenix.tools.util.PropertiesManager {

    private static final Properties properties = new MultiProperty();

    private static Config config = null;

    static {
        try {
            loadProperties(properties, "/configuration.properties");
            final Map<String, CasConfig> casConfigMap = new HashMap<String, CasConfig>();
            for (final Object key : properties.keySet()) {
                final String property = (String) key;
                int i = property.indexOf(".cas.enable");
                if (i >= 0) {
                    final String hostname = property.substring(0, i);
                    if (getBooleanProperty(property)) {
                        final String casLoginUrl = getProperty(hostname + ".cas.loginUrl");
                        final String casLogoutUrl = getProperty(hostname + ".cas.logoutUrl");
                        final String casValidateUrl = getProperty(hostname + ".cas.ValidateUrl");
                        final String serviceUrl = getProperty(hostname + ".cas.serviceUrl");

                        final CasConfig casConfig = new CasConfig(casLoginUrl, casLogoutUrl, casValidateUrl, serviceUrl);
                        casConfigMap.put(hostname, casConfig);
                    } else {
                        final CasConfig casConfig = new CasConfig();
                        casConfigMap.put(hostname, casConfig);
                    }
                }
            }

            config = new Config() {
                {
                    appContext = getProperty("app.context");
                    filterRequestWithDigest = getBooleanProperty("filter.request.with.digest");
                    tamperingRedirect = getProperty("digest.tampering.url");
                    defaultLanguage = getProperty("language");
                    defaultLocation = getProperty("location");
                    defaultVariant = getProperty("variant");
                    casConfigByHost = Collections.unmodifiableMap(casConfigMap);
                    javascriptValidationEnabled = true;
                }
            };
        } catch (IOException e) {
            throw new RuntimeException("Unable to load properties files.", e);
        }
    }

    public static String getProperty(final String key) {
        return properties.getProperty(key);
    }

    public static boolean getBooleanProperty(final String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    public static Integer getIntegerProperty(final String key) {
        return Integer.valueOf(properties.getProperty(key));
    }

    public static void setProperty(final String key, final String value) {
        properties.setProperty(key, value);
    }

    public static Config getFenixFrameworkConfig() {
        return config;
    }
}
