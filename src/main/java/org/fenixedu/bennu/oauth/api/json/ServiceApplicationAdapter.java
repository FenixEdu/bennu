package org.fenixedu.bennu.oauth.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.domain.ServiceApplication;

import com.google.gson.JsonObject;

@DefaultJsonAdapter(ServiceApplication.class)
public class ServiceApplicationAdapter extends ExternalApplicationAdapter {

    @Override
    protected ExternalApplication create() {
        return new ServiceApplication();
    }

    @Override
    protected String getRedirectUrl(JsonObject jObj) {
        return getDefaultValue(jObj, "redirectUrl", "");
    }
}
