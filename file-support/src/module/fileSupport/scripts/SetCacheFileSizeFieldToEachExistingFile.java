/**
 * 
 */
package module.fileSupport.scripts;

import myorg.domain.scheduler.WriteCustomTask;

import org.apache.log4j.Logger;

import pt.ist.fenixframework.plugins.fileSupport.domain.FileSupport;
import pt.ist.fenixframework.plugins.fileSupport.domain.GenericFile;

/**
 * Migration script to set all of the size field used as cache to the size to
 * the correct value
 * 
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
public class SetCacheFileSizeFieldToEachExistingFile extends WriteCustomTask {

    private static final Logger LOGGER = Logger.getLogger(SetCacheFileSizeFieldToEachExistingFile.class);
    /* (non-Javadoc)
     * @see myorg.domain.scheduler.WriteCustomTask#doService()
     */
    @Override
    protected void doService() {
	int countTouchedSizes = 0;
	int countGenericFiles = 0;
	for (GenericFile genericFile : FileSupport.getInstance().getGenericFiles()) {
	    countGenericFiles++;
	    if (genericFile.getSize() == null) {
		long size = (genericFile.getContent() == null) ? 0 : genericFile.getContent().length;
		genericFile.setSize(size);
		countTouchedSizes++;
	    }
	}
	
	LOGGER.info("Touched " + countTouchedSizes + " out of " + countGenericFiles + " GenericFile instances");
	out.println("Touched " + countTouchedSizes + " out of " + countGenericFiles + " GenericFile instances");

    }

}
