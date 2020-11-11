package org.fenixedu.bennu.spring;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.stream.StreamUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class BaseController {

    public static ResponseEntity<?> respond(final HttpStatus status, final JsonElement body) {
        final ResponseEntity.BodyBuilder builder = ResponseEntity.status(status);
        builder.contentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return builder.body(body.toString());
    }

    public static ResponseEntity<?> ok(final JsonElement body) {
        return respond(HttpStatus.OK, body);
    }

    public static ResponseEntity<?> respond(final Stream<JsonObject> stream) {
        return ok(stream.collect(StreamUtils.toJsonArray()));
    }

    public static ResponseEntity<?> respond(final HttpStatus status, final Consumer<JsonObject> consumer,
                                            final String bundle, final String messageKey, final String... args) {
        final JsonObject response = new JsonObject();
        response.addProperty("message", BundleUtil.getString(bundle, I18N.getLocale(), messageKey, args));
        final JsonObject data = new JsonObject();
        if (consumer != null) {
            consumer.accept(data);
        }
        response.add("data", data);
        return respond(status, response);
    }

    public static ResponseEntity<?> redirect(final String relativePath) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Location", CoreConfiguration.getConfiguration().applicationUrl().concat(relativePath));
        return new ResponseEntity<byte[]>(null, headers, HttpStatus.TEMPORARY_REDIRECT);
    }

    public static ResponseEntity<?> respond(final Consumer<JsonObject> consumer, final String bundle,
                                            final String messageKey, final String... args) {
        return respond(HttpStatus.OK, consumer, bundle, messageKey);
    }

    public static ResponseEntity<?> unauthorized() {
        final User user = Authenticate.getUser();
        final HttpStatus status = user == null ? HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).build();
    }

    public static ResponseEntity<?> respondNotFound() {
        return ResponseEntity.notFound().build();
    }

    public static ResponseEntity<?> respondNotFound(final String bundle, final String messageKey, final String... args) {
        final String message = BundleUtil.getString(bundle, I18N.getLocale(), messageKey, args);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    public static ResponseEntity<?> respondPreconditionFailed(final String errorMessage) {
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(errorMessage);
    }

    public static ResponseEntity<?> respondLiteralBadRequest(final String message) {
        return Strings.isNullOrEmpty(message) ? ResponseEntity.badRequest().build()
                : ResponseEntity.badRequest().body(message);
    }

    public static ResponseEntity<?> respondBadRequest(final String bundle, final String messageKey) {
        if (Strings.isNullOrEmpty(messageKey)) {
            return respondLiteralBadRequest(null);
        }
        final String message = BundleUtil.getString(bundle, I18N.getLocale(), messageKey);
        return respondLiteralBadRequest(message);
    }

    public static ResponseEntity<?> respondBadRequest(final String bundle, final String messageKey, final String... args) {
        if (Strings.isNullOrEmpty(messageKey)) {
            return respondLiteralBadRequest(null);
        }
        final String message = BundleUtil.getString(bundle, I18N.getLocale(), messageKey, args);
        return respondLiteralBadRequest(message);
    }

    public static ResponseEntity<?> respondBadRequest() {
        return respondBadRequest(null, null);
    }

    public static ResponseEntity<?> respondConflict() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    public static void setCookie(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse,
                          final String name, final String value, final int age, final String path, final boolean httpOnly) {
        final Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        if (CoreConfiguration.getConfiguration().applicationUrl().startsWith("https://")) {
            cookie.setSecure(true);
        }
        cookie.setMaxAge(age);
        cookie.setPath(httpRequest.getContextPath() + path);
        httpResponse.addCookie(cookie);
    }

}
