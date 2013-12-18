package pt.ist.bennu.core.rest.json;

import org.fenixedu.commons.json.JsonBuilder;
import org.fenixedu.commons.json.JsonViewer;

import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
import pt.ist.bennu.core.rest.BennuRestResource.CasConfigContext;
import pt.ist.bennu.core.util.CoreConfiguration.CasConfig;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(CasConfigContext.class)
public class CasConfigContextSerializer implements JsonViewer<CasConfigContext> {

    @Override
    public JsonElement view(CasConfigContext casConfigContext, JsonBuilder context) {
        JsonObject jsonObject = new JsonObject();
        CasConfig casConfig = casConfigContext.getCasConfig();
        if (casConfig != null && casConfig.isCasEnabled()) {
            jsonObject.addProperty("casEnabled", true);
            jsonObject.addProperty("loginUrl", casConfig.getCasLoginUrl(casConfigContext.getRequest()));
            jsonObject.addProperty("logoutUrl", casConfig.getCasLogoutUrl());
        } else {
            jsonObject.addProperty("casEnabled", false);
        }
        return jsonObject;
    }

}
