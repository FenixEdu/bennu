package pt.ist.bennu.bennu.core.rest.mapper;

import javax.ws.rs.core.Response.Status;

public interface RestError {

	public Status getStatus();

	public int getInternalErrorCode();

	public String getMessage();

}
