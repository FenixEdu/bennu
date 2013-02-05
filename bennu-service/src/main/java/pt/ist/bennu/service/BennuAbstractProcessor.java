package pt.ist.bennu.service;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BennuAbstractProcessor extends AbstractProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(BennuAbstractProcessor.class);

    @Override
    public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement typeElement : annotations) {
            Set<? extends Element> elementSet = roundEnv.getElementsAnnotatedWith(typeElement);
            processElementSet(elementSet);
            LOG.info("Annotation processing round finished.");
            LOG.info("Writing to log file: " + getLogFilename());
            Filer filer = processingEnv.getFiler();
            try (Writer logFileWriter = filer.createResource(StandardLocation.CLASS_OUTPUT, "", getLogFilename()).openWriter()) {
                writeLogFile(logFileWriter);
            } catch (Exception e) {
                throw new Error(e);
            }
        }
        return true;
    }

    /**
     * This method is called whenever there will not be another processing round. This means that the annotation log file can be
     * written.
     * 
     * @param logWriter
     *            the writer to which the entries must be written.
     * @throws IOException
     *             when the processor has problems in writing to the log file.
     */
    protected abstract void writeLogFile(Writer logWriter) throws IOException;

    /**
     * Obtains the annotation class used to retrieve from the round environment.
     * 
     * @return the annotation class
     */
    protected abstract Class<? extends Annotation> getAnnotationClass();

    /**
     * This method is called whenever the processing of a round runs and finds a set of annotated elements with the provided
     * annotation class.
     * 
     * @param elementSet
     *            the set of elements annotated with the provided annotation class.
     */
    protected abstract void processElementSet(Set<? extends Element> elementSet);

    /**
     * Obtains the log filename that will be generated from the processing of this annotation.
     * 
     * @return the name of the log filename that will be created.
     */
    protected abstract String getLogFilename();

}
