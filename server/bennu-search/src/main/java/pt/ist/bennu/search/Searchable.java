package pt.ist.bennu.search;

import java.util.Set;

/**
 * This interface is used in objects that contribute to some index although they
 * are not the actual result of the search we want. On a given search if there's
 * a hit on the data that was contributed by this object, the object (or
 * objects) that will be returned are in fact the Indexables that are returned
 * by the method getObjectsToIndex, not this one.
 * 
 * @author Paulo Abrantes
 */
public interface Searchable {
    public Set<Indexable> getObjectsToIndex();
}
