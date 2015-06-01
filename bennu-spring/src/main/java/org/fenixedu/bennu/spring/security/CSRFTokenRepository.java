package org.fenixedu.bennu.spring.security;

import java.security.SecureRandom;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Repository responsible for the handling of {@link CSRFToken}s.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
public class CSRFTokenRepository {

    private static final String CSRF_TOKEN_ATTRIBUTE = CSRFTokenRepository.class.getName() + ".CSRF_TOKEN";

    private final SecureRandom random = new SecureRandom();

    /**
     * Returns the {@link CSRFToken} associated with the given request.
     * 
     * The default implementation stores the token itself in the {@link HttpSession}, generating a new one, if no token is
     * currently associated.
     * 
     * @param request
     *            The request for which to return the token
     * @return
     *         The token associated with the given request
     */
    public CSRFToken getToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        CSRFToken token = (CSRFToken) session.getAttribute(CSRF_TOKEN_ATTRIBUTE);
        if (token == null) {
            synchronized (session) {
                token = (CSRFToken) session.getAttribute(CSRF_TOKEN_ATTRIBUTE);
                if (token == null) {
                    token = generateNewToken();
                    session.setAttribute(CSRF_TOKEN_ATTRIBUTE, token);
                }
            }
        }
        return token;
    }

    /**
     * Generates a new {@link CSRFToken}.
     * 
     * @return
     *         A new, randomly-generated {@link CSRFToken}
     */
    protected CSRFToken generateNewToken() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return new CSRFToken(Base64.getUrlEncoder().encodeToString(bytes));
    }

}
