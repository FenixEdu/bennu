package pt.ist.bennu.portal.rest;

import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import pt.ist.bennu.core.domain.Bennu;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.i18n.InternationalString;
import pt.ist.bennu.core.i18n.InternationalString.Builder;
import pt.ist.bennu.core.rest.BennuRestResource;
import pt.ist.bennu.core.rest.json.UserSessionViewer;
import pt.ist.bennu.core.security.Authenticate;
import pt.ist.bennu.core.security.UserSession;
import pt.ist.bennu.core.util.ConfigurationManager;
import pt.ist.bennu.portal.domain.HostInfo;
import pt.ist.bennu.portal.rest.json.HostMenuViewer;
import pt.ist.fenixframework.Atomic;

import com.google.gson.JsonObject;

@Path("hostmenu")
public class HostMenuResource extends BennuRestResource {

    private VirtualHost getVirtualHost(String hostname) {
        final VirtualHost virtualHost = Bennu.getInstance().getVirtualHost(hostname);
        if (virtualHost != null) {
            if (virtualHost.getInfo() == null) {
                initLocalhostInfo(virtualHost);
            }
            return virtualHost;
        } else {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
    }

    @Path("{hostname: [a-zA-Z0-9\\.]+}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String menu(@PathParam("hostname") final String hostname) {
        final JsonObject hostMenuView = new JsonObject();
        merge(hostMenuView, getBuilder().view(getVirtualHost(hostname), HostMenuViewer.class).getAsJsonObject());
        merge(hostMenuView, getBuilder().view(getCasConfigContext()).getAsJsonObject());
        merge(hostMenuView, getBuilder().view(Authenticate.getUserSession(), UserSession.class, UserSessionViewer.class)
                .getAsJsonObject());
        return toJson(hostMenuView);
    }

    private InternationalString getIS(String content) {
        Builder builder = new Builder();
        for (Locale locale : ConfigurationManager.getSupportedLocales()) {
            builder.with(locale, content);
        }
        return builder.build();
    }

    @Atomic
    private void initLocalhostInfo(VirtualHost virtualHost) {
        if (virtualHost.getInfo() == null) {
            final HostInfo hostInfo = new HostInfo(virtualHost);
            hostInfo.setApplicationCopyright(getIS("Copyright"));
            hostInfo.setApplicationTitle(getIS("Title"));
            hostInfo.setApplicationSubTitle(getIS("SubTitle"));
            hostInfo.setHtmlTitle(getIS("HTML Title"));
            hostInfo.setSupportEmailAddress("support@" + virtualHost.getHostname());
            hostInfo.setSystemEmailAddress("system@" + virtualHost.getHostname());
            hostInfo.setTheme("dot");
        }
    }
}
