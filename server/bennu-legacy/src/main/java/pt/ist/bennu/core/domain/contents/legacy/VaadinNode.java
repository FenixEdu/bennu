/*
 * @(#)VaadinNode.java
 *
 * Copyright 2010 Instituto Superior Tecnico
 * Founding Authors: Pedro Santos
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Bennu-Vadin Integration Module.
 *
 *   The Bennu-Vadin Integration Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Bennu-Vadin Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Bennu-Vadin Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.bennu.core.domain.contents.legacy;

import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.domain.groups.legacy.PersistentGroup;
import pt.ist.fenixframework.Atomic;

/**
 * 
 * @author Artur Ventura
 * @author Pedro Santos
 * @author Luis Cruz
 * 
 */
public class VaadinNode extends VaadinNode_Base {

    public VaadinNode(final VirtualHost virtualHost, final Node parentNode, final String linkBundle, final String linkKey,
            final String argument, final PersistentGroup accessibilityGroup) {
        super();
        init(virtualHost, parentNode, linkBundle, linkKey, argument, accessibilityGroup, true);
    }

    public VaadinNode(final VirtualHost virtualHost, final Node parentNode, final String linkBundle, final String linkKey,
            final String argument, final PersistentGroup accessibilityGroup, final boolean useBennuLayout) {
        super();
        init(virtualHost, parentNode, linkBundle, linkKey, argument, accessibilityGroup, useBennuLayout);
    }

    protected void init(final VirtualHost virtualHost, final Node parentNode, final String linkBundle, final String linkKey,
            final String argument, final PersistentGroup accessibilityGroup, final boolean useBennuLayout) {
        final String method = useBennuLayout ? "forwardToVaadin" : "forwardToFullVaadin";
        init(virtualHost, parentNode, "/vaadinContext", method, linkBundle, linkKey, accessibilityGroup);
        setArgument(argument);
    }

    @Atomic
    public static VaadinNode createVaadinNode(final VirtualHost virtualHost, final Node parentNode, final String linkBundle,
            final String linkKey, final String argument, final PersistentGroup accessibilityGroup) {
        return new VaadinNode(virtualHost, parentNode, linkBundle, linkKey, argument, accessibilityGroup);
    }

    @Atomic
    public static VaadinNode createVaadinNode(final VirtualHost virtualHost, final Node parentNode, final String linkBundle,
            final String linkKey, final String argument, final PersistentGroup accessibilityGroup, boolean useBennuLayout) {
        return new VaadinNode(virtualHost, parentNode, linkBundle, linkKey, argument, accessibilityGroup, useBennuLayout);
    }

    @Override
    public String getUrl() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.getUrl());
        stringBuilder.append("#");
        stringBuilder.append(getArgument());
        return stringBuilder.toString();
    }

    @Override
    public String getUrl(final String appContext) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.getUrl(appContext));
        stringBuilder.append("#");
        stringBuilder.append(getArgument());
        return stringBuilder.toString();
    }

    @Override
    public boolean isRedirect() {
        return true;
    }

}
