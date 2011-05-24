/*
 * @(#)VirtualHostBean.java
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
 *   3 of the License, or (at your option) any later version.*
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

import java.io.InputStream;
import java.io.Serializable;

import myorg._development.PropertiesManager;
import myorg.domain.util.ByteArray;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class VirtualHostBean implements Serializable {

    private String hostname;
    private String supportEmailAddress;
    private MultiLanguageString applicationTitle;
    private MultiLanguageString htmlTitle;
    private MultiLanguageString applicationSubTitle;
    private MultiLanguageString applicationCopyright;
    private Boolean googleSearchEnabled;
    private Boolean languageSelectionEnabled;
    private Boolean breadCrumbsEnabled;

    private Theme theme;
    private String helpLink;
    private String errorPage;
    private ByteArray logo;
    private transient InputStream logoInputStream;
    private String logoFilename;
    private String logoDisplayName;
    private ByteArray favicon;
    private transient InputStream faviconInputStream;
    private String faviconFilename;
    private String faviconDisplayName;

    public String getHelpLink() {
	return helpLink;
    }

    public void setHelpLink(String helpLink) {
	this.helpLink = helpLink;
    }

    public VirtualHostBean() {
	setErrorPage(PropertiesManager.getProperty("errorPage"));
    }

    public VirtualHostBean(VirtualHost virtualHost) {
	setHostname(virtualHost.getHostname());
	setApplicationTitle(virtualHost.getApplicationTitle());
	setApplicationSubTitle(virtualHost.getApplicationSubTitle());
	setHtmlTitle(virtualHost.getHtmlTitle());
	setApplicationCopyright(virtualHost.getApplicationCopyright());
	setGoogleSearchEnabled(virtualHost.getGoogleSearchEnabled());
	setLanguageSelectionEnabled(virtualHost.getLanguageSelectionEnabled());
	setTheme(virtualHost.getTheme());
	setHelpLink(virtualHost.getHelpLink());
	setErrorPage(virtualHost.getErrorPage());
	setBreadCrumbsEnabled(virtualHost.getBreadCrumbsEnabled());
	setSupportEmailAddress(virtualHost.getSupportEmailAddress());
    }

    public String getErrorPage() {
	return errorPage;
    }

    public void setErrorPage(String errorPage) {
	this.errorPage = errorPage;
    }

    public Boolean getGoogleSearchEnabled() {
	return googleSearchEnabled;
    }

    public void setGoogleSearchEnabled(Boolean googleSearchEnabled) {
	this.googleSearchEnabled = googleSearchEnabled;
    }

    public Boolean getLanguageSelectionEnabled() {
	return languageSelectionEnabled;
    }

    public void setLanguageSelectionEnabled(Boolean languageSelectionEnabled) {
	this.languageSelectionEnabled = languageSelectionEnabled;
    }

    public String getHostname() {
	return hostname;
    }

    public void setHostname(String hostname) {
	this.hostname = hostname;
    }

    public MultiLanguageString getApplicationTitle() {
	return applicationTitle;
    }

    public void setApplicationTitle(MultiLanguageString applicationTitle) {
	this.applicationTitle = applicationTitle;
    }

    public MultiLanguageString getApplicationSubTitle() {
	return applicationSubTitle;
    }

    public void setApplicationSubTitle(MultiLanguageString applicationSubTitle) {
	this.applicationSubTitle = applicationSubTitle;
    }

    public MultiLanguageString getApplicationCopyright() {
	return applicationCopyright;
    }

    public void setApplicationCopyright(MultiLanguageString applicationCopyright) {
	this.applicationCopyright = applicationCopyright;
    }

    public void setTheme(Theme theme) {
	this.theme = theme;
    }

    public Theme getTheme() {
	return theme;
    }

    public ByteArray getLogo() {
	return logo;
    }

    public void setLogo(ByteArray logo) {
	this.logo = logo;
    }

    public ByteArray getFavicon() {
	return favicon;
    }

    public void setFavicon(ByteArray favicon) {
	this.favicon = favicon;
    }

    public InputStream getLogoInputStream() {
	return logoInputStream;
    }

    public void setLogoInputStream(InputStream logoInputStream) {
	this.logoInputStream = logoInputStream;
    }

    public String getLogoFilename() {
	return logoFilename;
    }

    public void setLogoFilename(String logoFilename) {
	this.logoFilename = logoFilename;
    }

    public String getLogoDisplayName() {
	return logoDisplayName;
    }

    public void setLogoDisplayName(String logoDisplayName) {
	this.logoDisplayName = logoDisplayName;
    }

    public InputStream getFaviconInputStream() {
	return faviconInputStream;
    }

    public void setFaviconInputStream(InputStream faviconInputStream) {
	this.faviconInputStream = faviconInputStream;
    }

    public String getFaviconFilename() {
	return faviconFilename;
    }

    public void setFaviconFilename(String faviconFilename) {
	this.faviconFilename = faviconFilename;
    }

    public String getFaviconDisplayName() {
	return faviconDisplayName;
    }

    public void setFaviconDisplayName(String faviconDisplayName) {
	this.faviconDisplayName = faviconDisplayName;
    }

    public Boolean getBreadCrumbsEnabled() {
	return breadCrumbsEnabled;
    }

    public void setBreadCrumbsEnabled(Boolean breadCrumbsEnabled) {
	this.breadCrumbsEnabled = breadCrumbsEnabled;
    }

    public MultiLanguageString getHtmlTitle() {
        return htmlTitle;
    }

    public void setHtmlTitle(MultiLanguageString htmlTitle) {
        this.htmlTitle = htmlTitle;
    }

    public String getSupportEmailAddress() {
        return supportEmailAddress;
    }

    public void setSupportEmailAddress(String supportEmailAddress) {
        this.supportEmailAddress = supportEmailAddress;
    }

}
