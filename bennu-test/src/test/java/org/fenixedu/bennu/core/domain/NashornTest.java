package org.fenixedu.bennu.core.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class NashornTest {

    @Test
    public void testCallable() throws Exception {
        NashornStrategy<Callable<String>> strategy =
                new NashornStrategy<>(Callable.class, "function call() { return 'Hello world'; }");
        Callable<String> callable = strategy.getStrategy();
        assertNotNull(callable);
        assertEquals("Hello world", callable.call());
    }

    @Test
    public void testMyInterface() throws Exception {
        NashornStrategy<MyCalculator> strategy =
                new NashornStrategy<>(MyCalculator.class, "function calc(one, other) { return one + other; }");
        MyCalculator calc = strategy.getStrategy();
        assertNotNull(calc);
        assertEquals(3, calc.calc(1, 2));
    }

    public static interface MyCalculator {
        public int calc(int one, int other);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInterfaceRequired() {
        new NashornStrategy<>(Object.class, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCode() {
        new NashornStrategy<>(Callable.class, "funct xpto() {}");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCodeDoesNotImplementInterface() {
        new NashornStrategy<>(Callable.class, "function xpto() {}");
    }

}
