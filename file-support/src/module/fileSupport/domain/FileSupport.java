package module.fileSupport.domain;

import module.fileSupport.metadata.parsing.MetadataParserChain;
import module.fileSupport.metadata.parsing.parsers.ExcelParser;
import module.fileSupport.metadata.parsing.parsers.PDFParser;
import module.fileSupport.metadata.parsing.parsers.PowerPointParser;
import module.fileSupport.metadata.parsing.parsers.TextParser;
import module.fileSupport.metadata.parsing.parsers.WordParser;
import myorg.domain.ModuleInitializer;
import myorg.domain.MyOrg;
import pt.ist.fenixWebFramework.services.Service;

public class FileSupport extends FileSupport_Base implements ModuleInitializer {

    private static boolean isInitialized = false;

    private static ThreadLocal<FileSupport> init = null;

    private FileSupport() {
	setMyOrg(MyOrg.getInstance());
    }

    public static FileSupport getInstance() {
	if (init != null) {
	    return init.get();
	}

	if (!isInitialized) {
	    initialize();
	}
	final MyOrg myOrg = MyOrg.getInstance();
	return myOrg.getFileSupport();
    }

    @Service
    public synchronized static void initialize() {
	if (!isInitialized) {
	    try {
		final MyOrg myOrg = MyOrg.getInstance();
		final FileSupport controller = myOrg.getFileSupport();
		if (controller == null) {
		    new FileSupport();
		}
		init = new ThreadLocal<FileSupport>();
		init.set(myOrg.getFileSupport());

		isInitialized = true;
	    } finally {
		init = null;
	    }
	}
    }

    @Override
    public void init(MyOrg root) {
	MetadataParserChain.registerFileParser(new PDFParser());
	MetadataParserChain.registerFileParser(new TextParser());
	MetadataParserChain.registerFileParser(new WordParser());
	MetadataParserChain.registerFileParser(new ExcelParser());
	MetadataParserChain.registerFileParser(new PowerPointParser());
    }

}
