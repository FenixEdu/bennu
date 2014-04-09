package org.fenixedu.bennu.core.example.groups;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentLoggedGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.LoggedGroup;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class TestLogged {
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
        String expr = "logged";
        assertEquals(Group.parse(expr).getExpression(), expr);
    }

    @Test
    @Atomic(mode = TxMode.READ)
    public void membership() {
        assertEquals(LoggedGroup.get().getMembers(), Bennu.getInstance().getUserSet());
        assertTrue(LoggedGroup.get().isMember(user1));
        assertFalse(LoggedGroup.get().isMember(null));
    }

    @Test
    @Atomic(mode = TxMode.WRITE)
    public void createPersistent() {
        assertEquals(LoggedGroup.get().toPersistentGroup(), BennuGroupIndex.getGroupConstant(PersistentLoggedGroup.class));
    }

    @Test
    @Atomic(mode = TxMode.WRITE)
    public void gcDoesNotDelete() {
        PersistentGroup.garbageCollect();
        assertTrue(BennuGroupIndex.getGroupConstant(PersistentLoggedGroup.class) != null);
    }
}
