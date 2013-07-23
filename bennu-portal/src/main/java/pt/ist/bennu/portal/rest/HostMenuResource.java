package pt.ist.bennu.portal.rest;

import javax.ws.rs.Path;

import pt.ist.bennu.core.rest.BennuRestResource;

@Path("hostmenu")
public class HostMenuResource extends BennuRestResource {

//    @Path("{hostname: [a-zA-Z0-9\\.]+}")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public String menu(@PathParam("hostname") final String hostname) {
//        final JsonObject hostMenuView = new JsonObject();
//        merge(hostMenuView, getBuilder().view(BennuConfiguration getVirtualHost(hostname), HostMenuViewer.class).getAsJsonObject());
//        merge(hostMenuView, getBuilder().view(getCasConfigContext()).getAsJsonObject());
//        merge(hostMenuView, getBuilder().view(Authenticate.getUserSession(), UserSession.class, UserSessionViewer.class)
//                .getAsJsonObject());
//        return toJson(hostMenuView);
//    }
//
//    private LocalizedString getIS(String content) {
//        Builder builder = new Builder();
//        for (Locale locale : ConfigurationManager.getSupportedLocales()) {
//            builder.with(locale, content);
//        }
//        return builder.build();
//    }
//
//    @Atomic
//    private void initLocalhostInfo(VirtualHost virtualHost) {
//        if (virtualHost.getInfo() == null) {
//            final HostInfo hostInfo = new HostInfo(virtualHost);
//            hostInfo.setApplicationCopyright(getIS("Copyright"));
//            hostInfo.setApplicationTitle(getIS("Title"));
//            hostInfo.setApplicationSubTitle(getIS("SubTitle"));
//            hostInfo.setHtmlTitle(getIS("HTML Title"));
//            hostInfo.setSupportEmailAddress("support@" + virtualHost.getHostname());
//            hostInfo.setSystemEmailAddress("system@" + virtualHost.getHostname());
//            hostInfo.setTheme("dot");
//        }
//    }
}
