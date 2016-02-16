package org.fenixedu.bennu.core.domain;

import pt.ist.fenixframework.test.core.FenixFrameworkRunner;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(FenixFrameworkRunner.class)
public class BennuTest {

    @Test
    public void testStringProperties() {
        Bennu bennu = Bennu.getInstance();

        bennu.setProperty("foo", "bar");

        Optional<String> property = bennu.getProperty("foo");

        assertTrue(property.isPresent());
        assertEquals("bar", property.get());
    }

    @Test
    public void testNumericProperties() {
        Bennu bennu = Bennu.getInstance();

        bennu.setProperty("foo", 42);

        Optional<Number> property = bennu.getNumericProperty("foo");

        assertTrue(property.isPresent());
        assertEquals(42, property.get().intValue());
    }

    @Test
    public void testBooleanProperties() {
        Bennu bennu = Bennu.getInstance();

        bennu.setProperty("foo", true);

        Optional<Boolean> property = bennu.getBooleanProperty("foo");

        assertTrue(property.isPresent());
        assertEquals(true, property.get());
    }

    @Test
    public void testPropertyReplaced() {
        Bennu bennu = Bennu.getInstance();

        bennu.setProperty("foo", 42);

        Optional<Number> property = bennu.getNumericProperty("foo");
        assertTrue(property.isPresent());
        assertEquals(42, property.get().intValue());


        bennu.setProperty("foo", "bar");

        Optional<String> prop = bennu.getProperty("foo");

        assertTrue(prop.isPresent());
        assertEquals("bar", prop.get());
    }

    @Test
    public void testRemoves() {
        Bennu bennu = Bennu.getInstance();
        bennu.setProperty("string", "foo");
        bennu.setProperty("number", 42);
        bennu.setProperty("boolean", true);

        assertTrue(bennu.getProperty("string").isPresent());
        assertTrue(bennu.getProperty("number").isPresent());
        assertTrue(bennu.getProperty("boolean").isPresent());

        bennu.removeProperty("number");
        assertTrue(bennu.getProperty("string").isPresent());
        assertFalse(bennu.getProperty("number").isPresent());
        assertTrue(bennu.getProperty("boolean").isPresent());

        bennu.removeProperty("boolean");
        assertTrue(bennu.getProperty("string").isPresent());
        assertFalse(bennu.getProperty("number").isPresent());
        assertFalse(bennu.getProperty("boolean").isPresent());

        bennu.removeProperty("string");
        assertFalse(bennu.getProperty("string").isPresent());
        assertFalse(bennu.getProperty("number").isPresent());
        assertFalse(bennu.getProperty("boolean").isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void testNPEOnNullString() {
        Bennu.getInstance().setProperty("xpto", (String) null);
    }

    @Test(expected = NullPointerException.class)
    public void testNPEOnNullNumber() {
        Bennu.getInstance().setProperty("xpto", (Number) null);
    }

    @Test
    public void testNPEOnNullPropertyName() {
        try {
            Bennu.getInstance().setProperty(null, "foo");
            fail();
        } catch (NullPointerException ignored) {}
        try {
            Bennu.getInstance().setProperty(null, 42);
            fail();
        } catch (NullPointerException ignored) {}
        try {
            Bennu.getInstance().setProperty(null, true);
            fail();
        } catch (NullPointerException ignored) {}
        try {
            Bennu.getInstance().getProperty(null);
            fail();
        } catch (NullPointerException ignored) {}
        try {
            Bennu.getInstance().getNumericProperty(null);
            fail();
        } catch (NullPointerException ignored) {}
        try {
            Bennu.getInstance().getBooleanProperty(null);
            fail();
        } catch (NullPointerException ignored) {}
        try {
            Bennu.getInstance().removeProperty(null);
            fail();
        } catch (NullPointerException ignored) {}
    }

}
