package module.fileSupport.metadata.parsing;

import java.util.HashMap;
import java.util.Set;

public class FileMetadata {

    private HashMap<String, String> metaData;

    public FileMetadata() {
	this.metaData = new HashMap<String, String>();
    }

    public void addMetaData(String propertyName, String value) {
	String currentValue = this.metaData.get(propertyName);
	if (currentValue == null) {
	    this.metaData.put(propertyName, value);
	} else {
	    StringBuilder builder = new StringBuilder(currentValue);
	    builder.append(" ");
	    builder.append(value);
	    this.metaData.remove(propertyName);
	    this.metaData.put(propertyName, builder.toString());
	}

    }

    public void addMetaData(FileMetadata data) {
	metaData.putAll(data.metaData);
    }

    public HashMap<String, String> getMetaDataMap() {
	return this.metaData;
    }

    public boolean hasContent() {
	return !this.metaData.isEmpty();
    }

    public Set<String> keySet() {
	return this.metaData.keySet();
    }

    public String getObject(String propertyName) {
	return this.metaData.get(propertyName);
    }
}
