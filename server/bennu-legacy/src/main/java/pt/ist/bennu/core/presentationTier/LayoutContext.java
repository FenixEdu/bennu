/* 
* @(#)LayoutContext.java 
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
package pt.ist.bennu.core.presentationTier;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts.action.ActionForward;

import pt.ist.bennu.core.domain.VirtualHost;

/**
 * 
 * @author Pedro Santos
 * @author João Neves
 * @author Luis Cruz
 * @author Paulo Abrantes
 * 
 */
public class LayoutContext extends LegacyContext {

    private String layout;
    private String layoutSubDir;
    private String themeSubDir;

    private String title = "";
    private final List<String> head = new ArrayList<String>();
    private String login;
    private String configurationLink;
    private String profileLink;
    private String helpLink;
    private String googleSearch;
    private String languageSelection;
    private String sideBar;
    private String subMenuTop;
    private String menuTop;

    private String pageOperations;
    private String breadCrumbs;
    private String body;
    private String footer;

    protected void init() {
        final VirtualHost virtualHost = VirtualHost.getVirtualHostForThread();
        final String themeName = virtualHost.getConfiguration().getTheme();
        final String layoutName = themeName;
        init(layoutName, themeName);
    }

    protected void init(final String layoutSubDir, final String themeSubDir) {
        this.layoutSubDir = layoutSubDir;
        this.themeSubDir = themeSubDir;

        final String layoutPrefix = "/layout/" + layoutSubDir;
        this.layout = layoutPrefix + "/layout.jsp";

        login = layoutPrefix + "/login.jsp";
        configurationLink = layoutPrefix + "/configurationLink.jsp";
        profileLink = layoutPrefix + "/profileLink.jsp";
        helpLink = layoutPrefix + "/helpLink.jsp";
        googleSearch = layoutPrefix + "/googleSearch.jsp";
        languageSelection = layoutPrefix + "/languageSelection.jsp";
        sideBar = layoutPrefix + "/sideBar.jsp";
        subMenuTop = layoutPrefix + "/subMenuTop.jsp";
        menuTop = layoutPrefix + "/menuTop.jsp";

        pageOperations = "/blank.jsp";
        breadCrumbs = layoutPrefix + "/breadCrumbs.jsp";
        body = "/blank.jsp";
        footer = layoutPrefix + "/footer.jsp";

        head.add(layoutPrefix + "/head.jsp");
    }

    {
        init();
    }

    public LayoutContext() {
        super();
    }

    public LayoutContext(final String path) {
        super(path);
    }

    public void switchLayoutAndTheme(final String layoutSubDir, final String themeSubDir) {
        init(layoutSubDir, themeSubDir);
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(final String layoutSubDir) {
        switchLayoutAndTheme(layoutSubDir, getThemeSubDir());
    }

    public String getThemeSubDir() {
        return themeSubDir;
    }

    public void setThemeSubDir(final String themeSubDir) {
        switchLayoutAndTheme(layoutSubDir, themeSubDir);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public List<String> getHead() {
        return head;
    }

    @Override
    public void addHead(String head) {
        this.head.add(head);
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

    @Override
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

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setConfigurationLink(String configurationLink) {
        this.configurationLink = configurationLink;
    }

    public String getConfigurationLink() {
        return configurationLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setHelpLink(String helpLink) {
        this.helpLink = helpLink;
    }

    public String getHelpLink() {
        return helpLink;
    }

    public void setGoogleSearch(String googleSearch) {
        this.googleSearch = googleSearch;
    }

    public String getGoogleSearch() {
        return googleSearch;
    }

    public void setLanguageSelection(String languageSelection) {
        this.languageSelection = languageSelection;
    }

    public String getLanguageSelection() {
        return languageSelection;
    }
}