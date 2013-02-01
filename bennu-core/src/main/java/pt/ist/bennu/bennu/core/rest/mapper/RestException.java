package pt.ist.bennu.bennu.core.rest.mapper;

import javax.ws.rs.WebApplicationException;

import com.google.gson.JsonObject;

public class RestException extends WebApplicationException {

	private static final long serialVersionUID = 1L;

	private final RestError error;

	public RestException(RestError error) {
		this.error = error;
	}

	public RestError getError() {
		return error;
	}

	public final String asJsonString() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("internalErrorCode", error.getInternalErrorCode());
		jsonObject.addProperty("message", error.getMessage());
		return jsonObject.toString();
	}

}
