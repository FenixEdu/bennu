/**
 * 
 */
package module.fileSupport.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.fileSupport.domain.FileStorageConfiguration;
import myorg.domain.MyOrg;
import myorg.presentationTier.actions.ContextBaseAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Mapping;

/**
 * 
 * @author Shezad Anavarali Date: Jul 20, 2009
 * 
 */
@Mapping(path = "/storageConfigurationManagement")
public class StorageConfigurationManagement extends ContextBaseAction {

    public ActionForward prepare(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	FileStorageConfiguration.createMissingStorageConfigurations();
	request.setAttribute("storageConfigurations", MyOrg.getInstance().getFileStorageConfigurations());

	return forward(request, "/fileSupport/storageConfigurationManagement.jsp");
    }

}
