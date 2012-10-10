package module.webserviceutils.client;

import javax.ws.rs.core.MultivaluedMap;

import module.webserviceutils.domain.ClientHost;

import org.apache.commons.lang.StringUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class JerseyClient {

    private static String CONTEXT_URL = "jersey/services/";

    private static final Client client;
    private final MultivaluedMap params;
    private String method;
    private final ClientHost host;

    static {
	client = Client.create();
    }

    public JerseyClient(final ClientHost host) {
	params = new MultivaluedMapImpl();
	this.host = host;
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
	String url = host.getServerUrl();
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

    public <T> T get(final Class<T> clazz) {
	checkForParameters();
	T t;
	try {
	    t = client.resource(getUri()).queryParams(params).header("__username__", host.getUsername())
		    .header("__password__", host.getPassword()).get(clazz);
	    return t;
	} finally {
	    params.clear();
	    method = StringUtils.EMPTY;
	}
    }

    public String get() {
	return get(String.class);
    }
}
