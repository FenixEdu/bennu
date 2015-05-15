package org.fenixedu.bennu.core.example.groups;

import static org.junit.Assert.assertNotNull;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.ManualGroupRegister;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestCustomGroup {
    @BeforeClass
    public static void setup() {
        ManualGroupRegister.ensure();
    }

    @Test
    public void test() {
        assertNotNull(Group.parse("after"));
        assertNotNull(Group.parse("after(inclusive=true)"));
        assertNotNull(Group.parse("after('2013')"));
        assertNotNull(Group.parse("after(things='abc')"));
        assertNotNull(Group.parse("after(things=['a b c', def])"));
        assertNotNull(Group.parse("after(x1=['a b c', def])"));
        assertNotNull(Group.parse("after(x1=['a b c', def])"));
        assertNotNull(Group.parse("after(x1=[])"));
    }

}
