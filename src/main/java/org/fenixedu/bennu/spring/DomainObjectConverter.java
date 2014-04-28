package org.fenixedu.bennu.spring;

import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.collect.ImmutableSet;

/***
 * Converts a string to a DomainObject
 * 
 * @author Sérgio Silva (sergio.silva@tecnico.ulisboa.pt)
 * 
 */
public class DomainObjectConverter implements ConditionalGenericConverter {
    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return String.class.equals(sourceType.getType()) && DomainObject.class.isAssignableFrom(targetType.getType());
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return ImmutableSet.of(new ConvertiblePair(String.class, DomainObject.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        final DomainObject domainObject = FenixFramework.getDomainObject((String) source);

        //throws ClassCastException if not required domain type
        targetType.getType().cast(domainObject);

        if (FenixFramework.isDomainObjectValid(domainObject)) {
            return domainObject;
        }

        return null;
    }
}
