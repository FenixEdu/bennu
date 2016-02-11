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
    public void testNoArguments() {
        assertNotNull(Group.parse("after"));
    }

    @Test
    public void testBooleanArgument() {
        assertNotNull(Group.parse("after(inclusive=true)"));
    }

    @Test
    public void testDefaultArgument() {
        assertNotNull(Group.parse("after('2013')"));
    }

    @Test
    public void testAliasedArgument() {
        assertNotNull(Group.parse("after(things='abc')"));
    }

    @Test
    public void testMultiValueArguments() {
        assertNotNull(Group.parse("after(things=['a b c', def])"));
    }

    @Test
    public void testMultiValueArgumentsOnArrays() {
        assertNotNull(Group.parse("after(x1=['a b c', def])"));
    }

    @Test
    public void testEmptyMultiValueArgument() {
        assertNotNull(Group.parse("after(x1=[])"));
    }

}
