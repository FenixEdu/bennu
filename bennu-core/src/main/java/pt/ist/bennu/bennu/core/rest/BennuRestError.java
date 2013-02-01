package pt.ist.bennu.bennu.core.rest;

import javax.ws.rs.core.Response.Status;

import com.google.gson.JsonObject;

public enum BennuRestError implements RestError {

	UNAUTHORIZED(Status.UNAUTHORIZED, 1000, "You must authenticate first"), RESOURCE_NOT_FOUND(Status.NOT_FOUND, 1001,
			"The requested resource was not found."), CANNOT_CREATE_ENTITY(Status.INTERNAL_SERVER_ERROR, 1002,
			"Error while creating resource"), RESOURSE_TYPE_MISMATCH(Status.BAD_REQUEST, 1003, "Mismatch on resource types"),
	DESERIALIZER_NOT_FOUND(Status.INTERNAL_SERVER_ERROR, 1004, "Deserializer not found"), BAD_REQUEST(Status.BAD_REQUEST, 1005,
			"Bad request"), SERIALIZER_NOT_FOUND(Status.INTERNAL_SERVER_ERROR, 1006, "Serializer not found");

	private Status status;
	private int internalErrorCode;
	private String message;

	private BennuRestError(Status status, int internalErrorCode, String message) {
		this.status = status;
		this.internalErrorCode = internalErrorCode;
		this.message = message;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public int getInternalErrorCode() {
		return internalErrorCode;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public String asJsonString() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("internalCode", BennuRestError.RESOURCE_NOT_FOUND.getInternalErrorCode());
		jsonObject.addProperty("message", BennuRestError.RESOURCE_NOT_FOUND.getMessage());
		return jsonObject.toString();
	}
}
