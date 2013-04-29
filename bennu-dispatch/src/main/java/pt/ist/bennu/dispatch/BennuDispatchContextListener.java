package pt.ist.bennu.dispatch;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.i18n.InternationalString;
import pt.ist.bennu.core.util.ConfigurationManager;
import pt.ist.bennu.dispatch.model.ApplicationInfo;
import pt.ist.bennu.dispatch.model.Details;
import pt.ist.bennu.dispatch.model.FunctionalityInfo;
import pt.ist.bennu.dispatch.model.MultiLanguageDetails;
import pt.ist.fenixframework.artifact.FenixFrameworkArtifact;
import pt.ist.fenixframework.project.exception.FenixFrameworkProjectException;

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
            for (FenixFrameworkArtifact artifact : ConfigurationManager.getArtifacts()) {
                final JsonArray appsJson = getArtifactAppsJson(artifact.getName());
                if (appsJson != null) {
                    for (JsonElement appJson : appsJson) {
                        parseApplicationInfo((JsonObject) appJson);
                    }
                }
            }
        } catch (IOException | FenixFrameworkProjectException e) {
            throw new Error(e);
        }

    }

    private void parseApplicationInfo(JsonObject appJson) {
        final String accessExpression = appJson.get("accessExpression").getAsString();
        final String path = appJson.get("path").getAsString();
        ApplicationInfo appInfo = new ApplicationInfo(path, accessExpression, getDetails(appJson));

        for (JsonElement functionality : appJson.get("functionalities").getAsJsonArray()) {
            parseFunctionality(appInfo, (JsonObject) functionality);
        }
        AppServer.registerApp(appInfo);
    }

    private void parseFunctionality(ApplicationInfo appInfo, JsonObject funcJson) {
        final String accessExpression = funcJson.get("accessExpression").getAsString();
        final String path = funcJson.get("path").getAsString();
        appInfo.addFunctionality(new FunctionalityInfo(appInfo.getPath().concat(path), accessExpression, getDetails(funcJson)));
    }

    public Details getDetails(JsonObject functionality) {
        final InternationalString title = InternationalString.fromJson(functionality.get("title"));
        final InternationalString description = InternationalString.fromJson(functionality.get("description"));
        Details details = new MultiLanguageDetails(title, description);
        return details;
    }

    public JsonArray getArtifactAppsJson(String modulePath) {
        final String appJsonPath = String.format("/%s/apps.json", modulePath);
        final InputStream appJsonStream = BennuDispatchContextListener.class.getResourceAsStream(appJsonPath);
        if (appJsonStream != null) {
            final InputStreamReader appJsonReader = new InputStreamReader(appJsonStream);
            JsonParser parse = new JsonParser();
            LOGGER.info("parsing apps.json for {}", modulePath);
            return parse.parse(appJsonReader).getAsJsonObject().get("apps").getAsJsonArray();

        }
        LOGGER.info("No apps.json found in {}", modulePath);
        return null;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
