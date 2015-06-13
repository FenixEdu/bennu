package org.fenixedu.bennu.core.example.groups;

import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.bennu.core.example.domain.groups.UsersCreatedAfterGroup.UsersCreatedAfter;
import org.fenixedu.bennu.core.groups.AnonymousGroup;
import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.core.groups.CustomGroupRegistry;
import org.fenixedu.bennu.core.groups.CustomGroupRegistry.BooleanParser;
import org.fenixedu.bennu.core.groups.CustomGroupRegistry.DateTimeParser;
import org.fenixedu.bennu.core.groups.CustomGroupRegistry.StringParser;
import org.fenixedu.bennu.core.groups.LoggedGroup;
import org.fenixedu.bennu.core.groups.NobodyGroup;
import org.fenixedu.bennu.core.groups.UserGroup;

public class ManualGroupRegister {
    private static boolean done = false;

    public static void ensure() {
        if (!done) {
            CustomGroupRegistry.registerCustomGroup(AnonymousGroup.class);
            CustomGroupRegistry.registerCustomGroup(AnyoneGroup.class);
            CustomGroupRegistry.registerCustomGroup(LoggedGroup.class);
            CustomGroupRegistry.registerCustomGroup(NobodyGroup.class);
            CustomGroupRegistry.registerCustomGroup(UserGroup.class);
            CustomGroupRegistry.registerArgumentParser(UserGroup.UserArgumentParser.class);

            CustomGroupRegistry.registerCustomGroup(UsersCreatedAfter.class);
            CustomGroupRegistry.registerArgumentParser(BooleanParser.class);
            CustomGroupRegistry.registerArgumentParser(StringParser.class);
            CustomGroupRegistry.registerArgumentParser(DateTimeParser.class);
            done = true;
        }
    }

    public static UserProfile newProfile() {
        return new UserProfile("Test", "User", null, null, null);
    }
}
