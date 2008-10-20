package myorg.presentationTier.actions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.domain.content.Node;
import myorg.domain.content.Page;
import myorg.domain.content.Section;
import myorg.domain.content.Page.PageBean;
import myorg.domain.content.Section.SectionBean;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.utl.ist.fenix.tools.util.i18n.Language;

@Mapping( path="/content" )
@Forwards( { @Forward(name="page", path="/page.jsp"),
    @Forward(name="new.page", path="/newPage.jsp"),
    @Forward(name="edit.page", path="/editPage.jsp"),
    @Forward(name="new.section", path="/newSection.jsp"),
    @Forward(name="edit.section", path="/editSection.jsp")} )
public class ContentAction extends BaseAction {

    public static class LocaleBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Language language;

	public LocaleBean() {
	    language = Language.getLanguage();
	}

	public Language getLanguage() {
	    return language;
	}

	public void setLanguage(Language language) {
	    this.language = language;
	}

    }

    public final ActionForward viewPage(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Collection<Node> contentPath = getContentPath(request);
	if (contentPath.isEmpty()) {
	    final Node node = Node.getFirstTopLevelNode();
	    contentPath.add(node);
	}

	final LocaleBean localeBean = new LocaleBean();
	request.setAttribute("localeBean", localeBean);

	return mapping.findForward("page");
    }

    public final ActionForward prepareCreateNewPage(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final PageBean pageBean = new PageBean();
	request.setAttribute("pageBean", pageBean);
	return mapping.findForward("new.page");
    }

    public final ActionForward createNewPage(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final PageBean pageBean = getRenderedObject();
	final Node node = Page.createNewPage(pageBean);

	final List<Node> contentPath = new ArrayList<Node>();
	contentPath.add(node);
	request.setAttribute("contentPath", contentPath);
	request.setAttribute("selectedNode", node);

	final Collection<Node> menuNodes = getMenuNodes(request, contentPath);
	request.setAttribute("menuNodes", menuNodes);

	return mapping.findForward("page");
    }

    public final ActionForward deletePage(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Node node = getDomainObject(request, "nodeOid");
	node.delete();

	final List<Node> contentPath = new ArrayList<Node>();
	request.setAttribute("contentPath", contentPath);
	final Node newNode = Node.getFirstTopLevelNode();
	request.setAttribute("selectedNode", newNode);

	final Collection<Node> menuNodes = getMenuNodes(request, contentPath);
	request.setAttribute("menuNodes", menuNodes);

	return mapping.findForward("page");
    }

    public final ActionForward prepareEditPage(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Node node = getDomainObject(request, "nodeOid");
	request.setAttribute("selectedNode", node);
	return mapping.findForward("edit.page");
    }

    public final ActionForward prepareAddSection(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Page page = getCurrentPage(request);
	final SectionBean sectionBean = new SectionBean(page);
	request.setAttribute("sectionBean", sectionBean);
	return mapping.findForward("new.section");
    }

    public final ActionForward addSection(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final SectionBean sectionBean = getRenderedObject();
	Section.createNewSection(sectionBean);
	return mapping.findForward("page");
    }

    public final ActionForward deleteSection(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Section section = getDomainObject(request, "sectionOid");
	final Page page = section.getPage();
	section.delete();
	return mapping.findForward("page");
    }

    public final ActionForward prepareEditSection(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Section section = getDomainObject(request, "sectionOid");
	request.setAttribute("section", section);
	return mapping.findForward("edit.section");
    }

    public final ActionForward saveSectionOrders(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Node node = getDomainObject(request, "nodeOid");
	final Page page = node.getChildPage();

	final String[] sectionOrders = request.getParameter("articleOrders").split(";");
	final String[] originalSectionIds = request.getParameter("originalArticleIds").split(";");

	if (sectionOrders.length == originalSectionIds.length) {
	    final ArrayList<Section> originalSections = new ArrayList<Section>(sectionOrders.length);
	    final ArrayList<Section> sections = new ArrayList<Section>(sectionOrders.length);
	    int i = 0;
	    for (final Section section : page.getOrderedSections() ) {
		if (Long.toString(section.getOID()).equals(originalSectionIds[i])) {
		    originalSections.add(section);
		} else {
		    return mapping.findForward("page");
		}
		i++;
	    }
	    for (final String sectionOrder : sectionOrders) {
		final int so = Integer.parseInt(sectionOrder.substring(7));
		sections.add(originalSections.get(so));
	    }
	    page.reorderSections(sections);
	}

	return mapping.findForward("page");
    }

    private void print(String string, String[] strings) {
	System.out.print(string);
	System.out.print(":");
	for (final String s : strings) {
	    System.out.print(" ");
	    System.out.print(s);
	}
	System.out.println();
    }

    public final ActionForward reorderSections(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	request.setAttribute("reorderSections", Boolean.TRUE);
	return mapping.findForward("page");
    }

    public final ActionForward savePageOrders(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Collection<Node> menuNodes = (Collection<Node>) request.getAttribute("menuNodes");

	final String[] nodeOrders = request.getParameter("articleOrders").split(";");
	final String[] originalNodeIds = request.getParameter("originalArticleIds").split(";");

	if (nodeOrders.length == originalNodeIds.length) {
	    final ArrayList<Node> originalNodes = new ArrayList<Node>(nodeOrders.length);
	    final ArrayList<Node> nodes = new ArrayList<Node>(nodeOrders.length);
	    int i = 0;
	    for (final Node node : menuNodes) {
		if (Long.toString(node.getOID()).equals(originalNodeIds[i])) {
		    originalNodes.add(node);
		} else {
		    return mapping.findForward("page");
		}
		i++;
	    }
	    for (final String nodeOrder : nodeOrders) {
		final int no = Integer.parseInt(nodeOrder.substring(11));
		nodes.add(originalNodes.get(no));
	    }
	    Node.reorderSections(nodes);
	}

	final Collection<Node> contentPath = getContentPath(request);
	final Collection<Node> newMenuNodes = getMenuNodes(request, contentPath);
	request.setAttribute("menuNodes", newMenuNodes);

	return mapping.findForward("page");
    }

    public final ActionForward reorderPages(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	request.setAttribute("reorderPages", Boolean.TRUE);
	return mapping.findForward("page");
    }

}
