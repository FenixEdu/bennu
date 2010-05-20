package myorg.communication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import myorg._development.PropertiesManager;
import myorg.communication.transport.PrimiviteWithEnumConverter;
import pt.ist.fenixWebFramework.FenixWebFramework;
import pt.utl.ist.fenix.tools.util.i18n.Language;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class TestTransport {

    public static void init() {
	final File dir = new File("/Users/ghost/dev/workspace/myorg/build_expenditures/WEB-INF/classes");
	final List<String> urls = new ArrayList<String>();
	for (final File file : dir.listFiles()) {
	    if (file.isFile() && file.getName().endsWith(".dml")) {
		try {
		    urls.add(file.getCanonicalPath());
		} catch (IOException e) {
		    e.printStackTrace();
		    throw new RuntimeException(e);
		}
	    }
	}

	Collections.sort(urls);
	final String[] paths = new String[urls.size()];
	for (int i = 0; i < urls.size(); i++) {
	    paths[i] = urls.get(i);
	}
	try {
	    FenixWebFramework.initialize(PropertiesManager.getFenixFrameworkConfig(paths));
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    public static void main(String[] args) {

	init();

	PrimiviteWithEnumConverter test = new PrimiviteWithEnumConverter();
	// String representation = test.serializeToSend(Boolean.FALSE);
	// System.out.println(representation);
	// Boolean x = test.readObject(representation);
	// System.out.println(x);
	//
	// String representation2 = test.serializeToSend(Boolean.FALSE,
	// Boolean.TRUE, "bla blabla", 12421);
	// Object[] objects = test.readMultipleObjects(representation2);
	// for (Object object : objects) {
	// System.out.println(object.getClass() + " : " + object);
	// }
	//
	// System.out.println(Type.INSTANCE);
	// String representationX = test.serializeToSend(Type.INSTANCE);
	// System.out.println(representationX);
	//
	// Type type = test.readEnum(representationX);
	// System.out.println(type);

	MultiLanguageString mls = new MultiLanguageString();
	mls.setContent(Language.pt, "weeee");
	mls.setContent(Language.en, "weeeehhh");

	String serializeToSend = test.serializeToSend(mls);
	System.out.println(serializeToSend);

	MultiLanguageString mls2 = test.readValueType(serializeToSend);
	for (String content : mls2.getAllContents()) {
	    System.out.println(content);
	}

	System.out.println(mls2.getContent(Language.pt));
	System.out.println(mls2.getContent(Language.en));
    }
}
