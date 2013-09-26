package pt.ist.fenixWebFramework.servlets.functionalities;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes(value = { "pt.ist.fenixWebFramework.servlets.functionalities.Functionality" })
public class FunctionalityAnnotationProcessor extends AbstractProcessor {

    public static final String FUNCTIONALITIES_FILE = ".functionalitiesMappingLog";
    private static final String EMPTY_LINK = "-";
    private static final String SEPERATOR = ",";
    private static final Set<String> existingLinks = new HashSet<String>();

    @Override
    public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
        for (Element elem : env.getElementsAnnotatedWith(Functionality.class)) {
            Functionality functionality = elem.getAnnotation(Functionality.class);
            if (functionality == null) {
                continue;
            }

            String relativeLink = functionality.relativeLink();
            String mappingPath = elem.getEnclosingElement().getAnnotation(Mapping.class).path();
            String methodName = elem.getSimpleName().toString();

            String link = mappingPath + relativeLink;

            if (existingLinks.contains(link)) {
                throw new RuntimeException("Functionality in action: " + elem.getEnclosingElement().getSimpleName()
                        + " in method " + methodName + " has a semantic link that already exists");
            }
            existingLinks.add(link);

            try {
                appendToFile(mappingPath, methodName, relativeLink);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private void appendToFile(String... strings) throws IOException {
        FileWriter writer = new FileWriter(FUNCTIONALITIES_FILE, true);

        int i = strings.length;
        for (String str : strings) {
            if (isEmpty(str)) {
                writer.append(EMPTY_LINK);
            } else {
                writer.append(str);
            }
            i--;
            if (i > 0) {
                writer.append(SEPERATOR);
            } else {
                writer.append("\n");
            }
        }

        writer.close();
    }

    private boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        return str.isEmpty();
    }

}
