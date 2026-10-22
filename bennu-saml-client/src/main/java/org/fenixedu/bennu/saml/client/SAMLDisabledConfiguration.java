package org.fenixedu.bennu.saml.client;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration used when SAML is disabled ({@code saml.enabled = false}).
 *
 * The web-fragment of this module statically registers a {@code DelegatingFilterProxy} for the SAML
 * endpoints, which requires a {@code springSecurityFilterChain} bean to exist. When SAML is
 * disabled, this configuration provides a pass-through filter so that requests reaching the SAML
 * endpoints continue down the filter chain and land on the disabled-404 guard in
 * {@link org.fenixedu.bennu.saml.client.api.SAMLResource}, exactly as in previous releases.
 */
@Configuration
public class SAMLDisabledConfiguration {

    @Bean(name = "springSecurityFilterChain")
    public Filter samlDisabledFilter() {
        // javax.servlet-api 3.0.1 Filter has no default methods, so a lambda is not possible here
        return new Filter() {

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                chain.doFilter(request, response);
            }

            @Override
            public void init(FilterConfig filterConfig) {
            }

            @Override
            public void destroy() {
            }
        };
    }
}
