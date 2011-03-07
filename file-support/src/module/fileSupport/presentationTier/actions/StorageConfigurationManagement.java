/**
 * 
 */
package module.fileSupport.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.presentationTier.actions.ContextBaseAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.fenixframework.plugins.fileSupport.domain.FileStorageConfiguration;
import pt.ist.fenixframework.plugins.fileSupport.domain.FileSupport;

/**
 * 
 * @author Shezad Anavarali Date: Jul 20, 2009
 * 
 */
@Mapping(path = "/storageConfigurationManagement")
public class StorageConfigurationManagement extends ContextBaseAction {

    public ActionForward prepare(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	RenderUtils.invalidateViewState();
	createMissingConfigurations();
	request.setAttribute("storageConfigurations", FileSupport.getInstance().getFileStorageConfigurations());

	return forward(request, "/fileSupport/storageConfigurationManagement.jsp");
    }

    @Service
    private void createMissingConfigurations() {
	FileStorageConfiguration.createMissingStorageConfigurations();
    }

}
