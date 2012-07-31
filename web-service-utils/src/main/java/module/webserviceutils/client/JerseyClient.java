package module.webserviceutils.client;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import module.webserviceutils.domain.WSURemoteHost;

public class JerseyClient {

    private static String CONTEXT_URL = "jersey/services/";

    private String username;// =
			    // PropertiesManager.getProperty("jersey.username");
    private String password;// =
			    // PropertiesManager.getProperty("jersey.password");

    private final Client client;
    private final MultivaluedMap params;
    private String method;
    private WSURemoteHost host;

    public JerseyClient(final WSURemoteHost host) {
	client = Client.create();
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
	String url = host.getUrl();
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

    public String get() {
	checkForParameters();
	return client.resource(getUri()).queryParams(params).header("__username__", host.getUsername())
		.header("__password__", host.getPassword()).get(String.class);
    }
}
