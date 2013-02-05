/*
 * UserView.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package pt.ist.bennu.core.security;

import pt.ist.bennu.core.domain.User;

public class UserView {
    private static ThreadLocal<SessionUserWrapper> wrapper = new ThreadLocal<>();

    static SessionUserWrapper getSessionUserWrapper() {
        return wrapper.get();
    }

    static void setSessionUserWrapper(SessionUserWrapper newWrapper) {
        wrapper.set(newWrapper);
    }

    public static User getUser() {
        return wrapper.get() != null ? wrapper.get().getUser() : null;
    }

    public static boolean hasUser() {
        return wrapper.get() != null;
    }

    public static String getPrivateConstantForDigestCalculation() {
        return wrapper.get() != null ? wrapper.get().getPrivateConstantForDigestCalculation() : null;
    }
}
