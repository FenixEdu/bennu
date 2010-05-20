package myorg.presentationTier.actions;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.communication.RemoteCallProcessor;
import myorg.communication.transport.PrimiviteWithEnumConverter;
import myorg.communication.transport.RemoteCallReply;
import myorg.communication.transport.RemoteCallRequest;
import myorg.communication.transport.converters.Type;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/testCom")
public class TestCom extends BaseAction {

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
