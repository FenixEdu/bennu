/* 
* @(#)RemoteCallProcessor.java 
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
package myorg.communication;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import myorg.communication.transport.RemoteCallReply;
import myorg.communication.transport.RemoteCallRequest;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.ist.fenixframework.pstm.VersionNotAvailableException;

/**
 * 
 * @author  Paulo Abrantes
 * 
*/
public class RemoteCallProcessor {

    public static class RemoteCallException extends RuntimeException {

	public RemoteCallException(String message) {
	    super(message);
	}

	public RemoteCallException(Exception e) {
	    super(e);
	}
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface RemoteCall {

    }

    public static RemoteCallReply process(final RemoteCallRequest request, final RemoteCallReply reply) {

	Object result;

	switch (request.getType()) {
	case INSTANCE:
	    result = invokeMethod(request.getIdentifier(), request.getMethodName(), request.getClasses(), request.getClasses());
	    break;
	case STATIC:
	    result = invokeStatic(request.getIdentifier(), request.getMethodName(), request.getClasses(), request.getClasses());
	    break;
	default:
	    throw new RemoteCallException("Invalid request type");
	}

	reply.setResult(result);

	return reply;
    }

    public static Object invokeMethod(String externalId, String methodName, Class[] classes, Object[] objects) {

	DomainObject someObject = AbstractDomainObject.fromExternalId(externalId);
	try {
	    Method methodToCall = someObject.getClass().getMethod(methodName, classes);

	    if (!methodAllowsRemoteExecution(methodToCall)) {
		throw new RemoteCallException("Request method is not annotated with @RemoteClass");
	    }
	    Object result = methodToCall.invoke(someObject, objects);

	    return result;
	} catch (SecurityException e) {
	    e.printStackTrace();
	    throw new RemoteCallException(e);
	} catch (NoSuchMethodException e) {
	    e.printStackTrace();
	    throw new RemoteCallException(e);
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	    throw new RemoteCallException(e);
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	    throw new RemoteCallException(e);
	} catch (InvocationTargetException e) {
	    if (e.getCause() instanceof VersionNotAvailableException) {
		throw (VersionNotAvailableException) e.getCause();
	    }
	    e.printStackTrace();
	    throw new RemoteCallException(e);
	}
    }

    public static Object invokeStatic(String className, String methodName, Class[] classes, Object[] objects) {
	Class<?> someClass;
	try {
	    someClass = Class.forName(className);
	    if (!AbstractDomainObject.class.isAssignableFrom(someClass)) {
		throw new RemoteCallException("invokeStatic can only be called in domain objects");
	    }

	    Method methodToCall = someClass.getMethod(methodName, classes);
	    if (!methodAllowsRemoteExecution(methodToCall)) {
		throw new RemoteCallException("Request method is not annotated with @RemoteClass");
	    }

	    Object result = methodToCall.invoke(null, objects);

	    return result;

	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	    throw new RemoteCallException(e);
	} catch (SecurityException e) {
	    e.printStackTrace();
	    throw new RemoteCallException(e);
	} catch (NoSuchMethodException e) {
	    e.printStackTrace();
	    throw new RemoteCallException(e);
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	    throw new RemoteCallException(e);
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	    throw new RemoteCallException(e);
	} catch (InvocationTargetException e) {
	    e.printStackTrace();
	    throw new RemoteCallException(e);
	}
    }

    private static boolean methodAllowsRemoteExecution(Method methodToCall) {
	RemoteCall annotation = methodToCall.getAnnotation(RemoteCall.class);
	return annotation != null;
    }
}
