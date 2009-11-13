package module.fileSupport.domain;

public class TestFile extends TestFile_Base {
    
    public  TestFile() {
        super();
    }
    
    public TestFile(String filename, byte[] contents){
	this();
	setFilename(filename);
	setContent(contents);
    }
    
}
