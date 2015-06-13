package org.fenixedu.bennu.core.example.groups;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.AnonymousGroup;
import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.core.groups.LoggedGroup;
import org.fenixedu.bennu.core.groups.NobodyGroup;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class TestGroupMath {
    private static User user1;
    private static User user2;

    private static AnonymousGroup anonymous;
    private static AnyoneGroup anyone;
    private static LoggedGroup logged;
    private static NobodyGroup nobody;

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
    }

    @BeforeClass
    @Atomic(mode = TxMode.WRITE)
    public static void setup() {
        anonymous = AnonymousGroup.get();
        anyone = AnyoneGroup.get();
        logged = LoggedGroup.get();
        nobody = NobodyGroup.get();
    }

    @Test
    @Atomic(mode = TxMode.READ)
    public void andMath() {
        try {
            anonymous.and(null);
            fail();
        } catch (NullPointerException e) {
        }

        assertEquals(anonymous.and(anonymous), anonymous);
        assertEquals(anyone.and(anyone), anyone);
        assertEquals(logged.and(logged), logged);
        assertEquals(nobody.and(nobody), nobody);

        assertEquals(anonymous.and(anyone), anonymous);
        assertEquals(anonymous.and(logged), nobody);
        assertEquals(anonymous.and(nobody), nobody);

        assertEquals(anyone.and(anonymous), anonymous);
        assertEquals(anyone.and(logged), logged);
        assertEquals(anyone.and(nobody), nobody);

        assertEquals(logged.and(anonymous), nobody);
        assertEquals(logged.and(anyone), logged);
        assertEquals(logged.and(nobody), nobody);

        assertEquals(nobody.and(anonymous), nobody);
        assertEquals(nobody.and(anyone), nobody);
        assertEquals(nobody.and(logged), nobody);
    }

    @Test
    @Atomic(mode = TxMode.READ)
    public void orMath() {
        try {
            anonymous.or(null);
            fail();
        } catch (NullPointerException e) {
        }
        assertEquals(anonymous.or(anonymous), anonymous);
        assertEquals(anyone.or(anyone), anyone);
        assertEquals(logged.or(logged), logged);
        assertEquals(nobody.or(nobody), nobody);

        assertEquals(anonymous.or(anyone), anyone);
        assertEquals(anonymous.or(logged), anyone);
        assertEquals(anonymous.or(nobody), anonymous);

        assertEquals(anyone.or(anonymous), anyone);
        assertEquals(anyone.or(logged), anyone);
        assertEquals(anyone.or(nobody), anyone);

        assertEquals(logged.or(anonymous), anyone);
        assertEquals(logged.or(anyone), anyone);
        assertEquals(logged.or(nobody), logged);

        assertEquals(nobody.or(anonymous), anonymous);
        assertEquals(nobody.or(anyone), anyone);
        assertEquals(nobody.or(logged), logged);
    }

    @Test
    @Atomic(mode = TxMode.READ)
    public void minusMath() {
        try {
            anonymous.minus(null);
            fail();
        } catch (NullPointerException e) {
        }
        assertEquals(anonymous.minus(anonymous), nobody);
        assertEquals(anyone.minus(anyone), nobody);
        assertEquals(logged.minus(logged), nobody);
        assertEquals(nobody.minus(nobody), nobody);

        assertEquals(anonymous.minus(anyone), nobody);
        assertEquals(anonymous.minus(logged), anonymous);
        assertEquals(anonymous.minus(nobody), anonymous);

        assertEquals(anyone.minus(anonymous), logged);
        assertEquals(anyone.minus(logged), anonymous);
        assertEquals(anyone.minus(nobody), anyone);

        assertEquals(logged.minus(anonymous), logged);
        assertEquals(logged.minus(anyone), nobody);
        assertEquals(logged.minus(nobody), logged);

        assertEquals(nobody.minus(anonymous), nobody);
        assertEquals(nobody.minus(anyone), nobody);
        assertEquals(nobody.minus(logged), nobody);
    }

    @Test
    @Atomic(mode = TxMode.READ)
    public void notMath() {
        assertEquals(anonymous.not(), logged);
        assertEquals(anyone.not(), nobody);
        assertEquals(logged.not(), anonymous);
        assertEquals(nobody.not(), anyone);

        assertEquals(nobody.not().not(), nobody);
    }

    @Test
    @Atomic(mode = TxMode.READ)
    public void grantMath() {
        try {
            anonymous.grant(null);
            fail();
        } catch (NullPointerException e) {
        }
    }

    @Test
    @Atomic(mode = TxMode.READ)
    public void revokeMath() {
        try {
            anonymous.revoke(null);
            fail();
        } catch (NullPointerException e) {
        }
    }
}
