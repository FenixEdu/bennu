package pt.ist.bennu.core.rest;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;

import pt.ist.bennu.core.util.rest.RestHost;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class JerseyClient {
    private static final String CONTEXT_URL = "api/";
    private final Client client;
    private final MultivaluedMap<String, String> params;
    private String method;
    private final RestHost host;

    private String getHeader(String userToLogin) {
        try {
            return "Basic " + new String(Base64.encode(userToLogin + ":" + host.getSecret()), "ASCII");
        } catch (UnsupportedEncodingException ex) {
            // This should never occur
            throw new RuntimeException(ex);
        }
    }

    public JerseyClient(final RestHost host) {
        this.host = host;
        params = new MultivaluedMapImpl();
        client = Client.create();
    }

    public JerseyClient method(final String method) {
        this.method = method;
        return this;
    }

    public JerseyClient arg(final String key, final String value) {
        params.add(key, value);
        return this;
    }

    private String getUri() {
        String url = host.getHostUrl();
        if (!url.endsWith("/")) {
            url += "/";
        }
        return url + CONTEXT_URL + method;
    }

    private void checkForParameters() {
        if (host == null || StringUtils.isEmpty(method)) {
            final ClientResponse response = new ClientResponse(Status.BAD_REQUEST.getStatusCode(), null, null, null);
            throw new UniformInterfaceException("Host or method are null", response);
        }
    }

    public <T> T get(final Class<T> clazz, final String username) {
        checkForParameters();
        T t;
        try {
            t = client.resource(getUri()).queryParams(params).header(HttpHeaders.AUTHORIZATION, getHeader(username)).get(clazz);
            return t;
        } finally {
            params.clear();
            method = StringUtils.EMPTY;
        }
    }

    public String get() {
        return get(StringUtils.EMPTY);
    }

    public String get(String username) {
        return get(String.class, username);
    }
}
