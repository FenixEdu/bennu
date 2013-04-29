package pt.ist.bennu.plugin;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
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

    @Override
    public void execute() throws MojoExecutionException {
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

}
