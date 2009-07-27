/**
 * 
 */
package module.fileSupport.dto;

import java.io.Serializable;

/**
 * @author Shezad Anavarali Date: Jul 20, 2009
 * 
 */
abstract public class FileStorageDTO implements Serializable {

    String name;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

}
