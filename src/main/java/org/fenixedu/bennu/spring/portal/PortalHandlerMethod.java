package org.fenixedu.bennu.spring.portal;

import org.fenixedu.bennu.portal.model.Functionality;
import org.springframework.web.method.HandlerMethod;

/*
 * HandlerMethod extension to store a Functionality
 */
final class PortalHandlerMethod extends HandlerMethod {

    private final Functionality functionality;

    PortalHandlerMethod(HandlerMethod method, Functionality functionality) {
        super(method.createWithResolvedBean());
        this.functionality = functionality;
    }

    public Functionality getFunctionality() {
        return functionality;
    }

    @Override
    public HandlerMethod createWithResolvedBean() {
        return this;
    }

}
