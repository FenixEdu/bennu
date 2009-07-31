package myorg.domain.index.interfaces;

import java.util.Set;

public interface Searchable {

    public Set<Indexable> getObjectsToIndex();
}
