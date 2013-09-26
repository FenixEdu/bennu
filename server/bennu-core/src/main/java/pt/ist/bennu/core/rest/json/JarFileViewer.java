/*
 * JarFileViewer.java
 *
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 *
 * This file is part of bennu-core.
 *
 * bennu-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * bennu-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with bennu-core.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.rest.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.joda.time.DateTime;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.json.JsonViewer;

import com.google.gson.JsonElement;

@DefaultJsonAdapter(JarFile.class)
public class JarFileViewer implements JsonViewer<JarFile> {
    @Override
    public JsonElement view(JarFile file, JsonBuilder ctx) {
        Properties properties = new Properties();
        properties.put("jar", file.getName());

        ZipEntry manifest = file.getEntry("META-INF/MANIFEST.MF");
        properties.put("creation", new DateTime(manifest.getTime()).toString("yyyy-MM-dd HH:mm:ss"));

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
