package myorg.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.domain.contents.INode;
import myorg.domain.contents.Node;
import myorg.presentationTier.Context;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping( path="/node" )
@Forwards( {
    @Forward(name="page", path="/page.jsp")
} )
public class NodeProcessorAction extends ContextBaseAction {

    public final ActionForward viewElement(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);
	if (context.getElements().isEmpty()) {
	    final INode node = Node.getFirstTopLevelNode();
	    context.push(node);
	}
	return mapping.findForward("page");
    }

}
