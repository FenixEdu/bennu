//package pt.ist.bennu.portal.rest.json;
//
//import org.apache.commons.codec.binary.Base64;
//
//import pt.ist.bennu.core.annotation.DefaultJsonAdapter;
//import pt.ist.bennu.core.domain.Bennu;
//import pt.ist.bennu.core.domain.VirtualHost;
//import pt.ist.bennu.core.rest.json.DomainObjectViewer;
//import org.fenixedu.commons.json.JsonAdapter;
//import org.fenixedu.commons.json.JsonBuilder;
//import pt.ist.bennu.portal.domain.HostInfo;
//import pt.ist.bennu.portal.domain.MenuItem;
//import org.fenixedu.commons.i18n.LocalizedString;
//import pt.ist.fenixframework.FenixFramework;
//
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//
//@DefaultJsonAdapter(VirtualHost.class)
//public class VirtualHostAdapter implements JsonAdapter<VirtualHost> {
//
//    @Override
//    public VirtualHost create(JsonElement json, JsonBuilder ctx) {
//        JsonObject jsonObj = json.getAsJsonObject();
//        final String hostname = jsonObj.get("hostname").getAsString();
//        VirtualHost virtualHost = Bennu.getInstance().getVirtualHost(hostname);
//        if (virtualHost == null) {
//            virtualHost = new VirtualHost(hostname);
//            HostInfo info = new HostInfo(virtualHost);
//            setHostInfo(virtualHost, jsonObj, info, ctx);
//        }
//        return virtualHost;
//    }
//
//    @Override
//    public VirtualHost update(JsonElement json, VirtualHost obj, JsonBuilder ctx) {
//        JsonObject jsonObj = json.getAsJsonObject();
//        HostInfo info = obj.getInfo();
//        obj.setHostname(jsonObj.get("hostname").getAsString());
//        setHostInfo(obj, jsonObj, info, ctx);
//        return obj;
//    }
//
//    private void setHostInfo(VirtualHost obj, JsonObject jsonObj, HostInfo info, JsonBuilder ctx) {
//        if (jsonObj.has("applicationCopyright")) {
//            info.setApplicationCopyright(LocalizedString.fromJson(jsonObj.get("applicationCopyright").getAsJsonObject()));
//        }
//        if (jsonObj.has("applicationTitle")) {
//            info.setApplicationTitle(LocalizedString.fromJson(jsonObj.get("applicationTitle").getAsJsonObject()));
//        }
//        if (jsonObj.has("htmlTitle")) {
//            info.setHtmlTitle(LocalizedString.fromJson(jsonObj.get("htmlTitle").getAsJsonObject()));
//        }
//        if (jsonObj.has("applicationSubTitle")) {
//            info.setApplicationSubTitle(LocalizedString.fromJson(jsonObj.get("applicationSubTitle").getAsJsonObject()));
//        }
//        if (jsonObj.has("supportEmailAddress")) {
//            info.setSupportEmailAddress(jsonObj.get("supportEmailAddress").getAsString());
//        }
//        if (jsonObj.has("systemEmailAddress")) {
//            info.setSystemEmailAddress(jsonObj.get("systemEmailAddress").getAsString());
//        }
//        if (jsonObj.has("theme")) {
//            info.setTheme(jsonObj.get("theme").getAsString());
//        }
//        if (jsonObj.has("menu")) {
//            final JsonObject menuObj = jsonObj.get("menu").getAsJsonObject();
//            if (menuObj.has("id")) {
//                final String menuExternalId = menuObj.get("id").getAsString();
//                MenuItem menu = FenixFramework.getDomainObject(menuExternalId);
//                if (!menu.equals(obj.getMenu())) {
//                    obj.setMenu(menu);
//                }
//            }
//        }
//        if (jsonObj.has("logo")) {
//            info.setLogo(Base64.decodeBase64(jsonObj.get("logo").getAsString()));
//        }
//        if (jsonObj.has("logoType")) {
//            info.setLogoType(jsonObj.get("logoType").getAsString());
//        }
//    }
//
//    @Override
//    public JsonElement view(VirtualHost obj, JsonBuilder ctx) {
//        JsonObject json = new JsonObject();
//        json.addProperty("id", obj.getExternalId());
//        json.addProperty("hostname", obj.getHostname());
//        json.add("applicationTitle", obj.getInfo().getApplicationTitle().json());
//        json.add("htmlTitle", obj.getInfo().getHtmlTitle().json());
//        json.add("applicationSubTitle", obj.getInfo().getApplicationSubTitle().json());
//        json.add("applicationCopyright", obj.getInfo().getApplicationCopyright().json());
//        json.addProperty("supportEmailAddress", obj.getInfo().getSupportEmailAddress());
//        json.addProperty("systemEmailAddress", obj.getInfo().getSystemEmailAddress());
//        json.addProperty("theme", obj.getInfo().getTheme());
//        json.add("menu", ctx.view(obj.getMenu(), DomainObjectViewer.class));
//        if (obj.getInfo().hasLogo()) {
//            json.addProperty("logo", Base64.encodeBase64String(obj.getInfo().getLogo()));
//            json.addProperty("logoType", new String(obj.getInfo().getLogoType()));
//        }
//        return json;
//    }
//
//}
