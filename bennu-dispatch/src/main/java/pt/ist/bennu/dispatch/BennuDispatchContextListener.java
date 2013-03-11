package pt.ist.bennu.dispatch;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.util.ConfigurationManager;
import pt.ist.bennu.core.util.MultiLanguageString;
import pt.ist.bennu.dispatch.model.ApplicationInfo;
import pt.ist.bennu.dispatch.model.Details;
import pt.ist.bennu.dispatch.model.FunctionalityInfo;
import pt.ist.bennu.dispatch.model.MultiLanguageDetails;
import pt.ist.fenixframework.artifact.FenixFrameworkArtifact;
import pt.ist.fenixframework.project.exception.FenixFrameworkProjectException;

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
                final JsonObject appJson = getArtifactAppJson(artifact.getName());
                parseApplicationInfo(appJson);
            }
        } catch (IOException | FenixFrameworkProjectException e) {
            throw new Error(e);
        }

    }

    private void parseApplicationInfo(JsonObject appJson) {
        if (appJson == null) {
            return;
        }
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
        appInfo.addFunctionality(new FunctionalityInfo(path, accessExpression, getDetails(funcJson)));
    }

    public Details getDetails(JsonObject functionality) {
        final MultiLanguageString title = MultiLanguageString.fromJson(functionality.get("title"));
        final MultiLanguageString description = MultiLanguageString.fromJson(functionality.get("description"));
        Details details = new MultiLanguageDetails(title, description);
        return details;
    }

    public JsonObject getArtifactAppJson(String modulePath) {
        final String appJsonPath = String.format("/%s/app.json", modulePath);
        final InputStream appJsonStream = BennuDispatchContextListener.class.getResourceAsStream(appJsonPath);
        if (appJsonStream != null) {
            final InputStreamReader appJsonReader = new InputStreamReader(appJsonStream);
            JsonParser parse = new JsonParser();
            LOGGER.info("parsing app.json for {}", modulePath);
            return parse.parse(appJsonReader).getAsJsonObject();

        }
        LOGGER.info("No app.json found in {}", modulePath);
        return null;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
