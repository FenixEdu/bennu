package org.fenixedu.bennu.portal.servlet;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PortalLoginServletTest {

    @Test
    public void validateCallbackValid() {
        assertTrue(PortalLoginServlet.validateCallback("http://localhost:8080/test"));
        assertTrue(PortalLoginServlet.validateCallback("http://localhost:8080/hello world"));
        assertTrue(PortalLoginServlet.validateCallback("http://localhost:8080/hello%20world"));
    }

    @Test
    public void validateCallbackInvalid() {
        assertFalse(PortalLoginServlet.validateCallback("http://other:8080/test"));
        assertFalse(PortalLoginServlet.validateCallback("http://localhost:8081/test"));
        assertFalse(PortalLoginServlet.validateCallback("https://localhost:8080/test"));
        assertFalse(PortalLoginServlet.validateCallback("http://localhost/test"));

        assertFalse(PortalLoginServlet.validateCallback("http://localhost:8080@attacker:8080/test"));
    }

}
