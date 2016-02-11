package org.fenixedu.bennu.core.example.groups;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.ManualGroupRegister;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.test.core.FenixFrameworkRunner;

@RunWith(FenixFrameworkRunner.class)
public class TestLogged {
    private static User user1;

    @BeforeClass
    public static void setupUsers() {
        ManualGroupRegister.ensure();
        FenixFramework.atomic(() -> {
            user1 = User.findByUsername("user1");
            if (user1 == null) {
                user1 = new User("user1", ManualGroupRegister.newProfile());
            }
        });
    }

    @Test
    public void parse() {
        String expr = "logged";
        assertEquals(Group.parse(expr).getExpression(), expr);
    }

    @Test
    public void membership() {
        assertEquals(Group.logged().getMembers().collect(Collectors.toSet()), Bennu.getInstance().getUserSet());
        assertTrue(Group.logged().isMember(user1));
        assertFalse(Group.logged().isMember(null));
    }

    @Test
    public void loggedCompression() {
        assertEquals(Group.logged(), Group.logged().grant(user1));
        assertEquals(Group.logged(), user1.groupOf().or(Group.logged()));
        assertEquals(user1.groupOf(), Group.logged().and(user1.groupOf()));
        assertEquals(user1.groupOf(), user1.groupOf().and(Group.logged()));
        assertEquals(Group.logged(), user1.groupOf().or(Group.logged()));
    }

    @Test
    public void createPersistent() {
        assertTrue(Group.logged().toPersistentGroup() != null);
    }
}
