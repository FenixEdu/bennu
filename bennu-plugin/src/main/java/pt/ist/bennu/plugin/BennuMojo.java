package pt.ist.bennu.plugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspC;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Generate base classes from the DML files
 * 
 * @goal generate-jsp
 * 
 * @phase generate-sources
 * 
 * @requiresDependencyResolution runtime
 */
public class BennuMojo extends AbstractMojo {
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
     * Output Directory
     * 
     * @parameter expression="${outputDirectory}" default-value=
     *            "${project.build.directory}/generated-sources/jsp"
     */
    private File outputDirectory;

    /**
     * web-fragment.xml file
     * 
     * @parameter expression="${webFragment}" default-value=
     *            "${basedir}/src/main/resources/META-INF/web-fragment.xml"
     */
    private File webFragment;

    @Override
    public void execute() throws MojoExecutionException {
	try {
	    Resource resource = new Resource();
	    resource.setDirectory(webappDirectory.getAbsolutePath());
	    resource.addInclude("*.jsp");
	    mavenProject.addResource(resource);

	    JspC compiler = new JspC();
	    compiler.setUriroot(webappDirectory.getAbsolutePath());
	    compiler.setOutputDir(outputDirectory.getAbsolutePath());
	    compiler.setWebXmlFragment(mavenProject.getBuild().getDirectory() + "/web-fragment.xml");

	    compiler.execute();
	    if (!webFragment.exists()) {
		IOUtils.copy(this.getClass().getResourceAsStream("/web-fragment-template.xml"), new PrintWriter(webFragment));
	    }

	    String template = FileUtils.readFileToString(webFragment);
	    int start = template.indexOf(TEMPLATE_START);
	    if (start != -1) {
		int end = template.indexOf(TEMPLATE_END, start);
		if (end != -1) {
		    PrintWriter result = new PrintWriter(webFragment);
		    result.append(template.substring(0, start + TEMPLATE_START.length()));
		    result.append(FileUtils.readFileToString(new File(mavenProject.getBuild().getDirectory()
			    + "/web-fragment.xml")));
		    result.append(template.substring(end));
		    result.close();
		}
	    }

	    // outputDirectory.setLastModified(System.currentTimeMillis());
	    mavenProject.addCompileSourceRoot(outputDirectory.getAbsolutePath());
	} catch (JasperException e) {
	    throw new MojoExecutionException(null, e);
	} catch (IOException e) {
	    throw new MojoExecutionException(null, e);
	}
    }
}
