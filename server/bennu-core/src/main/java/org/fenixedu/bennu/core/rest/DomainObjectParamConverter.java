package org.fenixedu.bennu.core.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Strings;

/***
 * Parameter Converter for Fenix Framework Domain Objects
 * 
 * Converts the string representation of a {@link pt.ist.fenixframework.DomainObject} (usually using the method
 * {@link pt.ist.fenixframework.DomainObject#getExternalId()}) to the DomainObject instance.
 * 
 * If the parameter value is null returns null.
 * 
 * If the parameter type is not a subclass of {@link pt.ist.fenixframework.DomainObject} or
 * {@link pt.ist.fenixframework.DomainObject} is not valid a {@link javax.ws.rs.core.Response} is returned with
 * {@link javax.ws.rs.core.Response.Status#NOT_FOUND}.
 * 
 * @author Sérgio Silva (sergio.silva@tecnico.ulisboa.pt)
 *
 * @param <E> DomainObject subclass for this instance
 */
@Provider
public class DomainObjectParamConverter<E extends DomainObject> implements ParamConverter<E>, ParamConverterProvider {
    private final Class<E> rawType;

    public DomainObjectParamConverter() {
        rawType = null;
    }

    public DomainObjectParamConverter(Class<E> rawType) {
        this.rawType = rawType;
    }

    private E singleValue(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }

        if (!DomainObject.class.isAssignableFrom(rawType)) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        final E domainObject = FenixFramework.getDomainObject(value);
        if (!FenixFramework.isDomainObjectValid(domainObject)) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        return domainObject;
    }

    @Override
    public E fromString(String value) {
        return singleValue(value);
    }

    @Override
    public String toString(E value) {
        return value.getExternalId();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (DomainObject.class.isAssignableFrom(rawType)) {
            return (ParamConverter<T>) new DomainObjectParamConverter<E>((Class<E>) rawType);
        }
        return null;
    }
}
