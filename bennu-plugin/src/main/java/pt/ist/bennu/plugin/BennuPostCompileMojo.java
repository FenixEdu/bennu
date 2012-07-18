package pt.ist.bennu.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * Post process compiled classes
 * 
 * @goal post-compile
 * 
 * @phase process-classes
 * 
 * @requiresDependencyResolution runtime
 */
public class BennuPostCompileMojo extends AbstractMojo {
    /**
     * Maven Project
     * 
     * @parameter default-value="${project}"
     */
    private MavenProject mavenProject;

    /**
     * Classes Directory
     * 
     * @parameter expression="${post-compile.classesDirectory}"
     *            default-value="${project.build.outputDirectory}"
     */
    private File classesDirectory;

    @Override
    public void execute() throws MojoExecutionException {
	try (URLClassLoader loader = augmentClassLoader(getLog(), mavenProject)) {
	    Class<?> serviceInjector = loader.loadClass("pt.ist.fenixWebFramework.services.ServiceAnnotationInjector");
	    Method injector = serviceInjector.getMethod("inject", new Class<?>[] { File.class, ClassLoader.class });
	    injector.invoke(null, classesDirectory, loader);
	} catch (ClassNotFoundException e) {
	    getLog().info("No @Service injector found in classpath, not processing.");
	} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
		| InvocationTargetException e) {
	    throw new MojoExecutionException(null, e);
	} catch (IOException e) {
	    throw new MojoExecutionException(null, e);
	}

	// TODO: checked
	// CheckedAnnotationInjector.inject(classesDirectory, loader);

	File embedded = new File(mavenProject.getBasedir(), ".embeddedAnnotationLog");
	if (embedded.exists()) {
	    embedded.renameTo(new File(mavenProject.getBuild().getOutputDirectory() + File.separatorChar
		    + mavenProject.getArtifactId() + File.separatorChar + ".embeddedAnnotationLog"));
	}

	File createNode = new File(mavenProject.getBasedir(), ".createNodeActionAnnotationLog");
	if (createNode.exists()) {
	    createNode.renameTo(new File(mavenProject.getBuild().getOutputDirectory() + File.separatorChar
		    + mavenProject.getArtifactId() + File.separatorChar + ".createNodeActionAnnotationLog"));
	}

	File functionalities = new File(mavenProject.getBasedir(), ".functionalitiesMappingLog");
	if (functionalities.exists()) {
	    functionalities.renameTo(new File(mavenProject.getBuild().getOutputDirectory() + File.separatorChar
		    + mavenProject.getArtifactId() + File.separatorChar + ".functionalitiesMappingLog"));
	}

	File rest = new File(mavenProject.getBasedir(), ".restAnnotationLog");
	if (rest.exists()) {
	    rest.renameTo(new File(mavenProject.getBuild().getOutputDirectory() + File.separatorChar
		    + mavenProject.getArtifactId() + File.separatorChar + ".restAnnotationLog"));
	}

	File actionAnnotations = new File(mavenProject.getBasedir(), ".actionAnnotationLog");
	if (actionAnnotations.exists()) {
	    actionAnnotations.renameTo(new File(mavenProject.getBuild().getOutputDirectory() + File.separatorChar
		    + mavenProject.getArtifactId() + File.separatorChar + ".actionAnnotationLog"));
	}
    }

    public static URLClassLoader augmentClassLoader(Log log, MavenProject project) {
	List<String> classpathElements = null;
	try {
	    classpathElements = project.getCompileClasspathElements();
	} catch (DependencyResolutionRequiredException e) {
	    log.error(e);
	}

	URL[] classesURL = new URL[classpathElements.size()];
	int i = 0;

	for (String path : classpathElements) {
	    try {
		classesURL[i++] = new File(path).toURI().toURL();
	    } catch (MalformedURLException e) {
		log.error(e);
	    }
	}

	URLClassLoader loader = new URLClassLoader(classesURL, Thread.currentThread().getContextClassLoader());
	Thread.currentThread().setContextClassLoader(loader);
	return loader;
    }
}
