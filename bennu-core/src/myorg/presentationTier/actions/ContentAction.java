package myorg.presentationTier.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.domain.content.Node;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping( path="/content" )
@Forwards( { @Forward(name="page", path="/page.jsp") } )
public class ContentAction extends BaseAction {

    public final ActionForward viewPage(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response)
    		throws Exception {
	final Collection<Node> contentPath = getContentPath(request);
	if (contentPath.isEmpty()) {
	    final Node node = Node.getFirstTopLevelNode();
	    contentPath.add(node);
	}
	return mapping.findForward("page");
    }

}
