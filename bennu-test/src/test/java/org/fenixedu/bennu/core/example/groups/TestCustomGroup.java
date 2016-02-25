package org.fenixedu.bennu.core.example.groups;

import java.util.stream.Stream;

import static org.junit.Assert.assertNotNull;

import org.fenixedu.bennu.core.annotation.GroupArgument;
import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.CustomGroup;
import org.fenixedu.bennu.core.groups.CustomGroupRegistry;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.GroupStrategy;
import org.fenixedu.bennu.core.groups.ManualGroupRegister;
import org.joda.time.DateTime;
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

    @GroupOperator("foo.bar")
    public static class GroupWithInvalidOperatorName extends GroupStrategy {

        @Override
        public String getPresentationName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Stream<User> getMembers() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Stream<User> getMembers(DateTime when) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isMember(User user) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isMember(User user, DateTime when) {
            throw new UnsupportedOperationException();
        }
    }

    @GroupOperator("foobar")
    public static class GroupWithInvalidFieldName extends GroupStrategy {

        @GroupArgument("invalid.field")
        private String thisFieldIsInvalid;

        @Override
        public String getPresentationName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Stream<User> getMembers() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Stream<User> getMembers(DateTime when) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isMember(User user) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isMember(User user, DateTime when) {
            throw new UnsupportedOperationException();
        }
    }

    @Test(expected = DomainException.class)
    public void testInvalidOperatorNameThrowsException() {
        CustomGroupRegistry.registerCustomGroup(GroupWithInvalidOperatorName.class);
    }

    @Test(expected = DomainException.class)
    public void testInvalidFieldNameThrowsException() {
        CustomGroupRegistry.registerCustomGroup(GroupWithInvalidFieldName.class);
    }

}
