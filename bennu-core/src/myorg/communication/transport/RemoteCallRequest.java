package myorg.communication.transport;

import java.io.Serializable;

import myorg.communication.transport.converters.Type;

public class RemoteCallRequest implements Serializable {

    private Type type;
    private String identifier;
    private String methodName;
    private Class[] classes;
    private Object[] objects;

    public RemoteCallRequest(Type type, String identifier, String methodName, Class[] classes, Object[] objects) {
	super();
	this.type = type;
	this.identifier = identifier;
	this.methodName = methodName;
	this.classes = classes;
	this.objects = objects;
    }

    public Type getType() {
	return type;
    }

    public void setType(Type type) {
	this.type = type;
    }

    public String getIdentifier() {
	return identifier;
    }

    public void setIdentifier(String identifier) {
	this.identifier = identifier;
    }

    public String getMethodName() {
	return methodName;
    }

    public void setMethodName(String methodName) {
	this.methodName = methodName;
    }

    public Class[] getClasses() {
	return classes;
    }

    public void setClasses(Class[] classes) {
	this.classes = classes;
    }

    public Object[] getObjects() {
	return objects;
    }

    public void setObjects(Object[] objects) {
	this.objects = objects;
    }
}
