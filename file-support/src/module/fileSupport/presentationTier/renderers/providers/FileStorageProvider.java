/**
 * 
 */
package module.fileSupport.presentationTier.renderers.providers;

import myorg.domain.MyOrg;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

/**
 * @author Shezad Anavarali Date: Jul 21, 2009
 * 
 */
public class FileStorageProvider implements DataProvider {

    @Override
    public Converter getConverter() {
	return null;
    }

    @Override
    public Object provide(Object arg0, Object arg1) {
	return MyOrg.getInstance().getFileStorages();
    }

}
