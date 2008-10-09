package myorg.presentationTier.servlets.filters.contentRewrite;

import javax.servlet.http.HttpServletRequest;

public class RequestChecksumFilter extends pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestChecksumFilter {

    @Override
    protected boolean shoudFilterReques(HttpServletRequest httpServletRequest) {
      return !httpServletRequest.getRequestURI().endsWith("/home.do")
      		&& !httpServletRequest.getRequestURI().endsWith("/isAlive.do")
      		&& httpServletRequest.getRequestURI().indexOf("/authenticationAction.do?method=logoutEmptyPage") < 0;
    }
}
