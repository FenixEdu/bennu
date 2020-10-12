package org.fenixedu.bennu.saml.client;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.Error;
import com.onelogin.saml2.exception.SettingsException;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class SAMLClientSDK {
    private static Saml2Settings authSettings = null;
    private static Object lock = new Object();

    public static Auth getAuth(HttpServletRequest request, HttpServletResponse response)
            throws SettingsException, Error {
        synchronized (lock) {
            if(authSettings == null) {
                Map<String, Object> samlData = new HashMap<>();
                SettingsBuilder builder = new SettingsBuilder();
                authSettings = builder.fromFile("saml.auth.properties").fromValues(samlData).build();
            }
        }
        return new Auth(authSettings, request, response);
    }
}
