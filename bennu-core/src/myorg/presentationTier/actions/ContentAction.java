package myorg.presentationTier.actions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.domain.content.Node;
import myorg.domain.content.Page;
import myorg.domain.content.Section;
import myorg.domain.content.Page.PageBean;
import myorg.domain.content.Section.SectionBean;
import myorg.presentationTier.Context;

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
public class ContentAction extends ContextBaseAction {

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
	final Context context = getContext(request);
	if (context.getElements().isEmpty()) {
	    final Node node = Node.getFirstTopLevelNode();
	    context.push(node);
	}
	return mapping.findForward("page");
    }

    public final ActionForward prepareCreateNewPage(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	final Node node = context.getSelectedNode();
	final Page page = node == null ? null : node.getChildPage();
	final PageBean pageBean = new PageBean(page);
	request.setAttribute("pageBean", pageBean);
	return mapping.findForward("new.page");
    }

    public final ActionForward createNewPage(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final PageBean pageBean = getRenderedObject();
	final Node node = Page.createNewPage(pageBean);
	final Context context = getContext(request);
	if (node.getParentPage() == null) {
	    context.pop();
	}
	context.push(node);
	return viewPage(mapping, form, request, response);
    }

    public final ActionForward deletePage(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	final Node node = context.getSelectedNode();
	context.pop(node);
	node.delete();
	return viewPage(mapping, form, request, response);
    }

    public final ActionForward prepareEditPage(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Node node = getDomainObject(request, "nodeOid");
	request.setAttribute("selectedNode", node);
	return mapping.findForward("edit.page");
    }

    public final ActionForward prepareAddSection(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	final Node node = context.getSelectedNode();
	final Page page = node.getChildPage();
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
	section.delete();
	return viewPage(mapping, form, request, response);
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
		    return viewPage(mapping, form, request, response);
		}
		i++;
	    }
	    for (final String sectionOrder : sectionOrders) {
		final int so = Integer.parseInt(sectionOrder.substring(7));
		sections.add(originalSections.get(so));
	    }
	    page.reorderSections(sections);
	}

	return viewPage(mapping, form, request, response);
    }

    public final ActionForward reorderSections(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	request.setAttribute("reorderSections", Boolean.TRUE);
	return viewPage(mapping, form, request, response);
    }

    public final ActionForward savePageOrders(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	final Collection<Node> menuNodes = context.getMenuElements();

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
		    return viewPage(mapping, form, request, response);
		}
		i++;
	    }
	    for (final String nodeOrder : nodeOrders) {
		final int no = Integer.parseInt(nodeOrder.substring(11));
		nodes.add(originalNodes.get(no));
	    }
	    final Node parentNode = context.getParentNode();
	    if (parentNode == null) {
		Node.reorderNodes(nodes);
	    } else {
		parentNode.getChildPage().reorderNodes(nodes);
	    }
	}

	return viewPage(mapping, form, request, response);
    }

    public final ActionForward reorderPages(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	request.setAttribute("reorderPages", Boolean.TRUE);
	return viewPage(mapping, form, request, response);
    }

}
