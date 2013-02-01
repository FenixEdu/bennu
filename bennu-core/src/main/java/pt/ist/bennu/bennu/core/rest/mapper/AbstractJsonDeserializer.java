package pt.ist.bennu.bennu.core.rest.mapper;

import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public abstract class AbstractJsonDeserializer implements Deserializer {

	private Deserializer delegate;

	@Override
	public final <T> T deserialize(String jsonString, Class<T> type, String externalId) {
		T modifiedObject = AbstractDomainObject.fromExternalId(externalId);
		if (modifiedObject.getClass().equals(type)) {
			updateObject(type.cast(modifiedObject), jsonString, externalId);
			return modifiedObject;
		} else {
			throw new RestException(BennuRestError.RESOURSE_TYPE_MISMATCH);
		}
	}

	@Override
	public final void updateObject(Object object, String jsonString, String externalId) {
		if (delegate != null) {
			delegate.updateObject(object, jsonString, externalId);
		} else {
			throw new RestException(BennuRestError.DESERIALIZER_NOT_FOUND);
		}
	}

	protected final JsonObject parseJsonObject(String jsonString) {
		try {
			return new JsonParser().parse(jsonString).getAsJsonObject();
		} catch (JsonParseException e) {
			throw new RestException(BennuRestError.BAD_REQUEST);
		} catch (IllegalStateException e) {
			throw new RestException(BennuRestError.BAD_REQUEST);
		}
	}

	@Override
	public final void setDelegate(Deserializer delegate) {
		this.delegate = delegate;
	}
}
