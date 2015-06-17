package org.fenixedu.bennu.core.example.groups;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.ManualGroupRegister;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestExpressionCornerCases {

    @BeforeClass
    public static void setupUsers() {
        ManualGroupRegister.ensure();
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
}
