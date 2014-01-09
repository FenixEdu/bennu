package org.fenixedu.bennu.portal.rest.json;

import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PortalMenuViewer extends PortalConfigurationAdapter {
    @Override
    public JsonElement view(PortalConfiguration configuration, JsonBuilder ctx) {
        final JsonObject view = super.view(configuration, ctx).getAsJsonObject();
        if (configuration.getMenu() != null) {
            view.add("menu", ctx.view(configuration.getMenu().getUserMenu()));
        }
        return view;
    }
}
