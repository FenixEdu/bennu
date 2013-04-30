package pt.ist.bennu.portal.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.i18n.I18N;
import pt.ist.bennu.core.i18n.InternationalString;
import pt.ist.bennu.core.rest.BennuRestResource;
import pt.ist.bennu.portal.domain.HostInfo;
import pt.ist.bennu.portal.domain.MenuItem;
import pt.ist.bennu.service.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.core.util.Base64;

@Path("init")
public class InitResource extends BennuRestResource {

    Logger LOGGER = LoggerFactory.getLogger(InitResource.class);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response loadModel() throws IOException {
        initLocalhostInfo();
        InputStream stream = InitResource.class.getResourceAsStream("/model.json");
        final JsonObject jsonObject = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
        parseHosts(jsonObject.get("hosts").getAsJsonArray());
        return Response.ok("Init ok.").build();
    }

    @Path("basic")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String loadBasic() {
        initLocalhostInfo();
        return view(Bennu.getInstance().getVirtualHosts(), "hosts");
    }

    @Path("drop")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response dropModel() throws IOException {
        initDropModel();
        return Response.ok("Drop ok.").build();
    }

    @Service
    private void initDropModel() {
        final VirtualHost localhost = Bennu.getInstance().getVirtualHost("localhost");
        for (VirtualHost host : Bennu.getInstance().getVirtualHosts()) {
            if (!host.equals(localhost)) {
                if (host.hasInfo()) {
                    host.getInfo().delete();
                }
            }
            if (host.hasMenu()) {
                host.getMenu().delete();
            }
        }
        if (localhost != null) {
            if (localhost.hasMenu()) {
                localhost.getMenu().delete();
            }
            if (localhost.hasInfo()) {
                localhost.getInfo().delete();
            }
        }
    }

    @Service
    private void initLocalhostInfo() {
        final VirtualHost localhost = Bennu.getInstance().getVirtualHost("localhost");
        if (!localhost.hasInfo()) {
            final HostInfo hostInfo = new HostInfo(localhost);
            hostInfo.setApplicationCopyright(new InternationalString(I18N.getLocale(), "copyright"));
            hostInfo.setApplicationTitle(new InternationalString(I18N.getLocale(), "localhost"));
            hostInfo.setApplicationSubTitle(new InternationalString(I18N.getLocale(), "localhost"));
            hostInfo.setHtmlTitle(new InternationalString(I18N.getLocale(), "htmlTitle"));
            hostInfo.setSupportEmailAddress("support@localhost");
            hostInfo.setSystemEmailAddress("system@localhost");
            hostInfo.setTheme("dot");
        }
    }

    @Service
    private void parseHosts(JsonArray hostsArray) {
        for (JsonElement host : hostsArray) {
            parseHost(host.getAsJsonObject());
        }
    }

    private VirtualHost getOrCreate(String hostname) {
        VirtualHost virtualHost = Bennu.getInstance().getVirtualHost(hostname);
        if (virtualHost == null) {
            virtualHost = new VirtualHost(hostname);
        }
        return virtualHost;
    }

    private byte[] dec64(JsonElement base64JsonElement) {
        return Base64.decode(base64JsonElement.getAsString());
    }

    private InternationalString mls(JsonObject json, String attr) {
        return InternationalString.fromJson(json.get(attr).getAsJsonObject());
    }

    private String get(JsonObject obj, String attr) {
        return get(obj, attr, StringUtils.EMPTY);
    }

    private String get(JsonObject obj, String attr, String defValue) {
        if (obj.has(attr)) {
            return obj.get(attr).getAsString();
        }
        return defValue;
    }

    private void parseHost(JsonObject host) {
        final String hostname = host.get("hostname").getAsString();
        final VirtualHost virtualHost = getOrCreate(hostname);
        LOGGER.trace("parse Host {}", hostname);
        if (!virtualHost.hasInfo()) {
            HostInfo hostInfo = new HostInfo(virtualHost);
            final String supportEmailAddress = host.get("supportEmailAddress").getAsString();
            final String systemEmailAddress = host.get("systemEmailAddress").getAsString();
            hostInfo.setApplicationCopyright(mls(host, "copyright"));
            hostInfo.setApplicationSubTitle(mls(host, "subtitle"));
            hostInfo.setApplicationTitle(mls(host, "title"));
            hostInfo.setSupportEmailAddress(supportEmailAddress);
            hostInfo.setSystemEmailAddress(systemEmailAddress);
            hostInfo.setHtmlTitle(mls(host, "htmlTitle"));
            hostInfo.setTheme("dot");
            createMenu(virtualHost, host);
        }
    }

    private void createMenu(VirtualHost virtualHost, JsonObject host) {
        MenuItem hostMenu = new MenuItem();
        virtualHost.setMenu(hostMenu);
        for (JsonElement appEl : host.get("apps").getAsJsonArray()) {
            JsonObject appJson = (JsonObject) appEl;
            parseApp(hostMenu, appJson);
        }
    }

    private void parseApp(MenuItem hostMenu, JsonObject appJson) {
        MenuItem appMenu = new MenuItem();
        fillMenu(appMenu, appJson);
        LOGGER.trace("\tparse App : host {} app {}", hostMenu.getHost().getHostname(), hostMenu.getTitle());
        final JsonArray functionalities = appJson.get("functionalities").getAsJsonArray();
        for (JsonElement functionalityEl : functionalities) {
            parseFunctionality(appMenu, (JsonObject) functionalityEl);
        }
        hostMenu.addChild(appMenu);
    }

    public void fillMenu(MenuItem menu, JsonObject json) {
        menu.setTitle(mls(json, "title"));
        menu.setDescription(mls(json, "description"));
        menu.setPath(json.get("path").getAsString());
        menu.setAccessExpression(json.get("accessExpression").getAsString());
    }

    private void parseFunctionality(MenuItem hostMenu, JsonObject funcJson) {
        final MenuItem functionalityMenu = new MenuItem();
        fillMenu(functionalityMenu, funcJson);
        hostMenu.addChild(functionalityMenu);
    }
}
