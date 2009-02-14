package myorg.util;

import java.io.InputStream;
import java.io.Serializable;

public class FileUploadBean implements Serializable {


    private transient InputStream inputStream;
    private String filename;
    private String displayName;

    public FileUploadBean() {
    }

    public InputStream getInputStream() {
	return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
	this.inputStream = inputStream;
    }

    public String getFilename() {
	return filename;
    }

    public void setFilename(String filename) {
	this.filename = filename;
    }

    public String getDisplayName() {
	return displayName;
    }

    public void setDisplayName(String displayName) {
	this.displayName = displayName;
    }

}
