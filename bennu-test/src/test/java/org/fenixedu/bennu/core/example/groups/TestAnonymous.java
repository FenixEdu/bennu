package org.fenixedu.bennu.core.example.groups;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentAnonymousGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.AnonymousGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class TestAnonymous {
    private static User user1;

    @BeforeClass
    @Atomic(mode = TxMode.WRITE)
    public static void setupUsers() {
        ManualGroupRegister.ensure();
        user1 = User.findByUsername("user1");
        if (user1 == null) {
            user1 = new User("user1", ManualGroupRegister.newProfile());
        }
    }

    @Test
    public void parse() {
        String expr = "anonymous";
        assertEquals(Group.parse(expr).getExpression(), expr);
    }

    @Test
    public void membership() {
        assertTrue(AnonymousGroup.get().getMembers().isEmpty());
        assertTrue(AnonymousGroup.get().isMember(null));
        assertFalse(AnonymousGroup.get().isMember(user1));
    }

    @Test
    @Atomic(mode = TxMode.WRITE)
    public void createPersistent() {
        assertEquals(AnonymousGroup.get().toPersistentGroup(), BennuGroupIndex.groupConstant(PersistentAnonymousGroup.class)
                .findAny().orElse(null));
    }

    @Test
    @Atomic(mode = TxMode.WRITE)
    public void gcDoesNotDelete() {
        PersistentGroup.garbageCollect();
        assertTrue(BennuGroupIndex.groupConstant(PersistentAnonymousGroup.class).findAny().isPresent());
    }
}
