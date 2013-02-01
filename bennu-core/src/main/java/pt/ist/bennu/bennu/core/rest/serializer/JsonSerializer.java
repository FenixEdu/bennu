package pt.ist.bennu.bennu.core.rest.serializer;

import pt.ist.bennu.bennu.core.rest.BennuRestError;
import pt.ist.bennu.bennu.core.rest.RestException;
import pt.ist.bennu.core.domain.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class JsonSerializer implements Serializer {

	private final Gson gson;
	private Serializer delegate;

	public JsonSerializer() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(User.class, new UserSerializer());
		builder.registerTypeAdapter(CasConfigContextSerializer.class, new CasConfigContextSerializer());
		this.gson = builder.create();
		this.delegate = null;
	}

	@Override
	public final String serialize(Object object) {
		try {
			gson.getAdapter(object.getClass());
			return gson.toJson(object);
		} catch (IllegalArgumentException e) {
			if (delegate != null) {
				return delegate.serialize(object);
			} else {
				throw new RestException(BennuRestError.SERIALIZER_NOT_FOUND);
			}
		}
	}

	@Override
	public final String serialize(Object object, String collectionKey) {
		try {
			gson.getAdapter(object.getClass());
			JsonObject jsonObject = new JsonObject();
			jsonObject.add(collectionKey, gson.toJsonTree(object));
			return jsonObject.toString();
		} catch (IllegalArgumentException e) {
			if (delegate != null) {
				return delegate.serialize(object, collectionKey);
			} else {
				throw new RestException(BennuRestError.SERIALIZER_NOT_FOUND);
			}
		}
	}

	@Override
	public void setDelegate(Serializer delegate) {
		this.delegate = delegate;
	}
}
