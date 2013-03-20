/* 
* @(#)VariantBean.java 
* 
* Copyright 2009 Instituto Superior Tecnico 
* Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes 
*  
*      https://fenix-ashes.ist.utl.pt/ 
*  
*   This file is part of the Bennu Web Application Infrastructure. 
* 
*   The Bennu Web Application Infrastructure is free software: you can 
*   redistribute it and/or modify it under the terms of the GNU Lesser General 
*   Public License as published by the Free Software Foundation, either version  
*   3 of the License, or (at your option) any later version. 
* 
*   Bennu is distributed in the hope that it will be useful, 
*   but WITHOUT ANY WARRANTY; without even the implied warranty of 
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
*   GNU Lesser General Public License for more details. 
* 
*   You should have received a copy of the GNU Lesser General Public License 
*   along with Bennu. If not, see <http://www.gnu.org/licenses/>. 
*  
*/
package pt.ist.bennu.core.util;

import java.io.Serializable;

import org.joda.time.LocalDate;

import pt.ist.fenixframework.DomainObject;

/**
 * 
 * @author Luis Cruz
 * @author Paulo Abrantes
 * 
 */
public class VariantBean implements Serializable {
    public static enum Type {
        INTEGER, STRING, LOCAL_DATE, DOMAIN_OBJECT, OBJECT
    };

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 1L;

    private Object value;
    private Type type;

    public VariantBean() {
        value = null;
    }

    public Type getType() {
        return type;
    }

    protected void setType(Type type) {
        this.type = type;
    }

    public Integer getInteger() {
        return (Integer) (isType(Type.INTEGER) ? this.value : null);
    }

    public void setInteger(Integer value) {
        this.value = value;
        setType(Type.INTEGER);
    }

    public String getString() {
        return (String) (isType(Type.STRING) ? this.value : null);
    }

    public void setString(String string) {
        this.value = string;
        setType(Type.STRING);
    }

    public LocalDate getLocalDate() {
        return (LocalDate) (isType(Type.LOCAL_DATE) ? this.value : null);
    }

    public void setLocalDate(LocalDate value) {
        this.value = value;
        setType(Type.LOCAL_DATE);
    }

    private boolean isType(Type type) {
        return type.equals(this.getType());
    }

    public void setDomainObject(DomainObject value) {
        this.value = value;
        setType(Type.DOMAIN_OBJECT);
    }

    public <T extends DomainObject> T getDomainObject() {
        return (T) (isType(Type.DOMAIN_OBJECT) ? this.value : null);
    }

    public void setObject(Object object) {
        this.value = object;
        setType(Type.OBJECT);
    }

    public Object getObject() {
        return (isType(Type.OBJECT) ? this.value : null);
    }
}
