package myorg.presentationTier.actions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.domain.MyOrg;
import myorg.domain.content.Node;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.pstm.Transaction;
import pt.utl.ist.fenix.tools.util.FileUtils;

public abstract class BaseAction extends DispatchAction {

    @Override
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final MyOrg myOrg = MyOrg.getInstance();
	request.setAttribute("myOrg", myOrg);

	final List<Node> contentPath = getContentPathFromParameters(request);
	request.setAttribute("contentPath", contentPath);
	if (contentPath.isEmpty()) {
	    final Node node = Node.getFirstTopLevelNode();
	    request.setAttribute("selectedNode", node);
	} else {
	    final Node selectedNode = contentPath.get(contentPath.size() - 1);
	    request.setAttribute("selectedNode", selectedNode);
	}

	final Collection<Node> menuNodes = getMenuNodes(request, contentPath);
	request.setAttribute("menuNodes", menuNodes);

	return super.execute(mapping, form, request, response);
    }

    protected <T> T getAttribute(final HttpServletRequest request, final String attributeName) {
	final T t = (T) request.getAttribute(attributeName);
	return t == null ? (T) request.getParameter(attributeName) : t;
    }

    protected <T extends DomainObject> T getDomainObject(final String value) {
	final Long oid = value != null ? Long.valueOf(value) : null;
	return oid == null ? null : (T) Transaction.getObjectForOID(oid.longValue());
    }

    protected <T extends DomainObject> T getDomainObject(final HttpServletRequest request, final String attributeName) {
	final String parameter = request.getParameter(attributeName);
	final Long oid = parameter != null ? Long.valueOf(parameter) : (Long) request.getAttribute(attributeName);
	return oid == null ? null : (T) Transaction.getObjectForOID(oid.longValue());
    }

    protected <T extends Object> T getRenderedObject() {
	final IViewState viewState = RenderUtils.getViewState();
	return (T) getRenderedObject(viewState);
    }

    protected <T extends Object> T getRenderedObject(final String id) {
	final IViewState viewState = RenderUtils.getViewState(id);
	return (T) getRenderedObject(viewState);
    }

    protected <T extends Object> T getRenderedObject(final IViewState viewState) {
	if (viewState != null) {
	    MetaObject metaObject = viewState.getMetaObject();
	    if (metaObject != null) {
		return (T) metaObject.getObject();
	    }
	}
	return null;
    }

    protected byte[] consumeInputStream(final InputStream inputStream) {
	byte[] result = null;
	if (inputStream != null) {
	    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    try {
		try {
		    FileUtils.copy(inputStream, byteArrayOutputStream);
		    byteArrayOutputStream.flush();
		    result = byteArrayOutputStream.toByteArray();
		    byteArrayOutputStream.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    } finally {
		try {
		    inputStream.close();
		    byteArrayOutputStream.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
	return result;
    }

    protected ActionForward download(final HttpServletResponse response, final String filename, final byte[] bytes,
	    final String contentType) throws IOException {
	final OutputStream outputStream = response.getOutputStream();
	response.setContentType(contentType);
	response.setHeader("Content-disposition", "attachment; filename=" + filename.replace(" ", "_"));
	response.setContentLength(bytes.length);
	if (bytes != null) {
	    outputStream.write(bytes);
	}
	outputStream.flush();
	outputStream.close();
	return null;
    }

    protected List<Node> getContentPathFromParameters(final HttpServletRequest httpServletRequest) {
	final List<Node> contentPath = new ArrayList<Node>();

	final String contentPathString = getAttribute(httpServletRequest, "contentPathString");
	if (contentPathString != null) {
	    final String[] contentPathParts = contentPathString.split(",");
	    for (final String part : contentPathParts) {
		final Node node = getDomainObject(part);
		addNode(contentPath, node);
	    }
	}
	final Node node = getDomainObject(httpServletRequest, "nodeOid");
	addNode(contentPath, node);

	return contentPath;
    }

    protected void addNode(final Collection<Node> contentPath, final Node node) {
	if (node != null) {
	    contentPath.add(node);
	}
    }

    protected Collection<Node> getMenuNodes(final HttpServletRequest httpServletRequest, final Collection<Node> contentPath) {
	final Collection<Node> menuNodes = new TreeSet<Node>(Node.COMPARATOR_BY_ORDER);
	menuNodes.addAll(MyOrg.getInstance().getTopLevelNodesSet());
	return menuNodes;
    }

    protected Collection<Node> getContentPath(final HttpServletRequest httpServletRequest) {
	return (Collection<Node>) httpServletRequest.getAttribute("contentPath");
    }
}
