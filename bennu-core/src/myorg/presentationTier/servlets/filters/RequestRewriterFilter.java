package myorg.presentationTier.servlets.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import myorg.presentationTier.servlets.filters.contentRewrite.ContentContextInjectionRewriter;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.ResponseWrapper;

public class RequestRewriterFilter extends pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriterFilter {

    @Override
    protected void writeResponse(FilterChain filterChain, HttpServletRequest httpServletRequest, ResponseWrapper responseWrapper)
	    throws IOException, ServletException {
	responseWrapper.writeRealResponse(new ContentContextInjectionRewriter(httpServletRequest), new GenericChecksumRewriter(httpServletRequest));
    }

}
