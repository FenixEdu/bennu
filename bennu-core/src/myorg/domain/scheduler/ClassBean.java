package myorg.domain.scheduler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
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
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

public class ClassBean implements Serializable {

    public static class JavaSourceFromString extends SimpleJavaFileObject {

        final String code;

        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension), Kind.SOURCE);
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
	    for (final Enumeration<URL> e = super.getResources(name); e.hasMoreElements(); urls.add(e.nextElement())) ;
	    for (final Enumeration<URL> e = parent.getResources(name); e.hasMoreElements(); urls.add(e.nextElement())) ;
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

    static {
	cleanup();
    }

    public static void cleanup() {
	final String tmpDirName = System.getProperty("java.io.tmpdir");
	final File file = new File(tmpDirName);
	for (final File dir : file.listFiles()) {
	    if (dir.isDirectory() && dir.getName().indexOf("ClassBean_classpath_") >= 0) {
		dir.delete();
	    }
	}	
    }

    public class Executer extends Thread {


	private final int classpathIndex = classpathIndexCounter++;

	public Executer() {
	}

	private String getBaseClassPathDirName() {
	    final String tmpDirName = System.getProperty("java.io.tmpdir");
	    return tmpDirName + File.separatorChar + "ClassBean_classpath_" + classpathIndex;
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

	private void compileFile() throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, URISyntaxException {
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

	    standardJavaFileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singleton(new File(getBaseClassPathDirName())));
	    standardJavaFileManager.setLocation(StandardLocation.CLASS_PATH, files);

	    final Collection<JavaFileObject> javaFileObjects = new ArrayList<JavaFileObject>();
	    javaFileObjects.add(javaSourceFromString);

	    final CompilationTask compilationTask = javaCompiler.getTask(null, standardJavaFileManager, null, null, null, javaFileObjects);
	    final Boolean result = compilationTask.call();

	    System.out.println("Compile result: " + result);

	    standardJavaFileManager.close();
	}

	private void attemptLoad() throws ClassNotFoundException, InstantiationException, IllegalAccessException, MalformedURLException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, InterruptedException {
	    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    System.out.println("Classloader: " + classLoader.getClass().getName());
	    final URL[] urls = new URL[] { new File(getBaseClassPathDirName()).toURL() };
	    final MyClassLoader urlClassLoader = new MyClassLoader(urls, classLoader);

	    urlClassLoader.loadClass(getClassName());

	    final Class clazz = Class.forName(getClassName(), true, urlClassLoader);

	    final Object o = clazz.newInstance();
	    final Method method = clazz.getMethod("run", null);
	    method.invoke(o, null);
	    System.out.println("Created object: " + o.getClass().getName());
	}

	@Override
	public void run() {
	    try {
		createDirs();
		compileFile();
		attemptLoad();
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
	    }
	}

    }

    public synchronized void run() {
	final Executer executer = new Executer();
	try {
	    executer.start();
	    executer.join();
	} catch (final Exception ex) {
	    throw new Error(ex);
	} finally {
	    cleanup();
	}
    }

}
