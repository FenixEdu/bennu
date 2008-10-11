package myorg.domain.content;

import java.io.Serializable;

import myorg.domain.MyOrg;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.util.DomainReference;
import pt.ist.fenixframework.pstm.Transaction;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class Page extends Page_Base {

    public static class PageBean implements Serializable {

	private static final long serialVersionUID = 1L;

	MultiLanguageString title;
	MultiLanguageString link;
	DomainReference<Page> parentPage;

	public PageBean() {
	}

	public MultiLanguageString getTitle() {
	    return title;
	}
	public void setTitle(MultiLanguageString title) {
	    this.title = title;
	}
	public MultiLanguageString getLink() {
	    return link;
	}
	public void setLink(MultiLanguageString link) {
	    this.link = link;
	}
	public Page getParentPage() {
	    return parentPage == null ? null : parentPage.getObject();
	}
	public void setParentPage(Page parentPage) {
	    this.parentPage = parentPage == null ? null : new DomainReference<Page>(parentPage);
	}
    }

    public Page() {
        super();
        setMyOrg(MyOrg.getInstance());
    }

    public Page(final PageBean pageBean) {
	this();
	setTitle(pageBean.getTitle());
	setLink(pageBean.getLink());
	new Node(pageBean.getParentPage(), this, null);
    }

    @Service
    public static Node createNewPage(final PageBean pageBean) {
	final Page page = new Page(pageBean);
	return page.getParentNodesIterator().next();
    }

    public void deleteIfDisconnected() {
	if (!hasAnyParentNodes()) {
	    for (final Node node : getChildNodesSet()) {
		node.delete();
	    }	    
	    removeMyOrg();
	    Transaction.deleteObject(this);
	}
    }

}
