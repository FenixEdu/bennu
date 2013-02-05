package pt.ist.bennu.bennu.core.rest.serializer;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.bennu.core.rest.AbstractResource.CasConfigContext;
import pt.ist.bennu.bennu.core.rest.BennuRestError;
import pt.ist.bennu.bennu.core.rest.RestException;
import pt.ist.bennu.core.domain.User;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class JsonAdapter implements Serializer, Deserializer {

	private static final Logger logger = LoggerFactory.getLogger(JsonAdapter.class);
	private static final GsonBuilder builder;
	private static JsonAdapter jsonAdapter;

	static {
		builder = new GsonBuilder();
		registerTypeAdapter(User.class, new UserSerializer());
		registerTypeAdapter(CasConfigContext.class, new CasConfigContextSerializer());
	}

	
	private Gson gson;

	private JsonAdapter() {
	}
	
	private Gson getGson() {
		if (gson == null) {
			gson = builder.create();
		}
		return gson;
	}

	public static void registerTypeAdapter(Type type, Object object) {
		logger.trace("register type {} to {}", type, object);
		builder.registerTypeAdapter(type, object);
	}

	public static synchronized JsonAdapter getInstance() {
		logger.trace("get instance");
		if (jsonAdapter == null) {
			logger.trace("Create instance");
			jsonAdapter = new JsonAdapter();
		}
		return jsonAdapter;
	}

	@Override
	public final String serialize(Object object) {
		try {
			return getGson().toJson(object);
		} catch (IllegalArgumentException e) {
			throw new RestException(BennuRestError.SERIALIZER_NOT_FOUND);
		}
	}

	@Override
	public final String serialize(Object object, String collectionKey) {
		try {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add(collectionKey, getGson().toJsonTree(object));
			return jsonObject.toString();
		} catch (IllegalArgumentException e) {
			throw new RestException(BennuRestError.SERIALIZER_NOT_FOUND);
		}
	}

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
		throw new RestException(BennuRestError.DESERIALIZER_NOT_FOUND);
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

}
