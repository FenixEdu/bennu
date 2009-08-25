/*
 * @(#)LayoutContext.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the MyOrg web application infrastructure.
 *
 *   MyOrg is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.*
 *
 *   MyOrg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with MyOrg. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package myorg.presentationTier;

import java.util.ArrayList;
import java.util.List;

import myorg.domain.Theme;
import myorg.domain.VirtualHost;

import org.apache.struts.action.ActionForward;

public class LayoutContext extends Context {

    private String layout;

    private String title = "";
    private List<String> head = new ArrayList<String>();
    private String pageHeader = "/layout/pageHeader.jsp";
    private String sideBar = "/layout/sideBar.jsp";
    private String subMenuTop = "/layout/subMenuTop.jsp";
    private String menuTop = "/layout/menuTop.jsp";

    private String pageOperations = "/layout/blank.jsp";
    private String breadCrumbs = "/layout/breadCrumbs.jsp";
    private String body = "/layout/blank.jsp";
    private String footer = "/layout/footer.jsp";

    public LayoutContext() {
	super();
	setLayout(VirtualHost.getVirtualHostForThread().getTheme());
	head.add("/layout/head.jsp");
    }

    public LayoutContext(final String path) {
	super(path);
	setLayout(VirtualHost.getVirtualHostForThread().getTheme());
	head.add("/layout/head.jsp");
    }

    public String getLayout() {
	return layout;
    }

    public void setLayout(String layout) {
	this.layout = layout;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public List<String> getHead() {
	return head;
    }

    public void addHead(String head) {
	this.head.add(head);
    }

    public String getPageHeader() {
	return pageHeader;
    }

    public void setPageHeader(String pageHeader) {
	this.pageHeader = pageHeader;
    }

    public String getSideBar() {
	return sideBar;
    }

    public void setSideBar(String sideBar) {
	this.sideBar = sideBar;
    }

    public String getSubMenuTop() {
	return subMenuTop;
    }

    public void setSubMenuTop(String subMenuTop) {
	this.subMenuTop = subMenuTop;
    }

    public String getPageOperations() {
	return pageOperations;
    }

    public void setPageOperations(String pageOperations) {
	this.pageOperations = pageOperations;
    }

    public String getBreadCrumbs() {
	return breadCrumbs;
    }

    public void setBreadCrumbs(String breadCrumbs) {
	this.breadCrumbs = breadCrumbs;
    }

    public String getBody() {
	return body;
    }

    public void setBody(String body) {
	this.body = body;
    }

    public String getFooter() {
	return footer;
    }

    public void setFooter(String footer) {
	this.footer = footer;
    }

    public String getMenuTop() {
	return menuTop;
    }

    public void setMenuTop(String menuTop) {
	this.menuTop = menuTop;
    }

    @Override
    public ActionForward forward(final String body) {
	setBody(body);
	return new ActionForward(getLayout());
    }

    public void setLayout(Theme theme) {
	this.layout = "/CSS/" + theme.getName() + "/layout.jsp";
    }

}
