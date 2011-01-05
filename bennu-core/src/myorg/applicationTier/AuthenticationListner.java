package myorg.applicationTier;

import java.util.HashSet;
import java.util.Set;

import myorg.applicationTier.Authenticate.UserView;

public interface AuthenticationListner {

    public static final Set<AuthenticationListner> LOGIN_LISTNERS = new HashSet<AuthenticationListner>();

    public void afterLogin(final UserView userView);

}
