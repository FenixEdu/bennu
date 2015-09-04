package org.fenixedu.bennu.core.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.IllformedLocaleException;
import java.util.Locale;
import java.util.Locale.Builder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.api.json.AuthenticatedUserViewer;
import org.fenixedu.bennu.core.domain.Avatar;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.i18n.I18NFilter;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.gson.JsonElement;

@Path("/bennu-core/profile")
public class ProfileResource extends BennuRestResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement getProfile() {
        return view(null, Void.class, AuthenticatedUserViewer.class);
    }

    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement login(@Context HttpServletRequest request, @Context HttpServletResponse response,
            @FormParam("username") String username, @FormParam("password") String password) {
        if (CoreConfiguration.getConfiguration().localLoginEnabled()) {
            User user = User.findByUsername(username);
            if (user != null && (CoreConfiguration.getConfiguration().developmentMode() || user.matchesPassword(password))) {
                Authenticate.login(request, response, user);
            } else {
                throw AuthorizationException.authenticationFailed();
            }
            return view(null, Void.class, AuthenticatedUserViewer.class);
        }
        throw AuthorizationException.authenticationFailed();
    }

    @GET
    @Path("logout")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement logout(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        accessControl(Group.logged());
        Authenticate.logout(request, response);
        return view(null, Void.class, AuthenticatedUserViewer.class);
    }

    @POST
    @Path("locale/{tag}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonElement changeLocale(@Context HttpServletRequest request, @Context HttpServletResponse response,
            @PathParam("tag") String localeTag) {
        try {
            Locale locale = new Builder().setLanguageTag(localeTag).build();
            if (CoreConfiguration.supportedLocales().contains(locale)) {
                I18NFilter.updateLocale(locale, request, response);
                if (Authenticate.isLogged()) {
                    setPreferredLocale(locale);
                }
                return view(null, Void.class, AuthenticatedUserViewer.class);
            }
        } catch (IllformedLocaleException e) {
        }
        throw BennuCoreDomainException.resourceNotFound(localeTag);
    }

    @Atomic(mode = TxMode.WRITE)
    private void setPreferredLocale(Locale locale) {
        Authenticate.getUser().getProfile().setPreferredLocale(locale);
    }

    @GET
    @Path("localavatar/{username}")
    public Response localAvatar(@PathParam("username") String username, @QueryParam("s") @DefaultValue("100") Integer size,
            @HeaderParam("If-None-Match") String ifNoneMatch) {
        User user = User.findByUsername(username);
        if (user != null) {
            Avatar avatar = Avatar.getForUser(user);
            EntityTag etag = getEtag(avatar, size);
            if (etag.toString().equals(ifNoneMatch)) {
                return Response.notModified(etag).build();
            }
            if (avatar != null) {
                return Response.ok(avatar.getData(size), avatar.getMimeType())
                        .header(HttpHeaders.CACHE_CONTROL, CoreConfiguration.getConfiguration().staticCacheControl()).tag(etag)
                        .build();
            } else {
                try (InputStream mm =
                        ProfileResource.class.getClassLoader().getResourceAsStream("META-INF/resources/img/mysteryman.png")) {
                    return Response.ok(Avatar.process(mm, "image/png", size), "image/png")
                            .header(HttpHeaders.CACHE_CONTROL, CoreConfiguration.getConfiguration().staticCacheControl())
                            .tag(etag).build();
                } catch (IOException e) {
                    throw BennuCoreDomainException.resourceNotFound(username);
                }
            }
        }
        throw BennuCoreDomainException.resourceNotFound(username);
    }

    private EntityTag getEtag(Avatar avatar, int size) {
        return EntityTag.valueOf("W/\"" + (avatar == null ? "mm-av" : avatar.getExternalId()) + "-" + size + "\"");
    }
}