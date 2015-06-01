package org.fenixedu.bennu.spring.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Utility class to provide easy access to {@link CSRFToken} in JSP contexts.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
@Component("csrf")
public class CSRFTokenBean {

    private final CSRFTokenRepository tokenRepository;

    @Autowired
    public CSRFTokenBean(CSRFTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * Returns the value of the token associated with the current request.
     * 
     * @return
     *         The token associated with this request
     * @throws IllegalStateException
     *             If this method is invoked outside a Spring MVC context
     */
    public String getToken() {
        return tokenRepository.getToken(getCurrentRequest()).getToken();
    }

    /**
     * Prints a HTML input field with the CSRF Token associated with the current request.
     * 
     * This allows simply adding
     * 
     * <pre>
     * ${csrf.field()}
     * </pre>
     * 
     * to any form to handle CSRF protection.
     * 
     * @return
     *         An input field with the CSRF Token.
     *
     */
    public String field() {
        CSRFToken token = tokenRepository.getToken(getCurrentRequest());
        return "<input type=\"hidden\" name=\"" + token.getParameterName() + "\" value=\"" + token.getToken() + "\"/>";
    }

    /**
     * Returns the name of the form parameter that is expected to contain the CSRF token.
     * 
     * @return
     *         The CSRF Token parameter name
     */
    public String getParameterName() {
        return tokenRepository.getToken(getCurrentRequest()).getParameterName();
    }

    /**
     * Returns the name of the header that is expected to contain the CSRF token.
     * 
     * This is particularly useful when invoking Spring-based REST endpoints.
     * 
     * @return
     *         The CSRF Token header name
     */
    public String getHeaderName() {
        return tokenRepository.getToken(getCurrentRequest()).getHeaderName();
    }

    private static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attributes.getRequest();
    }

}
