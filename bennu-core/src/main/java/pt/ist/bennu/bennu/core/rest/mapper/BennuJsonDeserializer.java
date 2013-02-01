package pt.ist.bennu.bennu.core.rest.mapper;

import pt.ist.bennu.core.domain.User;

import com.google.gson.JsonObject;

public class BennuJsonDeserializer extends AbstractJsonDeserializer {

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

	public void updateObject(User user, String jsonString, String externalId) {
		JsonObject jsonObject = parseJsonObject(jsonString);
		if (jsonObject.has("email")) {
			user.setEmail(jsonObject.get("email").getAsString());
		}
	}

}
