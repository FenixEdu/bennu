/**
 * 
 */
package module.fileSupport.dto;

/**
 * @author Shezad Anavarali Date: Jul 20, 2009
 * 
 */
public class LocalFileSystemStorageDTO extends FileStorageDTO {

    String path;
    Integer treeDirectoriesNameLength;

    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    public Integer getTreeDirectoriesNameLength() {
	return treeDirectoriesNameLength;
    }

    public void setTreeDirectoriesNameLength(Integer treeDirectoriesNameLength) {
	this.treeDirectoriesNameLength = treeDirectoriesNameLength;
    }

}
