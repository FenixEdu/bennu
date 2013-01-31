/* 
* @(#)TestCom.java 
* 
* Copyright 2010 Instituto Superior Tecnico 
* Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes 
*  
*      https://fenix-ashes.ist.utl.pt/ 
*  
*   This file is part of the Bennu Web Application Infrastructure. 
* 
*   The Bennu Web Application Infrastructure is free software: you can 
*   redistribute it and/or modify it under the terms of the GNU Lesser General 
*   Public License as published by the Free Software Foundation, either version  
*   3 of the License, or (at your option) any later version. 
* 
*   Bennu is distributed in the hope that it will be useful, 
*   but WITHOUT ANY WARRANTY; without even the implied warranty of 
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
*   GNU Lesser General Public License for more details. 
* 
*   You should have received a copy of the GNU Lesser General Public License 
*   along with Bennu. If not, see <http://www.gnu.org/licenses/>. 
*  
*/
package pt.ist.bennu.core.presentationTier.actions;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.bennu.core.applicationTier.Authenticate.UserView;
import pt.ist.bennu.core.communication.RemoteCallProcessor;
import pt.ist.bennu.core.communication.transport.PrimiviteWithEnumConverter;
import pt.ist.bennu.core.communication.transport.RemoteCallReply;
import pt.ist.bennu.core.communication.transport.RemoteCallRequest;
import pt.ist.bennu.core.communication.transport.converters.Type;
import pt.ist.bennu.core.domain.RoleType;
import pt.ist.bennu.core.domain.User;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/testCom")
/**
 * 
 * @author  Luis Cruz
 * @author  Paulo Abrantes
 * 
 */
public class TestCom extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		final User user = UserView.getCurrentUser();
		if (user == null || !user.hasRoleType(RoleType.MANAGER)) {
			throw new Error("unauthorized.access");
		}
		return super.execute(mapping, form, request, response);
	}

	private static PrimiviteWithEnumConverter enumConverter = new PrimiviteWithEnumConverter();

	public ActionForward invokeMethod(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) throws ClassNotFoundException, IOException {

		String objectId = request.getParameter("id");
		String methodName = request.getParameter("methodName");
		Object[] objects = getObjects(request.getParameter("objectRepresentation"));
		Class[] classes = getClasses(request.getParameter("classesRepresentation"));

		RemoteCallRequest remoteRequest = new RemoteCallRequest(Type.INSTANCE, objectId, methodName, classes, objects);
		RemoteCallReply reply = new RemoteCallReply();
		RemoteCallProcessor.process(remoteRequest, reply);

		response.getOutputStream().write(enumConverter.serializeToSend(reply.getResult()).getBytes());

		return null;
	}

	public ActionForward invokeStatic(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) throws ClassNotFoundException, IOException {
		String className = request.getParameter("className");
		String methodName = request.getParameter("methodName");
		Object[] objects = getObjects(request.getParameter("objectRepresentation"));
		Class[] classes = getClasses(request.getParameter("classesRepresentation"));

		RemoteCallRequest remoteRequest = new RemoteCallRequest(Type.STATIC, className, methodName, classes, objects);
		RemoteCallReply reply = new RemoteCallReply();
		RemoteCallProcessor.process(remoteRequest, reply);

		response.getOutputStream().write(enumConverter.serializeToSend(reply.getResult()).getBytes());

		return null;
	}

	private Object[] getObjects(String representation) {
		return enumConverter.readMultipleObjects(representation);
	}

	private Class[] getClasses(String representation) throws ClassNotFoundException {

		String[] classNames = enumConverter.readMultipleObjects(String.class, representation);
		if (classNames.length == 0) {
			return new Class[] {};
		}
		Class[] classes = new Class[classNames.length];
		for (int i = 0; i < classNames.length; i++) {
			classes[i] = Class.forName(classNames[i]);
		}

		return classes;
	}
}
