package org.fenixedu.bennu.core.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AuthenticationContext implements Serializable {

    public class AuthenticationMethodEvent implements Serializable {

        private static final long serialVersionUID = 1L;

        private final String authenticationMethod;
        private final LocalDateTime instant;

        private AuthenticationMethodEvent(final String authenticationMethod, final LocalDateTime instant) {
            this.authenticationMethod = authenticationMethod;
            this.instant = instant;
        }

        public String getAuthenticationMethod() {
            return authenticationMethod;
        }

        public LocalDateTime getInstant() {
            return instant;
        }

    }

    private static final long serialVersionUID = 1L;

    private final User user;
    private AuthenticationMethodEvent[] authenticationMethodEvents;

    public AuthenticationContext(final User user, final String authenticationMethod) {
        this.user = user;
        authenticationMethodEvents = new AuthenticationMethodEvent[] { new AuthenticationMethodEvent(authenticationMethod, LocalDateTime.now()) };
    }

    public User getUser() {
        return user;
    }

    public AuthenticationMethodEvent[] getAuthenticationMethodEvents() {
        return copyAuthenticationMethodEvents(authenticationMethodEvents.length);
    }

    private void addAuthenticationMethodEvent(String authenticationMethod, LocalDateTime instant) {
        final int l = authenticationMethodEvents.length;
        authenticationMethodEvents = copyAuthenticationMethodEvents(l + 1);
        authenticationMethodEvents[l] = new AuthenticationMethodEvent(authenticationMethod, instant);
    }

    public void addAuthenticationMethodEvent(final String authenticationMethod) {
        addAuthenticationMethodEvent(authenticationMethod, LocalDateTime.now());
    }

    public void removeAuthenticationMethodEvent(final AuthenticationMethodEvent authenticationMethodEvent) {
        int r = -1;
        final int l = authenticationMethodEvents.length;
        for (int i = 0; i < l; i++) {
            if (authenticationMethodEvents[i] == authenticationMethodEvent) {
                r = i;
                break;
            }
        }
        if (r >= 0) {
            final AuthenticationMethodEvent[] newArray = new AuthenticationMethodEvent[l - 1];
            for (int i = 0, j = 0; i < l; i++) {
                if (i != r) {
                    newArray[j++] = authenticationMethodEvents[i];
                }
            }
            authenticationMethodEvents = newArray;
        }
    }

    private AuthenticationMethodEvent[] copyAuthenticationMethodEvents(final int size) {
        final AuthenticationMethodEvent[] result = new AuthenticationMethodEvent[size];
        System.arraycopy( authenticationMethodEvents, 0, result, 0, authenticationMethodEvents.length );
        return result;        
    }

}
