package module.fileSupport.metadata.parsing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import module.fileSupport.domain.GenericFile;

public class MetadataParserChain {

    public static Set<FileMetaDataParser> parsers = new HashSet<FileMetaDataParser>();

    public static void registerFileParser(FileMetaDataParser parser) {
	parsers.add(parser);
    }

    public static <T extends GenericFile> FileMetadata parseFile(T file) {
	FileMetadata fileMetaData = new FileMetadata();
	for (FileMetaDataParser parser : parsers) {
	    if (parser.isAppliableTo(file)) {
		parser.parse(file, fileMetaData);
	    }
	}
	return fileMetaData;
    }

    public static FileMetadata parseFiles(List<? extends GenericFile> files) {
	FileMetadata fileMetaData = new FileMetadata();
	for (GenericFile file : files) {
	    for (FileMetaDataParser parser : parsers) {
		if (parser.isAppliableTo(file)) {
		    parser.parse(file, fileMetaData);
		}
	    }
	}
	return fileMetaData;
    }

}
