package org.fenixedu.bennu.core.api.json;

import java.io.IOException;
import java.util.jar.JarFile;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;

import pt.ist.fenixframework.core.Project;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(Project.class)
public class FenixFrameworkProjectViewer implements JsonViewer<Project> {
    @Override
    public JsonElement view(Project project, JsonBuilder ctx) {
        String projectResource = "/" + project.getName() + "/project.properties";
        String url = FenixFrameworkProjectViewer.class.getResource(projectResource).toExternalForm();
        if (url.startsWith("jar")) {
            String jar = url.substring("jar:file:".length(), url.length() - projectResource.length() - 1);
            try (JarFile jarFile = new JarFile(jar)) {
                return ctx.view(jarFile);
            } catch (IOException e) {
                throw new Error(e);
            }
        }
        // webapp case
        JsonObject json = new JsonObject();
        json.addProperty("name", project.getName());
        return json;
    }
}