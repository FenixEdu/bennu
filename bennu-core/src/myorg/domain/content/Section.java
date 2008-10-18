package myorg.domain.content;

import java.io.Serializable;
import java.util.Comparator;

import myorg.domain.MyOrg;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.util.DomainReference;
import pt.ist.fenixframework.pstm.Transaction;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class Section extends Section_Base {

    public static final Comparator<Section> COMPARATOR_BY_ORDER = new Comparator<Section>() {

	@Override
	public int compare(final Section section1, final Section section2) {
	    final int c = section1.getSectionOrder().compareTo(section2.getSectionOrder());
	    return c == 0 ? section1.hashCode() - section2.hashCode() : c;
	}

    };

    public static class SectionBean implements Serializable {

	private static final long serialVersionUID = 1L;

	MultiLanguageString title;
	MultiLanguageString contents;
	DomainReference<Page> page;

	public SectionBean(final Page page) {
	    setPage(page);
	}

	public MultiLanguageString getTitle() {
	    return title;
	}
	public void setTitle(MultiLanguageString title) {
	    this.title = title;
	}
	public MultiLanguageString getContents() {
	    return contents;
	}
	public void setContents(MultiLanguageString contents) {
	    this.contents = contents;
	}
	public Page getPage() {
	    return page == null ? null : page.getObject();
	}
	public void setPage(Page page) {
	    this.page = page == null ? null : new DomainReference<Page>(page);
	}
    }

    public Section() {
        super();
        setMyOrg(MyOrg.getInstance());
    }

    public Section(final SectionBean sectionBean) {
	this();
	setTitle(sectionBean.getTitle());
	setContents(sectionBean.getContents());
	setPage(sectionBean.getPage());
    }

    @Service
    public static void createNewSection(final SectionBean sectionBean) {
	new Section(sectionBean);
    }

    @Override
    public void setPage(final Page page) {
	if (page != null) {
	    final int order = page.getSectionsCount() + 1;
	    setSectionOrder(Integer.valueOf(order));
	}
	super.setPage(page);
    }

    @Service
    public void delete() {
	removePage();
	removeMyOrg();
	Transaction.deleteObject(this);
    }

}
