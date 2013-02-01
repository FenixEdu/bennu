package pt.ist.bennu.bennu.core.rest.mapper;

import pt.ist.bennu.bennu.core.rest.AbstractResource.CasConfigContext;
import pt.ist.bennu.core.domain.User;

import com.google.gson.GsonBuilder;

public final class BennuJsonSerializer extends AbstractJsonSerializer {

	public BennuJsonSerializer() {
		GsonBuilder builder = new GsonBuilder();
		builder.serializeNulls();
		builder.setPrettyPrinting();
		builder.registerTypeAdapter(User.class, new UserSerializer());
		builder.registerTypeAdapter(CasConfigContext.class, new CasConfigContextSerializer());
		setGson(builder.create());
	}
}
