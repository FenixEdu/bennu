package myorg.domain.groups;

import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.MyOrg;
import myorg.domain.User;

import org.joda.time.DateTime;

public class PeopleUserLog extends PeopleUserLog_Base {
    
    public PeopleUserLog(final String operation, final String username, final String groupName) {
	setMyOrg(MyOrg.getInstance());
	setOperationDate(new DateTime());
	final User user = UserView.getCurrentUser();
	setExecutor(user == null ? "" : user.getUsername());
	setOperation(operation);
	setUsername(username);
	setGroupName(groupName);
    }
    
}
