/*
 * @(#)SQL2JavaConverters.java
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

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

public class SQL2JavaConverters {
    public static FieldConversion LocalDate2SqlFieldConverter = new LocalDate2SqlFieldConverter();
    public static FieldConversion MultiLanguageString2SqlMultiLanguageStringConversion = new MultiLanguageString2SqlMultiLanguageStringConversion();
    public static FieldConversion TimeStamp2DateTimeFieldConversion = new TimeStamp2DateTimeFieldConversion();
    public static FieldConversion BigDecimalConverter = new BigDecimalConverter();
    public static FieldConversion JavaByteArray2SqlByteArrayFieldConversion = new JavaByteArray2SqlByteArrayFieldConversion();
    public static FieldConversion JavaMoney2SqlMoneyFieldConversion = new JavaMoney2SqlMoneyFieldConversion();
    public static FieldConversion AddressConverter = new AddressConverter();
    public static FieldConversion StringsConverter = new StringsConverter();
}
