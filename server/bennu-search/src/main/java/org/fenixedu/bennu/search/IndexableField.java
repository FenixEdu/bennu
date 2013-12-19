package org.fenixedu.bennu.search;

/**
 * This interface is used in objects that represent index names.
 * 
 * You should implement this interface when there's the need to search for
 * specific indexes of a given domain object.
 * 
 * @author Paulo Abrantes
 */
public interface IndexableField {
    public String getFieldName();
}
