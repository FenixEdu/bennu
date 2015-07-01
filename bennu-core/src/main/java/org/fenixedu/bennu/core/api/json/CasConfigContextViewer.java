package org.fenixedu.bennu.core.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.fenixedu.bennu.core.rest.BennuRestResource.CasConfigContext;
import org.fenixedu.bennu.core.util.CoreConfiguration.CasConfig;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(CasConfigContext.class)
public class CasConfigContextViewer implements JsonViewer<CasConfigContext> {

    @Override
    public JsonElement view(CasConfigContext casConfigContext, JsonBuilder context) {
        JsonObject jsonObject = new JsonObject();
        CasConfig casConfig = casConfigContext.getCasConfig();
        if (casConfig != null && casConfig.isCasEnabled()) {
            jsonObject.addProperty("casEnabled", true);
            jsonObject.addProperty("loginUrl", casConfig.getCasLoginUrl());
            jsonObject.addProperty("logoutUrl", casConfig.getCasLogoutUrl());
        } else {
            jsonObject.addProperty("casEnabled", false);
        }
        return jsonObject;
    }

}
