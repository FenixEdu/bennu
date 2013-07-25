package pt.ist.bennu.portal.rest.json;

import pt.ist.bennu.json.JsonBuilder;
import pt.ist.bennu.portal.domain.PortalConfiguration;

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
