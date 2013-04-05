/* 
 * @(#)Theme.java 
 * 
 * Copyright 2009 Instituto Superior Tecnico 
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes 
 *  
 *      https://fenix-ashes.ist.utl.pt/ 
 *  
 *   This file is part of the Bennu Web Application Infrastructure. 
 * 
 *   The Bennu Web Application Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version  
 *   3 of the License, or (at your option) any later version. 
 * 
 *   Bennu is distributed in the hope that it will be useful, 
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 *   GNU Lesser General Public License for more details. 
 * 
 *   You should have received a copy of the GNU Lesser General Public License 
 *   along with Bennu. If not, see <http://www.gnu.org/licenses/>. 
 *  
 */
package pt.ist.bennu.core.domain;

import pt.ist.fenixframework.Atomic;

/**
 * 
 * @author Luis Cruz
 * @author Paulo Abrantes
 * 
 */
public class Theme extends Theme_Base {

    public Theme(String name, ThemeType type) {
        super();
        setMyOrg(MyOrg.getInstance());
        setName(name);
        setType(type);
    }

    public Theme(String name, String description, ThemeType type) {
        this(name, type);
        setDescription(description);
    }

    public Theme(String name, String description, ThemeType type, String screenshotFileName) {
        this(name, description, type);
        setScreenshotFileName(screenshotFileName);
    }

    public void delete() {
        setMyOrg(null);
        for (VirtualHost virtualHost : getVirtualHostsSet()) {
            virtualHost.setTheme(null);
        }
        deleteDomainObject();
    }

    public static Theme getThemeByName(String name) {
        for (Theme theme : MyOrg.getInstance().getThemesSet()) {
            if (theme.getName().equals(name)) {
                return theme;
            }
        }
        return null;
    }

    public static boolean isThemeAvailable(String name) {
        return getThemeByName(name) != null;
    }

    @Override
    public String getScreenshotFileName() {
        String screenshotName = super.getScreenshotFileName();
        return screenshotName != null ? screenshotName : getName() + ".jpg";
    }

    @Atomic
    public static Theme createTheme(String themeName, String description, ThemeType type, String screenshotFileName) {
        return new Theme(themeName, description, type, screenshotFileName);
    }

    @Atomic
    public static void deleteTheme(Theme theme) {
        theme.delete();
    }
    @Deprecated
    public java.util.Set<pt.ist.bennu.core.domain.VirtualHost> getVirtualHosts() {
        return getVirtualHostsSet();
    }

}
