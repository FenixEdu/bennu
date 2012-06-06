package pt.ist.bennu.plugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

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
     * Webapp Directory
     * 
     * @parameter expression="${webappDirectory}"
     *            default-value="${basedir}/src/main/webapp"
     */
    private File webappDirectory;

    /**
     * web-fragment.xml file
     * 
     * @parameter expression="${webFragment}" default-value="${basedir}/src/main/resources/META-INF/web-fragment.xml"
     */
    private File webFragment;

    @Override
    public void execute() throws MojoExecutionException {
        if(mavenProject.getArtifact().getType().equals("pom"))
            return;
	try {
	    String template = FileUtils.readFileToString(webFragment);
	    int start = template.indexOf(TEMPLATE_START);
            int numSpaces = getIndentation(template, start);
	    if (start != -1) {
		int end = template.indexOf(TEMPLATE_END, start);
		if (end != -1) {
		    PrintWriter printWriter = new PrintWriter(webFragment);
		    printWriter.append(template.substring(0, start + TEMPLATE_START.length()));
		    printWriter.append(NEW_LINE_CHAR);
                    printWriter.append(getModuleDependenciesAsStrings(numSpaces));
		    printWriter.append(template.substring(end));
		    printWriter.close();
		} else {
                    throw new MojoExecutionException("Missing template end comment: "+TEMPLATE_END);
                }
	    } else {
                throw new MojoExecutionException("Missing template start comment: "+TEMPLATE_START);
            }
	} catch (IOException e) {
	    throw new MojoExecutionException(null, e);
	}
    }
    
    public String getModuleDependenciesAsStrings(int indentation) throws MojoExecutionException {
        StringBuilder stringBuilder = new StringBuilder();
        String indentSpace = StringUtils.repeat(" ", indentation - 1);
        for(Artifact artifact : mavenProject.getArtifacts()) {
            if(isBennuModule(artifact)) {
                stringBuilder.append(indentSpace);
                stringBuilder.append(OPEN_NAME_TAG);
                stringBuilder.append(artifact.getArtifactId());
                stringBuilder.append(CLOSE_NAME_TAG);
                stringBuilder.append(NEW_LINE_CHAR);
            }
        }
        stringBuilder.append(indentSpace);
        return stringBuilder.toString();
    }
    
    public boolean isBennuModule(Artifact artifact) throws MojoExecutionException {
        if(artifact.getType().equals("pom")) {
            return false; 
        }
        try {
            JarFile artifactJar = new JarFile(artifact.getFile());
            JarEntry webFragmentFile = artifactJar.getJarEntry("META-INF/web-fragment.xml");
            return webFragmentFile != null;
        } catch (IOException ex) {
            throw new MojoExecutionException("Could not load jar file from artifact");
        }
    }

    private int getIndentation(String template, int i) {
        return i - template.lastIndexOf(NEW_LINE_CHAR, i);
    }
}
