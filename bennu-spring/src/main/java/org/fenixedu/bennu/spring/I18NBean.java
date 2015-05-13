/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of Bennu Spring.
 *
 * Bennu Spring is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu Spring is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu Spring.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.spring;

import java.util.Locale;
import java.util.Set;

import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.i18n.I18N;
import org.springframework.context.MessageSource;

/**
 * Utility bean that is meant to be used on JSPs for localized resources
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
public class I18NBean {

    private final MessageSource source;

    I18NBean(MessageSource source) {
        this.source = source;
    }

    public Locale getCurrentLocale() {
        return I18N.getLocale();
    }

    public Set<Locale> getSupportedLocales() {
        return CoreConfiguration.supportedLocales();
    }

    /*
     * This is ugly, but EL does not support varargs
     */

    public String message(String key) {
        return internalMessage(key);
    }

    public String message(String key, Object arg1) {
        return internalMessage(key, arg1);
    }

    public String message(String key, Object arg1, Object arg2) {
        return internalMessage(key, arg1, arg2);
    }

    public String message(String key, Object arg1, Object arg2, Object arg3) {
        return internalMessage(key, arg1, arg2, arg3);
    }

    public String message(String key, Object arg1, Object arg2, Object arg3, Object arg4) {
        return internalMessage(key, arg1, arg2, arg3, arg4);
    }

    public String message(String key, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        return internalMessage(key, arg1, arg2, arg3, arg4, arg5);
    }

    private String internalMessage(String key, Object... args) {
        return source.getMessage(key, args, "!!" + key + "!!", getCurrentLocale());
    }

}
