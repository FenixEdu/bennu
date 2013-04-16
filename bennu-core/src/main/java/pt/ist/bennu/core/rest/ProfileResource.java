package pt.ist.bennu.core.rest;

import java.util.Locale;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pt.ist.bennu.core.domain.exceptions.AuthorizationException;
import pt.ist.bennu.core.domain.exceptions.BennuCoreDomainException;
import pt.ist.bennu.core.domain.groups.LoggedGroup;
import pt.ist.bennu.core.i18n.I18N;
import pt.ist.bennu.core.rest.json.UserSessionViewer;
import pt.ist.bennu.core.security.Authenticate;
import pt.ist.bennu.core.security.UserSession;
import pt.ist.bennu.core.util.ConfigurationManager;

@Path("profile")
public class ProfileResource extends BennuRestResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfile() {
        return Response.ok(view(Authenticate.getUserSession(), UserSession.class, UserSessionViewer.class)).build();
    }

    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@FormParam("username") String username, @FormParam("password") String password) {
        if (!isCasEnabled()) {
            return Response.ok(view(login(username, password, false))).build();
        }
        throw AuthorizationException.authenticationFailed();
    }

    @GET
    @Path("logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout() {
        accessControl(LoggedGroup.getInstance());
        if (!isCasEnabled()) {
            Authenticate.logout(request.getSession(false));
            return Response.ok(view(null, UserSession.class, UserSessionViewer.class)).build();
        }
        throw AuthorizationException.authenticationFailed();
    }

    @POST
    @Path("locale/{tag}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeLocale(@PathParam("tag") String localeTag) {
        if (ConfigurationManager.isSupportedLanguage(localeTag)) {
            I18N.setLocale(request.getSession(true), Locale.forLanguageTag(localeTag));
            return Response.ok(view(Authenticate.getUserSession(), UserSession.class, UserSessionViewer.class)).build();
        } else {
            throw BennuCoreDomainException.resourceNotFound(localeTag);
        }
    }

    @POST
    @Path("preferred-locale/{tag}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePreferredLocale(@PathParam("tag") String localeTag) {
        accessControl(LoggedGroup.getInstance());
        if (ConfigurationManager.isSupportedLanguage(localeTag)) {
            Locale locale = Locale.forLanguageTag(localeTag);
            Authenticate.getUser().setPreferredLocale(locale);
            I18N.setLocale(request.getSession(true), locale);
            return Response.ok(view(Authenticate.getUserSession(), UserSession.class, UserSessionViewer.class)).build();
        } else {
            throw BennuCoreDomainException.resourceNotFound(localeTag);
        }
    }
}