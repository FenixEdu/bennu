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
package pt.ist.bennu.core.domain.scheduler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import jvstm.TransactionalCommand;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import pt.ist.fenixframework.plugins.fileSupport.domain.GenericFile;
import pt.ist.fenixframework.pstm.Transaction;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class ClassBean implements Serializable {

    public static final Logger LOGGER = Logger.getLogger(ClassBean.class);

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
            super(urls, parent);
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
            final List<URL> urls = new ArrayList<URL>();
            for (final Enumeration<URL> e = super.getResources(name); e.hasMoreElements(); urls.add(e.nextElement())) {
                ;
            }
            for (final Enumeration<URL> e = parent.getResources(name); e.hasMoreElements(); urls.add(e.nextElement())) {
                ;
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

    private String className;
    private String contents;

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

    private static int classpathIndexCounter = 0;

    private static final List<Executer> runningExecuters = Collections.synchronizedList(new ArrayList<Executer>());

    public static List<Executer> getRunningExecuters() {
        return Collections.unmodifiableList(runningExecuters);
    }

    public class Executer extends Thread {

        public class ExecutionLogger extends Thread {

            @Override
            public void run() {
                Transaction.withTransaction(false, new TransactionalCommand() {

                    @Override
                    public void doIt() {
                        new CustomTaskLog(getClassName(), getContents(), uploadTime, taskStart, taskEnd, out.toString(),
                                outputFiles);
                    }

                });
            }
        }

        private final DateTime uploadTime = new DateTime();

        private DateTime taskStart;
        private DateTime taskEnd;
        private Writer out = new StringWriter();
        private Set<GenericFile> outputFiles = new HashSet<GenericFile>();

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

        private void createDirs() throws IOException {
            final String filename = getJavaFileName();
            final File file = new File(filename);
            file.getParentFile().mkdirs();
        }

        private void compileFile() throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException,
                IllegalAccessException, InvocationTargetException, URISyntaxException {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            final Method method = classLoader.getClass().getMethod("getURLs", new Class[0]);
            final URL[] urls = (URL[]) method.invoke(classLoader, new Object[0]);

            final List<File> files = new ArrayList<File>();
            for (final URL url : urls) {
                files.add(new File(url.toURI()));
            }

            final JavaSourceFromString javaSourceFromString = new JavaSourceFromString(getClassName(), getContents());
            final JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
            final StandardJavaFileManager standardJavaFileManager = javaCompiler.getStandardFileManager(null, null, null);

            standardJavaFileManager.setLocation(StandardLocation.CLASS_OUTPUT,
                    Collections.singleton(new File(getBaseClassPathDirName())));
            standardJavaFileManager.setLocation(StandardLocation.CLASS_PATH, files);

            final Collection<JavaFileObject> javaFileObjects = new ArrayList<JavaFileObject>();
            javaFileObjects.add(javaSourceFromString);

            final CompilationTask compilationTask =
                    javaCompiler.getTask(out, standardJavaFileManager, null, null, null, javaFileObjects);
            if (compilationTask.call() == false) {
                LOGGER.warn(out.toString());

            }

            standardJavaFileManager.close();
        }

        private void runTask() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
                MalformedURLException, SecurityException, NoSuchMethodException, IllegalArgumentException,
                InvocationTargetException, InterruptedException {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            final URL[] urls = new URL[] { new File(getBaseClassPathDirName()).toURI().toURL() };
            final MyClassLoader urlClassLoader = new MyClassLoader(urls, classLoader);

            urlClassLoader.loadClass(getClassName());

            final Class<?> clazz = Class.forName(getClassName(), true, urlClassLoader);
            final Object o = clazz.newInstance();

            if (o instanceof WriteCustomTask) {
                final WriteCustomTask writeCustomTask = (WriteCustomTask) o;
                writeCustomTask.setOutputFiles(outputFiles);
            }
            if (o instanceof CustomTask) {
                final CustomTask customTask = (CustomTask) o;
                final PrintWriter printWriter = new PrintWriter(out, true);
                customTask.setOut(printWriter);
                taskStart = new DateTime();
                customTask.run();
                taskEnd = new DateTime();
            } else {
                final Method method = clazz.getMethod("run", new Class[0]);
                taskStart = new DateTime();
                method.invoke(o, new Object[0]);
                taskEnd = new DateTime();
            }
        }

        @Override
        public void run() {
            try {
                try {
                    try {
                        createDirs();
                        compileFile();
                        runTask();
                    } catch (InstantiationException e) {
                        throw new Error(e);
                    } catch (IllegalAccessException e) {
                        throw new Error(e);
                    } catch (IllegalArgumentException e) {
                        throw new Error(e);
                    } catch (InvocationTargetException e) {
                        throw new Error(e);
                    } catch (SecurityException e) {
                        throw new Error(e);
                    } catch (NoSuchMethodException e) {
                        throw new Error(e);
                    } catch (IOException e) {
                        throw new Error(e);
                    } catch (URISyntaxException e) {
                        throw new Error(e);
                    } catch (ClassNotFoundException e) {
                        throw new Error(e);
                    } catch (InterruptedException e) {
                        throw new Error(e);
                    } finally {
                        log();
                    }
                } finally {
                    cleanup();
                }
            } finally {
                runningExecuters.remove(this);
            }
        }

        private void log() {
            final ExecutionLogger executionLogger = new ExecutionLogger();
            executionLogger.start();
            try {
                executionLogger.join();
            } catch (InterruptedException e) {
                throw new Error(e);
            }
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

}
