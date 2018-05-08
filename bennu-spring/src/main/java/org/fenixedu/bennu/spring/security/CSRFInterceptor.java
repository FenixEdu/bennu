package org.fenixedu.bennu.spring.security;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.security.SkipCSRF;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

/**
 * Interceptor to prevent CSRF attacks on POST, PUT and DELETE methods.
 *
 * Individual controller methods may choose to bypass this verification by annotating the method with {@link SkipCSRF}.
 *
 * It will check that any request in the matching conditions contains a {@link CSRFToken} associated with it, either by providing
 * a request parameter or header. A token may be obtained via the {@link CSRFTokenRepository#getToken(HttpServletRequest)} method.
 *
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 * @see CSRFToken
 * @see CSRFTokenRepository
 *
 */
public class CSRFInterceptor implements HandlerInterceptor {

    private static final Set<String> METHODS_TO_FILTER = ImmutableSet.of("");

    private final CSRFTokenRepository tokenRepository;

    public CSRFInterceptor(final CSRFTokenRepository tokenBean) {
        this.tokenRepository = tokenBean;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
            throws Exception {
        // Checks if the request is in conditions to check for CSRF token
        if (METHODS_TO_FILTER.contains(request.getMethod().toLowerCase()) && shouldValidateCSRFToken(handler)) {
            // Acquire the expected and the provided token...
            CSRFToken token = tokenRepository.getToken(request);
            String tokenValue = findToken(token, request);
            // ... and compare them
            if (Strings.isNullOrEmpty(tokenValue) || !tokenValue.equals(token.getToken())) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "CSRF Token not present or incorrect!");
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the given method should be validated for a CSRF Token.
     */
    private boolean shouldValidateCSRFToken(final Object handler) {
        return !((HandlerMethod) handler).getMethod().isAnnotationPresent(SkipCSRF.class);
    }

    /*
     * Retrieving the token provided by the user, either via header or parameter.
     */
    private String findToken(final CSRFToken token, final HttpServletRequest request) {
        String value = request.getParameter(token.getParameterName());
        if (Strings.isNullOrEmpty(value)) {
            value = request.getHeader(token.getHeaderName());
        }
        return value;
    }

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
            final ModelAndView modelAndView) throws Exception {
        // Nothing to do here
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
            final Exception ex) throws Exception {
        // Nothing to do here
    }

}
