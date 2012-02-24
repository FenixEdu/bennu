package pt.ist.fenixframework.plugins.luceneIndexing.domain;

public class DomainIndexContent extends DomainIndexContent_Base {

    private byte[] contentCache = null;

    public DomainIndexContent(String name, byte[] content) {
	super();
	init(name, name, content);
    }

    @Override
    public byte[] getContent() {
	if (contentCache == null) {
	    contentCache = super.getContent();
	}
	return contentCache;
    }
}
