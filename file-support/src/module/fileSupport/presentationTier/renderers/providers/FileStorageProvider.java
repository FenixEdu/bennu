/**
 * 
 */
package module.fileSupport.presentationTier.renderers.providers;

import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixframework.plugins.fileSupport.domain.FileSupport;

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
	return FileSupport.getInstance().getFileStorages();
    }

}
