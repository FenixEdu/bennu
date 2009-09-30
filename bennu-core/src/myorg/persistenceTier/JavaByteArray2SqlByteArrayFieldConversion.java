/*
 * @(#)JavaByteArray2SqlByteArrayFieldConversion.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the MyOrg web application infrastructure.
 *
 *   MyOrg is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.*
 *
 *   MyOrg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with MyOrg. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg.persistenceTier;

import myorg.domain.util.ByteArray;

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

public class JavaByteArray2SqlByteArrayFieldConversion implements FieldConversion {

    public Object javaToSql(Object source) {
        if (source instanceof ByteArray) {
            final ByteArray byteArray = (ByteArray) source;
            return byteArray.getBytes();
        }
        return source;
    }

    public Object sqlToJava(Object source) {
        if (source instanceof byte[]) {            
            return new ByteArray((byte[]) source);
        }
        return source;
    }

}
