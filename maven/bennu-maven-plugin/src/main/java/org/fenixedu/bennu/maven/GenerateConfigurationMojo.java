package org.fenixedu.bennu.maven;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

@Mojo(name = "generate-configuration", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class GenerateConfigurationMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(property = "bennu.config.location", defaultValue = "src/main/resources")
    private File location;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Generating configuration.properties for project " + project.getArtifactId());

        try {
            File outputFile = new File(location, "configuration.properties");
            if (outputFile.exists()) {
                getLog().error(outputFile + " exists, not generating!");
                throw new MojoFailureException("Output file already exists");
            }
            Files.write(generateConfiguration(), outputFile, Charset.defaultCharset());
            getLog().info("Written configuration to: " + outputFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not write properties to file", e);
        }
    }

    private String generateConfiguration() throws MojoExecutionException {
        Reflections reflections = new ConfigurationBuilder().filterInputsBy(Predicates.alwaysTrue()).setUrls(getURLs()).build();

        StringBuilder properties = new StringBuilder();
        for (Class<?> type : reflections.getTypesAnnotatedWith(ConfigurationManager.class)) {
            properties.append("################### Properties for "
                    + type.getAnnotation(ConfigurationManager.class).description() + " ###################\n\n");
            for (Method method : sortedMethods(type)) {
                ConfigurationProperty property = method.getAnnotation(ConfigurationProperty.class);
                if (!property.description().isEmpty()) {
                    properties.append("# ");
                    properties.append(property.description().replace("\n", "\n#"));
                    properties.append('\n');
                }
                properties.append(property.key());
                properties.append(" = ");
                properties.append(property.defaultValue().equals(ConfigurationInvocationHandler.NULL_DEFAULT) ? "" : property
                        .defaultValue());
                properties.append('\n');
            }
            properties.append("\n\n");
        }

        return properties.toString();
    }

    private Collection<URL> getURLs() throws MojoExecutionException {
        try {
            Collection<URL> urls = new ArrayList<>(project.getArtifacts().size() + 1);
            for (Artifact artifact : project.getArtifacts()) {
                urls.add(artifact.getFile().toURI().toURL());
            }
            urls.add(new File(project.getBuild().getOutputDirectory()).toURI().toURL());

            URLClassLoader loader =
                    URLClassLoader
                            .newInstance(urls.toArray(new URL[urls.size()]), Thread.currentThread().getContextClassLoader());
            Thread.currentThread().setContextClassLoader(loader);
            return urls;
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Could not properly define the classloader", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Iterable<Method> sortedMethods(Class<?> type) {
        SortedSet<Method> methods = Sets.newTreeSet(new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                return o1.getAnnotation(ConfigurationProperty.class).key()
                        .compareTo(o2.getAnnotation(ConfigurationProperty.class).key());
            }
        });
        for (Method method : ReflectionUtils.getAllMethods(type)) {
            if (method.isAnnotationPresent(ConfigurationProperty.class)) {
                methods.add(method);
            }
        }
        return methods;
    }
}
