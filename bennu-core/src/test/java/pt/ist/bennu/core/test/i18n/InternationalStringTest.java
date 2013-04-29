/*
 * InternationalStringTest.java
 *
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 *
 * This file is part of bennu-core.
 *
 * bennu-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * bennu-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with bennu-core.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.test.i18n;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import pt.ist.bennu.core.i18n.InternationalString;

public class InternationalStringTest {
    private static final Locale pt = Locale.forLanguageTag("pt");
    private static final Locale ptPT = Locale.forLanguageTag("pt-PT");
    private static final Locale enGB = Locale.forLanguageTag("en-GB");
    private static final Locale esES = Locale.forLanguageTag("es-ES");
    private static final Locale ptBR = Locale.forLanguageTag("pt-BR");

    @Test
    public void testBuilderCreation() {
        InternationalString hello = new InternationalString.Builder().with(ptPT, "olá").with(enGB, "hello").build();
        assertEquals(hello.getContent(ptPT), "olá");
        assertEquals(hello.getContent(enGB), "hello");
        assertTrue(hello.getLocales().contains(ptPT));
        assertTrue(hello.getLocales().contains(enGB));
        assertTrue(hello.getLocales().size() == 2);
    }

    @Test
    public void testShortcutCreation() {
        InternationalString hello = new InternationalString(ptPT, "olá");
        assertEquals(hello.getContent(ptPT), "olá");
        assertTrue(hello.getLocales().contains(ptPT));
        assertTrue(hello.getLocales().size() == 1);
    }

    @Test
    public void testEdition() {
        InternationalString hello = new InternationalString.Builder().with(ptPT, "olá").with(enGB, "hello").build();

        InternationalString helloJohn = hello.builder().append(" John").build();
        assertEquals(helloJohn.getContent(ptPT), "olá John");
        assertEquals(helloJohn.getContent(enGB), "hello John");

        InternationalString formalHello =
                hello.builder().append(new InternationalString.Builder().with(ptPT, " Sr.").with(enGB, " Mr.").build()).build();
        assertEquals(formalHello.getContent(ptPT), "olá Sr.");
        assertEquals(formalHello.getContent(enGB), "hello Mr.");

        InternationalString helloPlusSpanish = hello.builder().with(esES, "hola").build();
        assertEquals(helloPlusSpanish.getContent(ptPT), "olá");
        assertEquals(helloPlusSpanish.getContent(enGB), "hello");
        assertEquals(helloPlusSpanish.getContent(esES), "hola");

        InternationalString helloMinusEnglish = hello.builder().without(enGB).build();
        assertNull(helloMinusEnglish.getContent(enGB));
    }

    @Test
    public void testShortcutEditions() {
        InternationalString hello = new InternationalString.Builder().with(ptPT, "olá").with(enGB, "hello").build();

        InternationalString helloJohn = hello.append(" John");
        assertEquals(helloJohn.getContent(ptPT), "olá John");
        assertEquals(helloJohn.getContent(enGB), "hello John");

        InternationalString formalHello = hello.append(new InternationalString(ptPT, " Sr.").with(enGB, " Mr."));
        assertEquals(formalHello.getContent(ptPT), "olá Sr.");
        assertEquals(formalHello.getContent(enGB), "hello Mr.");

        InternationalString helloPlusSpanish = hello.with(esES, "hola");
        assertEquals(helloPlusSpanish.getContent(ptPT), "olá");
        assertEquals(helloPlusSpanish.getContent(enGB), "hello");
        assertEquals(helloPlusSpanish.getContent(esES), "hola");

        InternationalString helloMinusEnglish = hello.without(enGB);
        assertNull(helloMinusEnglish.getContent(enGB));
    }

    @Test
    public void testEquals() {
        InternationalString hello1 = new InternationalString.Builder().with(ptPT, "olá").with(enGB, "hello").build();
        InternationalString hello2 = new InternationalString.Builder().with(enGB, "hello").with(ptPT, "olá").build();
        InternationalString hello3 =
                new InternationalString.Builder().with(enGB, "hello different").with(ptPT, "olá diferente").build();
        InternationalString hello4 = hello1.builder().with(esES, "olá").build();

        assertEquals(hello1, hello2);
        assertNotEquals(hello1, hello3);
        assertNotEquals(hello1, hello4);
    }

    @Test
    public void testEmptyness() {
        InternationalString hello = new InternationalString(ptPT, "olá");
        InternationalString empty = hello.without(ptPT);
        assertTrue(empty.isEmpty());
        assertTrue(new InternationalString().isEmpty());
    }

    @Test
    public void testFallBacks() {
        InternationalString hello1 = new InternationalString(pt, "olá");
        assertEquals(hello1.getContent(ptPT), "olá");

        InternationalString hello2 = new InternationalString.Builder().with(ptPT, "olá").with(ptBR, "oi").build();
        assertTrue(hello2.getContent(pt).equals("olá") || hello2.getContent(pt).equals("oi"));
    }
}
