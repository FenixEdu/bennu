package myorg.util;

import java.io.Serializable;

import org.joda.time.LocalDate;

import pt.ist.fenixframework.DomainObject;

public class VariantBean implements Serializable {
    public static enum Type {
	INTEGER, STRING, LOCAL_DATE, DOMAIN_OBJECT
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

    public DomainObject getDomainObject(DomainObject value) {
	return (DomainObject) (isType(Type.DOMAIN_OBJECT) ? this.value : null);
    }
}
