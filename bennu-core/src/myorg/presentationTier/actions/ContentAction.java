package myorg.presentationTier.actions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.domain.content.Node;
import myorg.domain.content.Page;
import myorg.domain.content.Page.PageBean;

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
    @Forward(name="edit.page", path="/editPage.jsp")} )
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

}
