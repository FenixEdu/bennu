/*
 * @(#)HomeAction.java
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

package myorg.presentationTier.actions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.domain.Theme;
import myorg.domain.VirtualHost;
import myorg.domain.contents.Node;
import myorg.domain.util.ByteArray;
import myorg.presentationTier.Context;
import myorg.presentationTier.LayoutContext;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.servlets.functionalities.CreateNodeActionAnnotationProcessor;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.utl.ist.fenix.tools.util.FileUtils;

@Mapping(path = "/home")
public class HomeAction extends ContextBaseAction {

    public static class ContentCreator {

	public static Comparator<ContentCreator> COMPARATOR_BY_KEY = new Comparator<ContentCreator>() {

	    @Override
	    public int compare(final ContentCreator contentCreator1, final ContentCreator contentCreator2) {
		final String key1 = contentCreator1.getKey();
		final String key2 = contentCreator2.getKey();
		return key1.compareTo(key2);
	    }
	    
	};

	private String path;
	private String bundle;
	private String key;
	private String groupKey;

	private ContentCreator(final String line) {
	    final String[] parts = line.split(CreateNodeActionAnnotationProcessor.FIELD_SEPERATOR);
	    bundle = parts[0];
	    groupKey = parts[1];
	    key = parts[2];
	    path = parts[3];
	}

	public String getPath() {
	    return path;
	}
	public void setPath(String path) {
	    this.path = path;
	}
	public String getBundle() {
	    return bundle;
	}
	public void setBundle(String bundle) {
	    this.bundle = bundle;
	}
	public String getKey() {
	    return key;
	}
	public String getGroupKey() {
	    return groupKey;
	}
	public void setKey(String key) {
	    this.key = key;
	}
    }

    public final ActionForward firstPage(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = VirtualHost.getVirtualHostForThread();
	final SortedSet<Node> nodes = virtualHost.getOrderedTopLevelNodes();
	for (final Node node : nodes) {
	    if (node.isAccessible()) {
		final Context context = getContext(request);
		context.push(node);
		return new ActionForward(node.getUrl());
	    }
	}
	final Context context = getContext(request);
	return context.forward("/noContentsAvailable.jsp");
    }

    public final ActionForward addContent(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
	request.setAttribute("virtualHostToManage", virtualHost);
	final Node node = getDomainObject(request, "parentOfNodesToManageId");
	request.setAttribute("parentOfNodesToManage", node);

	final Map<String, Set<ContentCreator>> contentCreatorsMap = new TreeMap<String, Set<ContentCreator>>();
	final InputStream inputStream = getClass().getResourceAsStream("/" + CreateNodeActionAnnotationProcessor.LOG_FILENAME);
	if (inputStream != null) {
	    try {
		final String contents = FileUtils.readFile(inputStream);
		for (final String line : contents.split(CreateNodeActionAnnotationProcessor.ENTRY_SEPERATOR)) {
		    final ContentCreator contentCreator = new ContentCreator(line);
		    final String groupKey = contentCreator.getGroupKey();
		    final Set<ContentCreator> contentCreators;
		    if (contentCreatorsMap.containsKey(groupKey)) {
			contentCreators = contentCreatorsMap.get(groupKey);
		    } else {
			contentCreators = new TreeSet<ContentCreator>(ContentCreator.COMPARATOR_BY_KEY);
			contentCreatorsMap.put(groupKey, contentCreators);
		    }
		    contentCreators.add(contentCreator);
		}
	    } catch (final IOException e) {
		e.printStackTrace();
	    }
	}
	request.setAttribute("contentCreatorsMap", contentCreatorsMap);

	final Context context = getContext(request);
	return context.forward("/myorg/newContent.jsp");
    }

    public ActionForward previewTheme(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final Theme theme = getDomainObject(request, "themeId");
	request.setAttribute("theme", theme);

	final LayoutContext context = (LayoutContext) getContext(request);
	context.setLayout("/myorg/preview/previewPage.jsp");
	return context.forward("");
    }

    protected ActionForward outputImage(final HttpServletResponse response, final ByteArray byteArray) throws Exception {
	OutputStream outputStream = null;
	try {
	    outputStream = response.getOutputStream();
	    if (byteArray != null) {
		outputStream.write(byteArray.getBytes());
	    }
	} finally {
	    if (outputStream != null) {
		outputStream.close();
	    }
	}
	return null;
    }

    public ActionForward favico(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostId");
	final ByteArray favico = virtualHost.getFavicon();
	return outputImage(response, favico);
    }

    public ActionForward logo(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostId");
	final ByteArray logo = virtualHost.getLogo();
	return outputImage(response, logo);
    }

}
