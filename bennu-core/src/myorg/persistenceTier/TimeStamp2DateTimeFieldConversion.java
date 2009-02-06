/*
 * @(#)TimeStamp2DateTimeFieldConversion.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
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

import java.sql.Timestamp;

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;
import org.joda.time.DateTime;

public class TimeStamp2DateTimeFieldConversion implements FieldConversion {


    public Object javaToSql(Object source) {
        if (source instanceof DateTime) {
            return new Timestamp(((DateTime) source).getMillis());
        }
        return source;
    }


    public Object sqlToJava(Object source) {
        if(source == null || source.equals("")){
            return null;
        } 
        if (source instanceof Timestamp) {
            return new DateTime(((Timestamp) source).getTime());
        }
        return source;
    }

}