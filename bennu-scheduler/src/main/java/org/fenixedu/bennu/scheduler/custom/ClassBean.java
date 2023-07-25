/*
 * @(#)ClassBean.java
 *
 * Copyright 2011 Instituto Superior Tecnico
 * Founding Authors: Luis Cruz
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Scheduler Module.
 *
 *   The Scheduler Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version
 *   3 of the License, or (at your option) any later version.
 *
 *   The Scheduler Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Scheduler Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.fenixedu.bennu.scheduler.custom;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.fenixedu.bennu.core.security.Authenticate;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class ClassBean implements Serializable {

    private static final long serialVersionUID = 6558922605683134259L;

    public static final Logger LOGGER = LoggerFactory.getLogger(ClassBean.class);

    private String className;

    private String contents;

    public static class JavaSourceFromString extends SimpleJavaFileObject {

        final String code;

        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    public static class MyClassLoader extends URLClassLoader {

        private final ClassLoader parent;

        public MyClassLoader(final URL[] urls, final ClassLoader parent) {
            super(urls);
            /* The parent is not explicitly defined, instead we manually
             * delegate whenever necessary.
             * This ensures that the newly uploaded class is always chosen
             * instead of an existing class with the same name.
             */
            this.parent = parent;
        }

        @Override
        public synchronized void clearAssertionStatus() {
            parent.clearAssertionStatus();
            super.clearAssertionStatus();
        }

        @Override
        public URL getResource(String name) {
            final URL url = super.getResource(name);
            return url == null ? parent.getResource(name) : url;
        }

        @Override
        public InputStream getResourceAsStream(final String name) {
            final InputStream inputStream = parent.getResourceAsStream(name);
            return inputStream == null ? super.getResourceAsStream(name) : inputStream;
        }

        @Override
        public Enumeration<URL> getResources(final String name) throws IOException {
            final List<URL> urls = new ArrayList<>();
            for (final Enumeration<URL> e = super.getResources(name); e.hasMoreElements(); urls.add(e.nextElement())) {
            }
            for (final Enumeration<URL> e = parent.getResources(name); e.hasMoreElements(); urls.add(e.nextElement())) {
            }
            return Collections.enumeration(urls);
        }

        @Override
        protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            try {
                return super.loadClass(name, resolve);
            } catch (ClassNotFoundException e) {
                return parent.loadClass(name);
            }
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            try {
                return super.loadClass(name);
            } catch (ClassNotFoundException e) {
                return parent.loadClass(name);
            }
        }

        @Override
        public synchronized void setClassAssertionStatus(String className, boolean enabled) {
            parent.setClassAssertionStatus(className, enabled);
            super.setClassAssertionStatus(className, enabled);
        }

        @Override
        public synchronized void setDefaultAssertionStatus(boolean enabled) {
            parent.setDefaultAssertionStatus(enabled);
            super.setDefaultAssertionStatus(enabled);
        }

        @Override
        public synchronized void setPackageAssertionStatus(String packageName, boolean enabled) {
            parent.setPackageAssertionStatus(packageName, enabled);
            super.setPackageAssertionStatus(packageName, enabled);
        }

    }

    public ClassBean(String className, String contents) {
        super();
        this.className = className;
        this.contents = contents.trim();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    private static final List<Executer> runningExecuters = Collections.synchronizedList(new ArrayList<Executer>());

    public static List<Executer> getRunningExecuters() {
        return Collections.unmodifiableList(runningExecuters);
    }

    public class Executer extends Thread {

        private final DateTime uploadTime = new DateTime();

        private final Writer out = new StringWriter();

        private final String username = Authenticate.isLogged() ? Authenticate.getUser().getUsername() : null;

        public Executer() {
            runningExecuters.add(this);
        }

        private String getBaseClassPathDirName() {
            final String tmpDirName = System.getProperty("java.io.tmpdir");
            return tmpDirName + File.separatorChar + "ClassBean_classpath_" + hashCode();
        }

        private String getBaseFileName() {
            final String baseClassPathDirName = getBaseClassPathDirName();
            final String pathFileName = className.replace('.', File.separatorChar);
            return baseClassPathDirName + File.separatorChar + pathFileName;
        }

        private String getJavaFileName() {
            return getBaseFileName() + ".java";
        }

        private void createDirs() {
            final String filename = getJavaFileName();
            final File file = new File(filename);
            file.getParentFile().mkdirs();
        }

        private Boolean compileFile() throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException,
                IllegalAccessException, InvocationTargetException, URISyntaxException {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            final Method method = classLoader.getClass().getMethod("getURLs", new Class[0]);
            final URL[] urls = (URL[]) method.invoke(classLoader, new Object[0]);

            final List<File> files = new ArrayList<>();
            for (final URL url : urls) {
                files.add(new File(url.toURI()));
            }

            CodeSource servletCodeSource = HttpServletRequest.class.getProtectionDomain().getCodeSource();
            if (servletCodeSource != null && servletCodeSource.getLocation() != null) {
                files.add(new File(servletCodeSource.getLocation().toURI()));
            }

            final JavaSourceFromString javaSourceFromString = new JavaSourceFromString(getClassName(), getContents());
            final JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
            try (StandardJavaFileManager standardJavaFileManager = javaCompiler.getStandardFileManager(null, null, null)) {

                standardJavaFileManager.setLocation(StandardLocation.CLASS_OUTPUT,
                        Collections.singleton(new File(getBaseClassPathDirName())));
                standardJavaFileManager.setLocation(StandardLocation.CLASS_PATH, files);

                final Collection<JavaFileObject> javaFileObjects = new ArrayList<>();
                javaFileObjects.add(javaSourceFromString);
                
                final Iterable<String> options = Arrays.asList("-g");
                
                final CompilationTask compilationTask =
                        javaCompiler.getTask(out, standardJavaFileManager, null, options, null, javaFileObjects);
                if (compilationTask.call() == false) {
                    return false;
                }
                return true;
            }
        }

        private void runTask() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException,
                IllegalArgumentException, IOException {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            final URL[] urls = new URL[] { new File(getBaseClassPathDirName()).toURI().toURL() };
            try (MyClassLoader urlClassLoader = new MyClassLoader(urls, classLoader)) {

                urlClassLoader.loadClass(getClassName());

                @SuppressWarnings("unchecked")
                final Class<? extends CustomTask> clazz =
                        (Class<? extends CustomTask>) Class.forName(getClassName(), true, urlClassLoader); // nosemgrep
                setName("CustomTaskRunner-" + clazz.getName() + "-" + uploadTime.getMillis());
                CustomTask task = clazz.newInstance();
                task.init(contents, username);
                task.run();
            }
        }

        @Override
        public void run() {
            runCompileAndExecute();
        }

        public void runCompileAndExecute() {
            try {
                try {
                    try {
                        createDirs();
                        compileFile();
                        runTask();
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException | SecurityException | NoSuchMethodException | IOException
                            | URISyntaxException | ClassNotFoundException e) {
                        throw new Error(e);
                    } finally {
                    }
                } finally {
                    cleanup();
                }
            } finally {
                runningExecuters.remove(this);
            }
        }

        public JsonObject runCompile() {
            final JsonObject result;
            Boolean compiledSuccessfully = false;
            try {
                createDirs();
                compiledSuccessfully = compileFile();
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException
                    | NoSuchMethodException | IOException | URISyntaxException e) {
                try (PrintWriter pout = new PrintWriter(out)) {
                    e.printStackTrace(pout);
                }
            } finally {
                cleanup();
            }
            result = new JsonObject();
            result.addProperty("compileOK", compiledSuccessfully);
            if (!compiledSuccessfully) {
                result.addProperty("error", out.toString());
            }
            return result;
        }

        private void cleanup() {
            final String dirName = getBaseClassPathDirName();
            final File file = new File(dirName);
            delete(file);
        }

        private void delete(final File file) {
            if (file.isDirectory()) {
                for (final File subFile : file.listFiles()) {
                    delete(subFile);
                }
            }
            file.delete();
        }

        public String getClassBeanClassName() {
            return getClassName();
        }

        public DateTime getUploaded() {
            return uploadTime;
        }
    }

    public void run() {
        final Executer executer = new Executer();
        executer.start();
    }

    public JsonObject compile() {
        final Executer executer = new Executer();
        return executer.runCompile();
    }

}
