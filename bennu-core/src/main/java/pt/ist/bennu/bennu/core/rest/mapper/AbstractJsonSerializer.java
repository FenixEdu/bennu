package pt.ist.bennu.bennu.core.rest.mapper;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class AbstractJsonSerializer implements Serializer {

	private Gson gson;
	private Serializer delegate;

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

	public void setGson(Gson gson) {
		this.gson = gson;
	}

	@Override
	public void setDelegate(Serializer delegate) {
		this.delegate = delegate;
	}
}
