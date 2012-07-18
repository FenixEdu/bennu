/*
 * @(#)FileStorageManagement.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: Shezad Anavarali
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the File Support Module.
 *
 *   The File Support Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The File Support Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the File Support Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.fileSupport.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.fileSupport.dto.DBStorageDTO;
import module.fileSupport.dto.DomainStorageDTO;
import module.fileSupport.dto.LocalFileSystemStorageDTO;
import pt.ist.bennu.core.presentationTier.actions.ContextBaseAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.fenixframework.plugins.fileSupport.domain.FileStorage;
import pt.ist.fenixframework.plugins.fileSupport.domain.FileSupport;
import pt.ist.fenixframework.plugins.fileSupport.domain.GenericFile;

/**
 * 
 * @author Shezad Anavarali
 * @author Sérgio Silva
 * @author Paulo Abrantes
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
