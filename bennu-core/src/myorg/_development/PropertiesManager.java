/*
 * @(#)PropertiesManager.java
 *
 * Copyright 2009 Instituto Superior Tecnico, João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the MyOrg web application infrastructure.
 *
 *   MyOrg is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.*
 *
 *   MyOrg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with MyOrg. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg._development;

import java.io.IOException;
import java.util.Properties;

import pt.ist.fenixWebFramework.Config;

/**
 * The <code>PropertiesManager</code> class is a application wide utility for
 * accessing the applications configuration and properties.
 *
 * @author  João Figueiredo
 * @author  Luis Cruz
 * @author  Paulo Abrantes
 * @author  Susana Fernandes
 *
 * @version 1.0
 */
public class PropertiesManager extends pt.utl.ist.fenix.tools.util.PropertiesManager {

    private static final Properties properties = new Properties();

    static {
        try {
            loadProperties(properties, "/configuration.properties");
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

    public static Config getFenixFrameworkConfig(final String[] domainModels) {
        return new Config() {{
            domainModelPaths = domainModels;
            dbAlias = getProperty("db.alias");
            dbUsername = getProperty("db.user");
            dbPassword = getProperty("db.pass");
            appName = getProperty("app.name");
            appContext =getProperty("app.context"); 
            filterRequestWithDigest = getBooleanProperty("filter.request.with.digest");
            tamperingRedirect = getProperty("digest.tampering.url");
            errorIfChangingDeletedObject = getBooleanProperty("error.if.changing.deleted.object");
            defaultLanguage = getProperty("language");
            defaultLocation = getProperty("location");
            defaultVariant = getProperty("variant");
            updateDataRepositoryStructure = true;
            updateDataRepositoryStructure = true;
            casEnabled = getBooleanProperty("cas.enable");
            casLoginUrl = getProperty("cas.loginUrl");
            casValidateUrl = getProperty("cas.ValidateUrl");
            casLogoutUrl = getProperty("cas.logoutUrl");
        }};
    }

}
