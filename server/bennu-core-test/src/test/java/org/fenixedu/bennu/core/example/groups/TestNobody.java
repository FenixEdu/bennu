package org.fenixedu.bennu.core.example.groups;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentNobodyGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.NobodyGroup;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class TestNobody {
    private static User user1;

    @BeforeClass
    @Atomic(mode = TxMode.WRITE)
    public static void setupUsers() {
        ManualGroupRegister.ensure();
        user1 = User.findByUsername("user1");
        if (user1 == null) {
            user1 = new User("user1");
        }
    }

    @Test
    public void parse() {
        String expr = "nobody";
        assertEquals(Group.parse(expr).getExpression(), expr);
    }

    @Test
    @Atomic(mode = TxMode.READ)
    public void membership() {
        assertTrue(NobodyGroup.get().getMembers().isEmpty());
        assertFalse(NobodyGroup.get().isMember(user1));
        assertFalse(NobodyGroup.get().isMember(null));
    }

    @Test
    @Atomic(mode = TxMode.WRITE)
    public void createPersistent() {
        assertEquals(NobodyGroup.get().toPersistentGroup(), BennuGroupIndex.getGroupConstant(PersistentNobodyGroup.class));
    }

    @Test
    @Atomic(mode = TxMode.WRITE)
    public void gcDoesNotDelete() {
        PersistentGroup.garbageCollect();
        assertTrue(BennuGroupIndex.getGroupConstant(PersistentNobodyGroup.class) != null);
    }
}
