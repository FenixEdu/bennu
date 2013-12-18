package pt.ist.bennu.portal;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.commons.i18n.LocalizedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.portal.domain.ApplicationInfo;
import pt.ist.bennu.portal.domain.Details;
import pt.ist.bennu.portal.domain.FunctionalityInfo;
import pt.ist.bennu.portal.domain.MultiLanguageDetails;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebListener
public class BennuDispatchContextListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BennuDispatchContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            LOGGER.info("Init portal initialization, reading apps.json");
            initAppsJsonFromJars(getClass().getClassLoader());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("portal initialization done.");
    }

    private void initAppsJsonFromJars(final ClassLoader classLoader) throws IOException {

        final Enumeration<URL> resources = classLoader.getResources("apps.json");

        final JsonParser parse = new JsonParser();

        while (resources.hasMoreElements()) {
            URL appsJsonURL = resources.nextElement();
            final InputStreamReader appJsonReader = new InputStreamReader(appsJsonURL.openStream());
            final String url = appsJsonURL.toExternalForm();
            LOGGER.info("parsing apps.json for {}", url);
            JsonArray appsJson = parse.parse(appJsonReader).getAsJsonObject().get("apps").getAsJsonArray();
            if (appsJson != null) {
                for (JsonElement appJson : appsJson) {
                    parseApplicationInfo((JsonObject) appJson);
                }
            } else {
                LOGGER.info("No apps.json found in {}", url);
            }
        }

    }

    private void parseApplicationInfo(JsonObject appJson) {
        final String accessExpression = appJson.get("accessExpression").getAsString();
        final String path = appJson.get("path").getAsString();
        ApplicationInfo appInfo = new ApplicationInfo(path, accessExpression, getDetails(appJson));

        if (appJson.has("functionalities")) {
            for (JsonElement functionality : appJson.get("functionalities").getAsJsonArray()) {
                parseFunctionality(appInfo, (JsonObject) functionality);
            }
        }
        AppServer.registerApp(appInfo);
    }

    private void parseFunctionality(ApplicationInfo appInfo, JsonObject funcJson) {
        final String accessExpression = funcJson.get("accessExpression").getAsString();
        final String path = funcJson.get("path").getAsString();
        appInfo.addFunctionality(new FunctionalityInfo(appInfo.getPath().concat(path), accessExpression, getDetails(funcJson)));
    }

    public Details getDetails(JsonObject functionality) {
        final LocalizedString title = LocalizedString.fromJson(functionality.get("title"));
        final LocalizedString description = LocalizedString.fromJson(functionality.get("description"));
        Details details = new MultiLanguageDetails(title, description);
        return details;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
