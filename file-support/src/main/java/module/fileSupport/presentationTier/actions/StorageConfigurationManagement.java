/*
 * @(#)StorageConfigurationManagement.java
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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.bennu.core.presentationTier.actions.ContextBaseAction;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.plugins.fileSupport.domain.FileStorageConfiguration;
import pt.ist.fenixframework.plugins.fileSupport.domain.FileSupport;

/**
 * 
 * @author João Neves
 * @author Shezad Anavarali
 * @author Paulo Abrantes
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

    @Atomic
    private void createMissingConfigurations() {
        FileStorageConfiguration.createMissingStorageConfigurations();
    }

}
