package org.fenixedu.bennu.core.example.groups;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.ManualGroupRegister;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class TestUserGroup {
    private static User user1;
    private static User user2;
    private static Group all;

    @BeforeClass
    @Atomic(mode = TxMode.WRITE)
    public static void setupUsers() {
        ManualGroupRegister.ensure();
        user1 = User.findByUsername("user1");
        if (user1 == null) {
            user1 = new User("user1", ManualGroupRegister.newProfile());
        }
        user2 = User.findByUsername("user2");
        if (user2 == null) {
            user2 = new User("user2", ManualGroupRegister.newProfile());
        }
        all = Group.users(user1, user2);
    }

    @Test
    @Atomic(mode = TxMode.READ)
    public void creation() {
        Group allv1 = Group.parse("U(user1, user2)");
        Group allv2 = Group.parse("U(user2, user1)");
        Group allv3 = Group.users(user1, user2);
        Group allv4 = Group.users(user2, user1);
        assertEquals(all, allv1);
        assertEquals(all, allv2);
        assertEquals(all, allv3);
        assertEquals(all, allv4);
    }

    @Test
    @Atomic(mode = TxMode.READ)
    public void membership() {
        assertEquals(all.getMembers(), Bennu.getInstance().getUserSet());
        for (User user : Bennu.getInstance().getUserSet()) {
            assertTrue(all.isMember(user));
        }
        assertFalse(all.isMember(null));
    }

    @Test
    @Atomic(mode = TxMode.READ)
    public void optimizations() {
        Group one = user1.groupOf();
        Group two = user2.groupOf();

        assertEquals(one.and(two), Group.nobody());
        assertEquals(all.and(two), two);
        assertEquals(all.and(all), all);

        assertEquals(one.or(two), all);
        assertEquals(all.or(one), all);
        assertEquals(all.or(all), all);

        assertEquals(all.minus(Group.nobody()), all);
        assertEquals(all.minus(one), two);
        assertEquals(all.minus(one).minus(two), Group.nobody());
        assertEquals(all.minus(all), Group.nobody());

        assertEquals(all.grant(user1), all);

        assertEquals(all.revoke(user1), two);
    }

    @Test
    @Atomic(mode = TxMode.WRITE)
    public void createPersistent() {
        PersistentGroup allP = all.toPersistentGroup();
        PersistentGroup allv1P = Group.parse("U(user1, user2)").toPersistentGroup();
        PersistentGroup allv2P = Group.parse("U(user2, user1)").toPersistentGroup();
        PersistentGroup allv3P = Group.users(user1, user2).toPersistentGroup();
        PersistentGroup allv4P = Group.users(user2, user1).toPersistentGroup();
        assertEquals(allP, allv1P);
        assertEquals(allP, allv2P);
        assertEquals(allP, allv3P);
        assertEquals(allP, allv4P);
    }
}
