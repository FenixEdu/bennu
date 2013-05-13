package pt.ist.bennu.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.StringUtils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * Generate base classes from the DML files
 * 
 * @goal update-web-fragment
 * 
 * @phase generate-sources
 * 
 * @requiresDependencyResolution runtime
 */
public class BennuMojo extends AbstractMojo {

    private static final String OPEN_NAME_TAG = "<name>";
    private static final String CLOSE_NAME_TAG = "</name>";
    private static final String NEW_LINE_CHAR = "\n";

    private static final String TEMPLATE_START = "<!-- [START OF BENNU GENERATED FRAGMENT] -->";

    private static final String TEMPLATE_END = "<!-- [END OF BENNU GENERATED FRAGMENT] -->";

    /**
     * Maven Project
     * 
     * @parameter default-value="${project}"
     */
    private MavenProject mavenProject;

    /**
     * web-fragment.xml file
     * 
     * @parameter expression="${webFragment}" default-value="${basedir}/src/main/resources/META-INF/web-fragment.xml"
     */
    private File webFragment;

    @Override
    public void execute() throws MojoExecutionException {
        if (mavenProject.getArtifact().getType().equals("jar")) {
            if (webFragment.exists()) {
                try {
                    if (webFragment.exists()) {
                        String template = Files.toString(webFragment, Charsets.UTF_8);
                        int start = template.indexOf(TEMPLATE_START);
                        if (start != -1) {
                            int end = template.indexOf(TEMPLATE_END, start);
                            if (end != -1) {
                                StringBuilder output = new StringBuilder();
                                output.append(template.substring(0, start + TEMPLATE_START.length()));
                                output.append(NEW_LINE_CHAR);
                                fillModuleDependencies(output, getIndentation(template, start));
                                output.append(template.substring(end));
                                File metaInf =
                                        new File(mavenProject.getBuild().getOutputDirectory() + File.separatorChar + "META-INF");
                                metaInf.mkdirs();
                                Files.write(output.toString(), new File(metaInf, "web-fragment.xml"), Charsets.UTF_8);
                            } else {
                                throw new MojoExecutionException("Missing template end comment: " + TEMPLATE_END);
                            }
                        } else {
                            throw new MojoExecutionException("Missing template start comment: " + TEMPLATE_START);
                        }
                    } else {
                        getLog().info("File: " + webFragment.getAbsolutePath() + " not found. No depency injection could be made");
                    }
                } catch (IOException e) {
                    throw new MojoExecutionException(null, e);
                }
            }

            try {
                List<Resource> resources = mavenProject.getResources();
                StringBuilder messages = new StringBuilder();
                for (Resource resource : resources) {
                    DirectoryScanner scanner = new DirectoryScanner();
                    scanner.setBasedir(resource.getDirectory());
                    scanner.setIncludes(new String[] { "resources/*.properties" });
                    scanner.scan();

                    for (String resourceFile : scanner.getIncludedFiles()) {
                        if (!resourceFile.contains("_")) {
                            messages.append(resourceFile.substring("resources/".length(),
                                    resourceFile.length() - ".properties".length()));
                            messages.append("\n");
                        }
                    }
                }
                if (messages.length() != 0) {
                    String output =
                            mavenProject.getBuild().getOutputDirectory() + File.separatorChar + mavenProject.getArtifactId()
                                    + File.separatorChar + ".messageResources";
                    Files.write(messages.toString(), new File(output), Charsets.UTF_8);
                }
            } catch (IOException e) {
                throw new MojoExecutionException(null, e);
            }
        }
    }

    public void fillModuleDependencies(StringBuilder output, int indentation) throws MojoExecutionException {
        String indentSpace = StringUtils.repeat(" ", indentation - 1);
        for (Artifact artifact : mavenProject.getArtifacts()) {
            if (isWebModule(artifact)) {
                output.append(indentSpace);
                output.append(OPEN_NAME_TAG);
                output.append(artifact.getArtifactId());
                output.append(CLOSE_NAME_TAG);
                output.append(NEW_LINE_CHAR);
            }
        }
        output.append(indentSpace);
    }

    private boolean isWebModule(Artifact artifact) throws MojoExecutionException {
        if (artifact.getType().equals("jar")) {
            try (JarFile artifactJar = new JarFile(artifact.getFile())) {
                JarEntry webFragmentFile = artifactJar.getJarEntry("META-INF/web-fragment.xml");
                return webFragmentFile != null;
            } catch (IOException ex) {
                throw new MojoExecutionException("Could not load jar file from artifact");
            }
        }
        return false;
    }

    private int getIndentation(String template, int i) {
        return i - template.lastIndexOf(NEW_LINE_CHAR, i);
    }
}
