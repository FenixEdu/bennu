package pt.ist.bennu.core.util.rest;

import pt.ist.bennu.core.rest.JerseyClient;

public class RestHost {
    private final String hostUrl;
    private final String secret;

    public RestHost(String host, String secret) {
        super();
        this.hostUrl = host;
        this.secret = secret;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public String getSecret() {
        return secret;
    }

    public JerseyClient getClient() {
        return new JerseyClient(this);
    }

}
