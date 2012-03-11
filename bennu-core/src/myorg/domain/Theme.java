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
package myorg.domain;

import pt.ist.fenixWebFramework.services.Service;

/**
 * 
 * @author  Luis Cruz
 * @author  Paulo Abrantes
 * 
*/
public class Theme extends Theme_Base {

    public static enum ThemeType {
	TOP, SIDE;
    }

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
	removeMyOrg();
	while (!getVirtualHosts().isEmpty()) {
	    getVirtualHosts().get(0).removeTheme();
	}
	deleteDomainObject();
    }

    public static Theme getThemeByName(String name) {
	for (Theme theme : MyOrg.getInstance().getThemes()) {
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

    @Service
    public static Theme createTheme(String themeName, String description, ThemeType type, String screenshotFileName) {
	return new Theme(themeName, description, type, screenshotFileName);
    }

    @Service
    public static void deleteTheme(Theme theme) {
	theme.delete();
    }
}
