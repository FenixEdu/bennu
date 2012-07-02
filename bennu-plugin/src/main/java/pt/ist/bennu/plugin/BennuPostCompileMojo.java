package pt.ist.bennu.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import pt.ist.fenixWebFramework.services.ServiceAnnotationInjector;

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
	URLClassLoader loader = augmentClassLoader(getLog(), mavenProject);
	ServiceAnnotationInjector.inject(classesDirectory, loader);
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
