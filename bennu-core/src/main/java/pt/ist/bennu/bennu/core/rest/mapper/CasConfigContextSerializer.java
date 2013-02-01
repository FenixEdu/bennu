package pt.ist.bennu.bennu.core.rest.mapper;

import java.lang.reflect.Type;

import pt.ist.bennu.bennu.core.rest.AbstractResource.CasConfigContext;
import pt.ist.bennu.core.util.ConfigurationManager.CasConfig;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CasConfigContextSerializer implements JsonSerializer<CasConfigContext> {

	@Override
	public JsonElement serialize(CasConfigContext casConfigContext, Type type, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		CasConfig casConfig = casConfigContext.getCasConfig();
		if (casConfig.isCasEnabled()) {
			jsonObject.addProperty("casEnabled", true);
			jsonObject.addProperty("loginUrl", casConfig.getCasLoginUrl(casConfigContext.getRequest()));
			jsonObject.addProperty("logoutUrl", casConfig.getCasLogoutUrl());
		} else {
			jsonObject.addProperty("casEnabled", false);
		}
		return jsonObject;
	}

}
