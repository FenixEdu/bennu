package org.fenixedu.bennu.portal.client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.bennu.portal.model.Application;
import org.fenixedu.bennu.portal.model.ApplicationRegistry;
import org.fenixedu.bennu.portal.model.Functionality;
import org.fenixedu.bennu.portal.servlet.PortalBackendRegistry;
import org.fenixedu.commons.i18n.LocalizedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebListener
public class ClientSiteBackendInitilizer implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(ClientSiteBackendInitilizer.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        PortalBackendRegistry.registerPortalBackend(new ClientSidePortalBackend());
        try {
            initAppsJsonFromJars(getClass().getClassLoader());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initAppsJsonFromJars(final ClassLoader classLoader) throws IOException {
        final Enumeration<URL> resources = classLoader.getResources("apps.json");

        final JsonParser parser = new JsonParser();
        while (resources.hasMoreElements()) {
            URL appsJsonURL = resources.nextElement();
            final InputStreamReader appJsonReader = new InputStreamReader(appsJsonURL.openStream(), StandardCharsets.UTF_8);
            logger.debug("parsing apps.json for {}", appsJsonURL.toExternalForm());
            JsonArray appsJson = parser.parse(appJsonReader).getAsJsonObject().get("apps").getAsJsonArray();
            for (JsonElement appJson : appsJson) {
                parseApplicationInfo(appJson.getAsJsonObject());
            }
        }
    }

    private void parseApplicationInfo(JsonObject appJson) {
        final String accessExpression = appJson.get("accessExpression").getAsString();
        final String path = appJson.get("path").getAsString();
        final LocalizedString title = LocalizedString.fromJson(appJson.get("title"));
        final LocalizedString description = LocalizedString.fromJson(appJson.get("description"));
        final String group = appJson.has("group") ? appJson.get("group").getAsString() : "client-side";

        Application app = new Application(path, path, accessExpression, title, description, group);

        if (appJson.has("functionalities")) {
            for (JsonElement functionality : appJson.get("functionalities").getAsJsonArray()) {
                parseFunctionality(app, functionality.getAsJsonObject());
            }
        }

        ApplicationRegistry.registerApplication(app);
    }

    private void parseFunctionality(Application application, JsonObject funcJson) {
        final String accessExpression = funcJson.get("accessExpression").getAsString();
        final String path = funcJson.get("path").getAsString();
        final LocalizedString title = LocalizedString.fromJson(funcJson.get("title"));
        final LocalizedString description = LocalizedString.fromJson(funcJson.get("description"));

        Functionality functionality =
                new Functionality(ClientSidePortalBackend.BACKEND_KEY, path, path, accessExpression, title, description);

        application.addFunctionality(functionality);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
