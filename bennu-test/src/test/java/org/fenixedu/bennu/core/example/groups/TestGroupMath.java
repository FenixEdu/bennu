package org.fenixedu.bennu.core.example.groups;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.ManualGroupRegister;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ist.fenixframework.FenixFramework;

public class TestGroupMath {
    private static User user1;
    private static User user2;

    private static Group anonymous;
    private static Group anyone;
    private static Group logged;
    private static Group nobody;

    @BeforeClass
    public static void setupUsers() {
        ManualGroupRegister.ensure();
        FenixFramework.atomic(() -> {
            user1 = User.findByUsername("user1");
            if (user1 == null) {
                user1 = new User("user1", ManualGroupRegister.newProfile());
            }
            user2 = User.findByUsername("user2");
            if (user2 == null) {
                user2 = new User("user2", ManualGroupRegister.newProfile());
            }
        });
    }

    @BeforeClass
    public static void setup() {
        anonymous = Group.anonymous();
        anyone = Group.anyone();
        logged = Group.logged();
        nobody = Group.nobody();
    }

    @Test
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
    public void notMath() {
        assertEquals(anonymous.not(), logged);
        assertEquals(anyone.not(), nobody);
        assertEquals(logged.not(), anonymous);
        assertEquals(nobody.not(), anyone);

        assertEquals(nobody.not().not(), nobody);
    }

    @Test
    public void grantMath() {
        try {
            anonymous.grant(null);
            fail();
        } catch (NullPointerException e) {
        }
    }

    @Test
    public void revokeMath() {
        try {
            anonymous.revoke(null);
            fail();
        } catch (NullPointerException e) {
        }
    }
}
