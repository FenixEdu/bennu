package pt.ist.fenixframework.plugins.luceneIndexing;

import pt.ist.fenixframework.plugins.luceneIndexing.domain.DomainIndexFile;

/**
 * This is an interface needed to allow that both DomainIndexFile (the persisted
 * instance of an index) and the RamIndex (the yet to be persisted index) are
 * dealt by pt.ist.fenixframework.plugins.luceneIndexing.LuceneDomainDirectory
 * in a transparent way.
 * 
 * @author Paulo Abrantes
 * 
 */
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
