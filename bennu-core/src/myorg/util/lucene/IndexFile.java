package myorg.util.lucene;

import myorg.domain.index.DomainIndexFile;

public interface IndexFile {

    public String getName();
    public void setName(String name);
    
    public Long getLastModified();
    public void setLastModified(Long time);

    public Long getLength();
    public void setLength(Long length);
    
    public DomainIndexFile getPersistentIndex();
    
    public RAMIndex getNonPersistentIndex();
    
    public boolean isPersisted();
}

