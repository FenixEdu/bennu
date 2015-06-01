package org.fenixedu.bennu.spring.security;

import java.io.Serializable;

/**
 * Representation of a token that is used to prevent CSRF attacks.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 */
public class CSRFToken implements Serializable {

    private static final long serialVersionUID = -7758859776587994878L;

    private static final String DEFAULT_HEADER_NAME = "X-CSRF-TOKEN";
    private static final String DEFAULT_PARAMETER_NAME = "_csrf";

    private final String headerName;
    private final String parameterName;
    private final String token;

    /**
     * Constructs a new token with the given value, and default values for both {@code headerName} and {@code parameterName}.
     * 
     * @param token
     *            The value of newly created token
     */
    public CSRFToken(String token) {
        this(DEFAULT_HEADER_NAME, DEFAULT_PARAMETER_NAME, token);
    }

    /**
     * Constructs a new token with the given Header Name, Parameter Name and Value.
     * 
     * @param headerName
     *            The name of the header that is expected to contain the CSRF token
     * @param parameterName
     *            The name of the form parameter that is expected to contain the CSRF token
     * @param token
     *            The value of newly created token
     */
    public CSRFToken(String headerName, String parameterName, String token) {
        this.headerName = headerName;
        this.parameterName = parameterName;
        this.token = token;
    }

    /**
     * Returns the name of the header that is expected to contain the CSRF token.
     * 
     * @return
     *         The CSRF Token header name
     */
    public String getHeaderName() {
        return headerName;
    }

    /**
     * Returns the name of the form parameter that is expected to contain the CSRF token.
     * 
     * @return
     *         The CSRF Token parameter name
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * Returns the value of this token.
     * 
     * @return
     *         A string containing the value of this token
     */
    public String getToken() {
        return token;
    }

}
