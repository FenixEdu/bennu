package myorg.communication.transport;

import java.io.Serializable;

public class RemoteCallReply implements Serializable {

    private Object result;

    public Object getResult() {
	return result;
    }

    public void setResult(Object result) {
	this.result = result;
    }

    public RemoteCallReply() {
	super();
    }

    public RemoteCallReply(Object result) {
	this();
	this.result = result;
    }

}
