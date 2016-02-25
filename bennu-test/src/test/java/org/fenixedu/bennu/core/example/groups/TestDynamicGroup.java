package org.fenixedu.bennu.core.example.groups;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.groups.Group;
import org.junit.Test;

public class TestDynamicGroup {

    @Test
    public void testValidIdentifiers() {
        Group.dynamic("managers");
        Group.dynamic("Az0_");
        Group.dynamic("_3fJI");
    }

    @Test(expected = DomainException.class)
    public void testInvalidEmptyIdentifier() {
        Group.dynamic("");
    }

    @Test(expected = DomainException.class)
    public void invalidIdentifierThrowsException() {
        Group.dynamic("hello!");
    }

}
