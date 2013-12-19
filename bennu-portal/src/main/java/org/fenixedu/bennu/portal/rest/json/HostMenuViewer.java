package org.fenixedu.bennu.portal.rest.json;
//package org.fenixedu.bennu.portal.rest.json;
//
//import org.fenixedu.bennu.core.domain.VirtualHost;
//import org.fenixedu.commons.json.JsonBuilder;
//import org.fenixedu.commons.json.JsonViewer;
//
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//
//public class HostMenuViewer implements JsonViewer<VirtualHost> {
//    @Override
//    public JsonElement view(VirtualHost obj, JsonBuilder ctx) {
//        JsonObject json = (JsonObject) ctx.view(obj);
//        if (obj.getMenu() != null) {
//            json.add("menu", ctx.view(obj.getMenu().getUserMenu()));
//        }
//        return json;
//    }
//}
