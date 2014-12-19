package org.fenixedu.bennu.oauth.jaxrs;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.fenixedu.bennu.oauth.annotation.OAuthEndpoint;

@Provider
public class BennuOAuthFeature implements DynamicFeature {

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        OAuthEndpoint endpoint = resourceInfo.getResourceMethod().getAnnotation(OAuthEndpoint.class);
        if (endpoint != null) {
//            BennuOAuthAuthorizationFilter filter = new BennuOAuthAuthorizationFilter();
//            filter.setEndpoint(endpoint);
            context.register(BennuOAuthAuthorizationFilter.class);
        }
    }

}