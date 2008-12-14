package myorg.presentationTier.actions;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.presentationTier.Context;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.servlets.functionalities.CreateNodeActionAnnotationProcessor;
import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.utl.ist.fenix.tools.util.FileUtils;

@Mapping(path = "/home")
@Forwards( {
    @Forward(name = "page.hello", path = "/node.do?method=viewElement"),
    @Forward(name = "add.content", path = "/newContent.jsp")
})
public class HomeAction extends ContextBaseAction {

    public static class ContentCreator {
	private String path;
	private String bundle;
	private String key;

	private ContentCreator(final String line) {
	    final String[] parts = line.split(CreateNodeActionAnnotationProcessor.FIELD_SEPERATOR);
	    path = parts[2];
	    bundle = parts[0];
	    key = parts[1];
	}

	public String getPath() {
	    return path;
	}
	public void setPath(String path) {
	    this.path = path;
	}
	public String getBundle() {
	    return bundle;
	}
	public void setBundle(String bundle) {
	    this.bundle = bundle;
	}
	public String getKey() {
	    return key;
	}
	public void setKey(String key) {
	    this.key = key;
	}
    }

    public final ActionForward firstPage(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	return new ActionForward("/node.do?method=viewElement");
    }

    public final ActionForward addContent(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Set<ContentCreator> contentCreators = new HashSet<ContentCreator>();
	final InputStream inputStream = getClass().getResourceAsStream("/" + CreateNodeActionAnnotationProcessor.LOG_FILENAME);
	if (inputStream != null) {
	    try {
		final String contents = FileUtils.readFile(inputStream);
		for (final String line : contents.split(CreateNodeActionAnnotationProcessor.ENTRY_SEPERATOR)) {
		    final ContentCreator contentCreator = new ContentCreator(line);
		    contentCreators.add(contentCreator);
		}
	    } catch (final IOException e) {
		e.printStackTrace();
	    }
	}
	request.setAttribute("contentCreators", contentCreators);

	final Context context = getContext(request);
	return context.forward("/newContent.jsp");
    }

}
