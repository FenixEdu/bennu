package org.fenixedu.bennu.core.json.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.joda.time.DateTime;

import com.google.gson.JsonElement;

@DefaultJsonAdapter(JarFile.class)
public class JarFileViewer implements JsonViewer<JarFile> {
    @Override
    public JsonElement view(JarFile file, JsonBuilder ctx) {
        Properties properties = new Properties();
        properties.put("jar", file.getName());

        ZipEntry manifest = file.getEntry("META-INF/MANIFEST.MF");
        if (manifest != null) {
            properties.put("creation", new DateTime(manifest.getTime()).toString("yyyy-MM-dd HH:mm:ss"));
        }

        Enumeration<JarEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (jarEntry.getName().endsWith("pom.properties")) {
                try (InputStream stream = file.getInputStream(jarEntry)) {
                    properties.load(stream);
                } catch (IOException e) {
                    throw new Error(e);
                }
            }
        }
        try {
            for (Entry<Object, Object> entry : file.getManifest().getMainAttributes().entrySet()) {
                properties.put(entry.getKey().toString(), entry.getValue());
            }
        } catch (IOException e) {
            throw new Error(e);
        }
        return ctx.view(properties);
    }
}
