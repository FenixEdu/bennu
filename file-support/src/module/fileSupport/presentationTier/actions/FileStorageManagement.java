/**
 * 
 */
package module.fileSupport.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.fileSupport.dto.DBStorageDTO;
import module.fileSupport.dto.DomainStorageDTO;
import module.fileSupport.dto.LocalFileSystemStorageDTO;
import myorg.presentationTier.actions.ContextBaseAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.fenixframework.plugins.fileSupport.domain.FileStorage;
import pt.ist.fenixframework.plugins.fileSupport.domain.FileSupport;
import pt.ist.fenixframework.plugins.fileSupport.domain.GenericFile;

/**
 * @author Shezad Anavarali Date: Jul 16, 2009
 * 
 */
@Mapping(path = "/fileStorageManagement")
public class FileStorageManagement extends ContextBaseAction {

    public ActionForward prepare(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {
	request.setAttribute("fileStorages", FileSupport.getInstance().getFileStorages());
	request.setAttribute("domainStorage", new DomainStorageDTO());
	request.setAttribute("localFileSystemStorage", new LocalFileSystemStorageDTO());
	request.setAttribute("dbStorage", new DBStorageDTO());
	return forward(request, "/fileSupport/fileStorageManagement.jsp");
    }

    public ActionForward deleteStorage(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	final FileStorage storage = getDomainObject(request, "storageOID");
	storage.delete();
	return prepare(mapping, form, request, response);
    }

    public ActionForward convertFileStorage(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	final FileStorage storage = getDomainObject(request, "storageOID");
	GenericFile.convertFileStorages(storage);

	return prepare(mapping, form, request, response);
    }

    public ActionForward createDomainStorage(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) {

	DomainStorageDTO domainStorageDTO = (DomainStorageDTO) getRenderedObject("domainStorage");
	ServiceHack.createDomainStorage(domainStorageDTO);
	RenderUtils.invalidateViewState();
	return prepare(mapping, form, request, response);
    }

    public ActionForward createDBStorage(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	DBStorageDTO dbStorageDTO = (DBStorageDTO) getRenderedObject("dbStorage");
	ServiceHack.createDBStorage(dbStorageDTO);
	RenderUtils.invalidateViewState();
	return prepare(mapping, form, request, response);
    }

    public ActionForward createLocalFileSystemStorage(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) {

	LocalFileSystemStorageDTO fdStorageDTO = (LocalFileSystemStorageDTO) getRenderedObject("localFileSystemStorage");
	ServiceHack.createLocalFileSystemStorage(fdStorageDTO);
	RenderUtils.invalidateViewState();
	return prepare(mapping, form, request, response);
    }

}
