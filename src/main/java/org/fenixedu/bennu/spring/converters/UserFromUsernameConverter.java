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
package org.fenixedu.bennu.spring.converters;

import java.util.Collections;
import java.util.Set;

import org.fenixedu.bennu.core.domain.User;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Strings;

/***
 * Converts a string to a User object
 * 
 * This converter will try to get the User object by using the source value as an username.
 * If it does not find any matches, it will try to retrieve the User object by using the source value as an externalId, otherwise
 * returns null.
 * 
 * @author Sérgio Silva (sergio.silva@tecnico.ulisboa.pt)
 * 
 */
public class UserFromUsernameConverter implements ConditionalGenericConverter {
    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return String.class.equals(sourceType.getType()) && User.class.isAssignableFrom(targetType.getType());
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, User.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {

        if (Strings.isNullOrEmpty((String) source)) {
            return null;
        }

        User user = User.findByUsername((String) source);

        if (user != null) {
            return user;
        }

        // check if it is an externalId instead of username
        user = FenixFramework.getDomainObject((String) source);
        if (FenixFramework.isDomainObjectValid(user)) {
            return user;
        }

        return null;
    }
}
