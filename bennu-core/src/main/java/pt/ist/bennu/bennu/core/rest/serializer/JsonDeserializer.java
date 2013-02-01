package pt.ist.bennu.bennu.core.rest.serializer;

import pt.ist.bennu.bennu.core.rest.BennuRestError;
import pt.ist.bennu.bennu.core.rest.RestException;
import pt.ist.bennu.core.domain.User;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class JsonDeserializer implements Deserializer {

	private Deserializer delegate;

	@Override
	public <T> T deserialize(String jsonString, Class<T> type, String externalId) {
		T modifiedObject = AbstractDomainObject.fromExternalId(externalId);
		if (modifiedObject.getClass().equals(type)) {
			updateObject(type.cast(modifiedObject), jsonString, externalId);
			return modifiedObject;
		} else {
			throw new RestException(BennuRestError.RESOURSE_TYPE_MISMATCH);
		}
	}

	@Override
	public <T> T deserialize(String jsonString, Class<T> type) {
		if (type.equals(User.class)) {
			JsonObject jsonObject = parseJsonObject(jsonString);
			User user = new User(jsonObject.get("username").getAsString());
			return (T) user;
		} else {
			throw new RestException(BennuRestError.DESERIALIZER_NOT_FOUND);
		}
	}

	@Override
	public void updateObject(Object object, String jsonString, String externalId) {
		if (delegate != null) {
			delegate.deserialize(jsonString, object.getClass(), externalId);
		} else {
			throw new RestException(BennuRestError.DESERIALIZER_NOT_FOUND);
		}
	}

	public void updateObject(User user, String jsonString, String externalId) {
		JsonObject jsonObject = parseJsonObject(jsonString);
		if (jsonObject.has("email")) {
			user.setEmail(jsonObject.get("email").getAsString());
		}
	}

	private JsonObject parseJsonObject(String jsonString) {
		try {
			return new JsonParser().parse(jsonString).getAsJsonObject();
		} catch (JsonParseException e) {
			throw new RestException(BennuRestError.BAD_REQUEST);
		} catch (IllegalStateException e) {
			throw new RestException(BennuRestError.BAD_REQUEST);
		}
	}

	@Override
	public void setDelegate(Deserializer delegate) {
		this.delegate = delegate;
	}
}
