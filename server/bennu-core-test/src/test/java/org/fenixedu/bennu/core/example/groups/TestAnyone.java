package org.fenixedu.bennu.core.example.groups;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentAnyoneGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.AnyoneGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class TestAnyone {
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
        String expr = "anyone";
        assertEquals(Group.parse(expr).getExpression(), expr);
    }

    @Test
    @Atomic(mode = TxMode.READ)
    public void membership() {
        assertEquals(AnyoneGroup.get().getMembers(), Bennu.getInstance().getUserSet());
        assertTrue(AnyoneGroup.get().isMember(user1));
        assertTrue(AnyoneGroup.get().isMember(null));
    }

    @Test
    @Atomic(mode = TxMode.WRITE)
    public void createPersistent() {
        assertEquals(AnyoneGroup.get().toPersistentGroup(), BennuGroupIndex.getGroupConstant(PersistentAnyoneGroup.class));
    }

    @Test
    @Atomic(mode = TxMode.WRITE)
    public void gcDoesNotDelete() {
        PersistentGroup.garbageCollect();
        assertTrue(BennuGroupIndex.getGroupConstant(PersistentAnyoneGroup.class) != null);
    }
}
