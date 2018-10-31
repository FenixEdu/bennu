/**
 * Copyright © 2018 Instituto Superior Técnico
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

import com.google.gson.JsonElement;

import com.google.gson.JsonParser;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

/**
 * Converts a String to a JsonElement.
 *
 * @author Tiago Pinho (tiagoppinho@gmail.com)
 */
public class JsonElementConverter implements ConditionalGenericConverter {

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return String.class.equals(sourceType.getType()) && JsonElement.class.equals(targetType.getType());
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes(){
        return Collections.singleton(new ConvertiblePair(String.class, JsonElement.class));
    }

    @Override
    public JsonElement convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return new JsonParser().parse((String) source);
    }
}