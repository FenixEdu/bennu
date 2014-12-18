package org.fenixedu.bennu.oauth.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ServiceApplication;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(ServiceApplication.class)
public class ServiceApplicationAdapter extends ExternalApplicationAdapter {

    @Override
    protected ExternalApplication create(JsonElement json) {
        ServiceApplication serviceApplication = new ServiceApplication();
        serviceApplication.setAuthorName(Authenticate.getUser().getProfile().getDisplayName());
        JsonElement ipAddresses = json.getAsJsonObject().get("ipAddresses");
        if (ipAddresses != null && ipAddresses.isJsonArray()) {
            serviceApplication.setIpAddresses(ipAddresses);
        }
        return serviceApplication;
    }

    @Override
    protected String getRedirectUrl(JsonObject jObj) {
        return getDefaultValue(jObj, "redirectUrl", "");
    }

    @Override
    public JsonElement view(ExternalApplication obj, JsonBuilder ctx) {
        JsonElement view = super.view(obj, ctx);
        JsonObject jsonObject = view.getAsJsonObject();
        jsonObject.add("ipAddresses", ((ServiceApplication) obj).getIpAddresses());
        return jsonObject;
    }

    @Override
    public ExternalApplication update(JsonElement json, ExternalApplication app, JsonBuilder ctx) {
        ServiceApplication serviceApplication = (ServiceApplication) super.update(json, app, ctx);
        serviceApplication.setIpAddresses(json.getAsJsonObject().get("ipAddresses"));
        return serviceApplication;
    }

}