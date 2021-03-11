package org.fenixedu.bennu.spring;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.json.JsonUtils;
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
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
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

    /**
     * Responds the pagination results accordingly with the default page mapper.
     *
     * <p>Check {@link BaseController#respondPagination(Long, Long, List, Function, BiFunction)}
     * for the proper method documentation.</p>
     *
     * <p>Also check {@link BaseController#toPaginationPage(int, Stream)} for the default
     * page mapper.</p>
     *
     * @see BaseController#respondPagination(Long, Long, List, Function, BiFunction).
     * @param skip {@link Long} value to use as stream skip.
     * @param limit {@link Long} value to use as stream limit.
     * @param results {@link List} containing all results to be used, this value cannot be {@code null}.
     * @param resultMapper {@link Function} to be used as the result mapper, this value cannot be {@code null}.
     * @return {@link ResponseEntity} with an {@link HttpStatus} of 200 (ok) and the correspondent body.
     */
    public static <T> ResponseEntity<?> respondPagination(final Long skip, final Long limit, final List<T> results,
                                                          final Function<T, JsonObject> resultMapper) {
        return respondPagination(skip, limit, results, resultMapper, BaseController::toPaginationPage);
    }

    /**
     * Responds a {@link ResponseEntity} with an {@link HttpStatus} of 200 (ok).
     *
     * <p>If either skip or limit are {@code null}, the response body will contain a
     * {@link JsonArray} with the mapped results.</p>
     *
     * <p>If both skip and limit values are passed, the response body will contain a
     * {@link JsonObject} with a {@link Integer} value of total items {@code "totalItems"}
     * and a {@link JsonArray} containing all the mapped results with the
     * skip and limit values applied {@code "items"}.</p>
     *
     * @param skip {@link Long} value to use as stream skip.
     * @param limit {@link Long} value to use as stream limit.
     * @param results {@link List} containing all results to be used, this value cannot be {@code null}.
     * @param resultMapper {@link Function} to be used as the result mapper, this value cannot be {@code null}.
     * @param pageMapper {@link BiFunction} to be used as the page mapper, this value cannot be {@code null}.
     * @return {@link ResponseEntity} with an {@link HttpStatus} of 200 (ok) and the correspondent body.
     */
    public static <T> ResponseEntity<?> respondPagination(final Long skip, final Long limit, final List<T> results,
                                                          final Function<T, JsonObject> resultMapper,
                                                          final BiFunction<Integer, Stream<JsonObject>, JsonObject> pageMapper) {
        final int totalItems = results.size();
        final Stream<JsonObject> result = results.stream()
                .skip(skip == null ? 0L : skip)
                .limit(limit == null ? Long.MAX_VALUE : limit)
                .map(resultMapper);

        if (skip != null && limit != null) {
            return ok(pageMapper.apply(totalItems, result));
        }
        return respond(result);
    }

    /**
     * This method is the default pagination page mapper.
     *
     * <p>Returns a {@link JsonObject} containing the {@code "totalItems"} param value
     * and a {@link JsonArray} {@code "items"} value of multiple {@link JsonObject}
     * containing the collected result.</p>
     *
     * <p>It is used in the {@link BaseController#respondPagination(Long, Long, List, Function)}
     * method as default page mapper.</p>
     *
     * @param totalItems int value of total items.
     * @param result {@link Stream} of {@link JsonObject} containing the result to be collected.
     * @return {@link JsonObject} with the corresponding properties.
     */
    public static JsonObject toPaginationPage(final int totalItems, final Stream<JsonObject> result) {
        return JsonUtils.toJson(data -> {
            data.addProperty("totalItems", totalItems);
            data.add("items", result.collect(StreamUtils.toJsonArray()));
        });
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
        return respondConflict(null);
    }

    public static ResponseEntity<?> respondConflict(final String message) {
        final ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.CONFLICT);
        return Strings.isNullOrEmpty(message) ? builder.build() : builder.body(message);
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
