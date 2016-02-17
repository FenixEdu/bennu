package org.fenixedu.bennu.core.example.groups;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.test.core.FenixFrameworkRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.ManualGroupRegister;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(FenixFrameworkRunner.class)
public class TestExpressionCornerCases {

    private static final String USERNAME = "foo@bar.com";

    @BeforeClass
    public static void setupUsers() {
        ManualGroupRegister.ensure();
        FenixFramework.atomic(() -> {
            new User(USERNAME, ManualGroupRegister.newProfile());
        });
    }

    @Test
    public void test() {
        try {
            assertEquals(Group.parse("anyone && nobody").getExpression(), "nobody");
            fail();
        } catch (BennuCoreDomainException e) {
        }
        assertEquals(Group.parse("anyone & nobody | nobody").getExpression(), "nobody");
        assertEquals(Group.parse("(anyone & anyone) | nobody").getExpression(), "anyone");
    }

    @Test
    public void testGroupArgumentsAreProperlyEscaped() {
        User user = User.findByUsername(USERNAME);
        Group group = user.groupOf();
        assertEquals("U('" + USERNAME + "')", group.getExpression());
        Group otherGroup = Group.parse(group.getExpression());
        assertEquals(group, otherGroup);
    }
}
