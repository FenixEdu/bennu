package myorg.presentationTier.util;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import myorg.applicationTier.Authenticate.UserView;

import org.apache.struts.action.ActionMapping;

public class ExceptionInformation {

    public static final String ORIGINAL_MAPPING_KEY = "original_mapping_key";

    private String exceptionInfo;
    private String requestContext;
    private String sessionContext;
    private String stackTrace;

    public static String buildUncaughtExceptionInfo(HttpServletRequest request, Throwable ex) {

	final StringBuilder exceptionInfo = headerAppend(ex);

	// user
	userInfoContextAppend(request, exceptionInfo);

	// mappings
	mappingContextAppend(request, exceptionInfo);

	// requestContext
	exceptionInfo.append("\n[RequestContext] \n");
	requestContextAppend(request, exceptionInfo);
	exceptionInfo.append("\n\n");

	// sessionContext
	exceptionInfo.append("\n[SessionContext]\n");
	sessionContextAppend(request, exceptionInfo);
	exceptionInfo.append("\n\n");

	// stackTrace
	stackTrace2StringAppend(ex.getStackTrace(), exceptionInfo);

	return exceptionInfo.toString();
    }

    private static StringBuilder headerAppend(Throwable ex) {
	StringBuilder exceptionInfo = new StringBuilder("- - - - - - - - - - - Error Origin - - - - - - - - - - -\n");
	exceptionInfo.append("\n[Exception] ").append(ex.toString()).append("\n\n");
	return exceptionInfo;
    }

    private static void userInfoContextAppend(HttpServletRequest request, final StringBuilder exceptionInfo) {
	exceptionInfo.append("[UserLoggedIn] ");

	UserView userView = UserView.getCurrentUserView();
	if (userView != null) {
	    exceptionInfo.append(userView.getUsername()).append("\n");
	} else {
	    exceptionInfo.append("No user logged in, or session was lost.\n");
	}
	exceptionInfo.append("\n");
    }

    private static void mappingContextAppend(HttpServletRequest request, final StringBuilder exceptionInfo) {
	exceptionInfo.append("[RequestURI] ").append(request.getRequestURI()).append("\n");
	exceptionInfo.append("[RequestURL] ").append(request.getRequestURL()).append("\n");
	exceptionInfo.append("[QueryString] ").append(request.getQueryString()).append("\n");

	if (request.getAttribute(ORIGINAL_MAPPING_KEY) != null) {
	    ActionMapping mapping = (ActionMapping) request.getAttribute(ORIGINAL_MAPPING_KEY);
	    exceptionInfo.append("[Path] ").append(mapping.getPath()).append("\n");
	    exceptionInfo.append("[Name] ").append(mapping.getName()).append("\n");
	} else {
	    exceptionInfo.append("[Path|Name] impossible to get (exception through UncaughtExceptionFilter)\n");
	}
    }

    private static void requestContextAppend(HttpServletRequest request, StringBuilder exceptionInfo) {

	Enumeration<?> requestContents = request.getAttributeNames();
	while (requestContents.hasMoreElements()) {
	    String requestElement = requestContents.nextElement().toString();
	    exceptionInfo.append("RequestElement:").append(requestElement).append("\n");
	    exceptionInfo.append("RequestElement Value:").append(request.getAttribute(requestElement)).append("\n");
	}
    }

    private static void sessionContextAppend(HttpServletRequest request, StringBuilder exceptionInfo) {

	HttpSession session = request.getSession(false);
	if (session != null) {
	    Enumeration<?> sessionContents = session.getAttributeNames();
	    while (sessionContents.hasMoreElements()) {
		String sessionElement = sessionContents.nextElement().toString();
		exceptionInfo.append("Element:").append(sessionElement).append("\n");
		exceptionInfo.append("Element Value:").append(session.getAttribute(sessionElement)).append("\n");
	    }
	}
    }

    private static void stackTrace2StringAppend(StackTraceElement[] stackTrace, StringBuilder exceptionInfo) {

	exceptionInfo.append("StackTrace: \n ");
	if (stackTrace != null) {
	    int i = 0;
	    while (i < stackTrace.length) {
		exceptionInfo.append(stackTrace[i++]).append("\n");
	    }
	}
    }

    public String getRequestContext() {
	return requestContext;
    }

    private void setRequestContext(String requestContext) {
	this.requestContext = requestContext;
    }

    public String getSessionContext() {
	return sessionContext;
    }

    private void setSessionContext(String sessionContext) {
	this.sessionContext = sessionContext;
    }

    public String getStackTrace() {
	return stackTrace;
    }

    private void setStackTrace(String stackTrace) {
	this.stackTrace = stackTrace;
    }

    public String getExceptionInfo() {
	return exceptionInfo;
    }

    private void setExceptionInfo(String exceptionInfo) {
	this.exceptionInfo = exceptionInfo;
    }

}
