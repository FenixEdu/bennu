/* 
 * @(#)VirtualHost.java 
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

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import pt.ist.bennu.core._development.PropertiesManager;
import pt.ist.bennu.core.domain.contents.INode;
import pt.ist.bennu.core.domain.contents.Node;
import pt.ist.fenixWebFramework.Config;
import pt.ist.fenixWebFramework.Config.CasConfig;
import pt.ist.fenixWebFramework.FenixWebFramework;
import pt.ist.fenixframework.Atomic;
import pt.utl.ist.fenix.tools.util.ByteArray;
import pt.utl.ist.fenix.tools.util.i18n.Language;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

/**
 * 
 * @author João Antunes
 * @author João Neves
 * @author Pedro Santos
 * @author Sérgio Silva
 * @author Luis Cruz
 * @author Paulo Abrantes
 * 
 */
public class VirtualHost extends VirtualHost_Base {

    private static final ThreadLocal<VirtualHost> threadVirtualHost = new ThreadLocal<>();

    public static VirtualHost getVirtualHostForThread() {
        return threadVirtualHost.get();
    }

    public static void setVirtualHostForThread(final VirtualHost virtualHost) {
        threadVirtualHost.set(virtualHost);
    }

    public static void releaseVirtualHostFromThread() {
        threadVirtualHost.remove();
    }

    public static VirtualHost setVirtualHostForThread(final String serverName) {
        final MyOrg myOrg = MyOrg.getInstance();
        final Set<VirtualHost> virtualHosts = myOrg.getVirtualHostsSet();
        for (final VirtualHost virtualHost : virtualHosts) {
            if (virtualHost.getHostname().startsWith(serverName)) {
                setVirtualHostForThread(virtualHost);
                return virtualHost;
            }
        }
        final VirtualHost virtualHost = virtualHosts.iterator().next();
        setVirtualHostForThread(virtualHost);
        return virtualHost;
    }

    public VirtualHost(final MyOrg myOrg) {
        setMyOrg(myOrg);
        setHostname("localhost");
        setApplicationTitle(new MultiLanguageString("Bennu Application Title"));
        setApplicationSubTitle(new MultiLanguageString("Bennu Application SubTitle"));
        setApplicationCopyright(new MultiLanguageString("My Organization Name"));
        setGoogleSearchEnabled(Boolean.TRUE);
        setLanguageSelectionEnabled(Boolean.TRUE);
        setBreadCrumbsEnabled(Boolean.TRUE);
        setErrorPage(PropertiesManager.getProperty("errorPage"));
    }

    public VirtualHost(final VirtualHostBean virtualHostBean) {
        setMyOrg(MyOrg.getInstance());
        setHostname(virtualHostBean.getHostname());
        setApplicationTitle(virtualHostBean.getApplicationTitle());
        setApplicationSubTitle(virtualHostBean.getApplicationSubTitle());
        setApplicationCopyright(virtualHostBean.getApplicationCopyright());
        setHtmlTitle(virtualHostBean.getHtmlTitle());
        setGoogleSearchEnabled(virtualHostBean.getGoogleSearchEnabled());
        setLanguageSelectionEnabled(virtualHostBean.getLanguageSelectionEnabled());
        setHelpLink(virtualHostBean.getHelpLink());
        setErrorPage(virtualHostBean.getErrorPage());
        setLayout(virtualHostBean.getLayout());
        if (virtualHostBean.getLogo() != null) {
            virtualHostBean.setLogo(virtualHostBean.getLogo());
        }
        if (virtualHostBean.getFavicon() != null) {
            virtualHostBean.setFavicon(virtualHostBean.getFavicon());
        }
        setBreadCrumbsEnabled(virtualHostBean.getBreadCrumbsEnabled());
        setSupportEmailAddress(virtualHostBean.getSupportEmailAddress());
        setSystemEmailAddress(virtualHostBean.getSystemEmailAddress());
    }

    @Atomic
    public static VirtualHost createVirtualHost(final VirtualHostBean virtualHostBean) {
        return new VirtualHost(virtualHostBean);
    }

    @Override
    public Theme getTheme() {
        Theme theme = super.getTheme();
        return theme != null ? theme : setAndReturnDefaultTheme();
    }

    @Override
    public Layout getLayout() {
        Layout layout = super.getLayout();
        return layout != null ? layout : setAndReturnDefaultLayout();
    }

    @Atomic
    private Theme setAndReturnDefaultTheme() {
        Theme theme = getMyOrg().getThemesSet().iterator().next();
        setTheme(theme);
        return theme;
    }

    @Atomic
    private Layout setAndReturnDefaultLayout() {
        Layout layout = Layout.getLayoutByName("default");
        setLayout(layout);
        return layout;
    }

    @Atomic
    public void deleteService() {
        delete();
    }

    public void delete() {
        if (MyOrg.getInstance().getVirtualHostsSet().size() > 1) {
            for (final Node node : getTopLevelNodesSet()) {
                node.delete();
            }

            // setExpenditureTrackingSystem(null);
            // setExternalAccountingIntegrationSystem(null);
            // setWorkflowSystem(null);
            // setWorkingCapitalSystem(null);
            setTheme(null);
            setMyOrg(null);
            deleteDomainObject();
        }
    }

    public SortedSet<Node> getOrderedTopLevelNodes() {
        final SortedSet<Node> nodes = new TreeSet<>(INode.COMPARATOR_BY_ORDER);
        nodes.addAll(getTopLevelNodesSet());
        return nodes;
    }

    @Atomic
    public void edit(VirtualHostBean bean) {
        setHostname(bean.getHostname());
        setApplicationTitle(bean.getApplicationTitle());
        setApplicationSubTitle(bean.getApplicationSubTitle());
        setHtmlTitle(bean.getHtmlTitle());
        setApplicationCopyright(bean.getApplicationCopyright());
        setGoogleSearchEnabled(bean.getGoogleSearchEnabled());
        setLanguageSelectionEnabled(bean.getLanguageSelectionEnabled());
        setTheme(bean.getTheme());
        setLayout(bean.getLayout());
        setHelpLink(bean.getHelpLink());
        setErrorPage(bean.getErrorPage());
        if (bean.getLogo() != null) {
            bean.setLogo(bean.getLogo());
        }
        if (bean.getFavicon() != null) {
            bean.setFavicon(bean.getFavicon());
        }
        setBreadCrumbsEnabled(bean.getBreadCrumbsEnabled());
        setSupportEmailAddress(bean.getSupportEmailAddress());
        setSystemEmailAddress(bean.getSystemEmailAddress());
    }

    @Override
    public void setHostname(final String hostname) {
        super.setHostname(hostname.toLowerCase());
    }

    @Atomic
    @Override
    public void setFavicon(final ByteArray favicon) {
        super.setFavicon(favicon);
    }

    @Atomic
    @Override
    public void setLogo(final ByteArray logo) {
        super.setLogo(logo);
    }

    public boolean isCasEnabled() {
        final Config config = FenixWebFramework.getConfig();
        final CasConfig casConfig = config.getCasConfig(getHostname());
        return casConfig != null && casConfig.isCasEnabled();
    }

    public Set<Language> getSupportedLanguagesSet() {
        Set<Language> languages = new HashSet<>();
        if (StringUtils.isNotBlank(getSupportedLanguages())) {
            for (String code : getSupportedLanguages().split(":")) {
                languages.add(Language.valueOf(code));
            }
        }
        return languages;
    }

    public boolean supports(final Language language) {
        final String supportedLanguages = getSupportedLanguages();
        return supportedLanguages != null && language != null && supportedLanguages.indexOf(language.name()) >= 0;
    }

    @Atomic
    public void setLanguages(final Set<Language> languages) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final Language language : languages) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(':');
            }
            stringBuilder.append(language.name());
        }
        setSupportedLanguages(stringBuilder.toString());
    }

    public boolean getLanguageSelectionAsMenu() {
        final String supportedLanguages = getSupportedLanguages();
        if (supportedLanguages != null && !supportedLanguages.isEmpty()) {
            final int dotCount = countDots(supportedLanguages);
            if (dotCount < 3) {
                return false;
            }
        }
        return true;
    }

    private int countDots(final String supportedLanguages) {
        int count = 0;
        for (int i = supportedLanguages.indexOf(':'); i > 0 && i < supportedLanguages.length(); i =
                supportedLanguages.indexOf(':', i) + 1) {
            if (i >= 0) {
                count++;
            }
        }
        return count;
    }
    @Deprecated
    public java.util.Set<pt.ist.bennu.core.domain.contents.Node> getTopLevelNodes() {
        return getTopLevelNodesSet();
    }

}
