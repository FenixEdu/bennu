/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of Bennu Spring.
 *
 * Bennu Spring is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu Spring is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu Spring.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.spring.portal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.servlet.BennuPortalDispatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class PortalHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        PortalHandlerMethod handlerMethod = (PortalHandlerMethod) handler;
        if (handlerMethod.getFunctionality() == null) {
            // The requested controller does not map to a functionality
            return true;
        }
        // If no functionality is selected, look it up from the current request
        if (BennuPortalDispatcher.getSelectedFunctionality(request) == null) {
            MenuFunctionality functionality =
                    MenuFunctionality.findFunctionality(SpringPortalBackend.BACKEND_KEY, handlerMethod.getFunctionality()
                            .getKey());
            if (functionality == null || !functionality.isAvailableForCurrentUser()) {
                request.getRequestDispatcher("/bennu-spring/unauthorized.jsp").forward(request, response);
                return false;
            }
            BennuPortalDispatcher.selectFunctionality(request, functionality);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }

}
