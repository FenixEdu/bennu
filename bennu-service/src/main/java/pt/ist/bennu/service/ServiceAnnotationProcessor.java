package pt.ist.bennu.service;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({ "pt.ist.bennu.service.Service" })
public class ServiceAnnotationProcessor extends BennuAbstractProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceAnnotationProcessor.class);

	private static Set<String> entrySet = new HashSet<String>();

	public final static String LOG_FILENAME = ".serviceAnnotationLog";
	public final static String FIELD_SEPARATOR = " ";
	public final static String ENTRY_SEPARATOR = System.getProperty("line.separator");

	@Override
	protected Class<? extends Annotation> getAnnotationClass() {
		return Service.class;
	}

	@Override
	protected void processElementSet(Set<? extends Element> elementSet) {
		for (Element element : elementSet) {
			if (LOG.isDebugEnabled())
				LOG.debug("Processing " + element.getKind().toString().toLowerCase() + " with name " + element.getSimpleName());
			element.getAnnotation(getAnnotationClass());
			Element enclosingElement = element.getEnclosingElement();
			if (enclosingElement instanceof ClassSymbol) {
				String className = processClassName((ClassSymbol) enclosingElement);
				StringBuilder entryBuilder = new StringBuilder();
				entryBuilder.append(className);
				entryBuilder.append(FIELD_SEPARATOR);
				entryBuilder.append(element.getSimpleName().toString());
				entrySet.add(entryBuilder.toString());
			} else {
				LOG.warn("Enclosing element (" + enclosingElement.getSimpleName() + ") of " + element.getSimpleName()
						+ " is not a class");
			}
		}
	}

	private String processClassName(ClassSymbol classSymbol) {
		Symbol symbol = classSymbol.getEnclosingElement();
		if (symbol instanceof ClassSymbol) {
			return processClassName((ClassSymbol) symbol) + "$" + classSymbol.getSimpleName();
		}
		return classSymbol.getQualifiedName().toString();
	}

	@Override
	protected String getLogFilename() {
		return LOG_FILENAME;
	}

	@Override
	protected void writeLogFile(Writer logWriter) throws IOException {
		for (String entry : entrySet) {
			logWriter.write(entry);
			logWriter.write(ENTRY_SEPARATOR);
		}
	}
}
