/*
 * @(#)AjaxExpandableRenderer.java
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

package myorg.presentationTier.renderers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import pt.ist.fenixWebFramework.renderers.OutputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.contexts.OutputContext;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectFactory;
import pt.ist.fenixWebFramework.renderers.plugin.RenderersRequestProcessorImpl;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;
import pt.ist.fenixframework.DomainObject;

public class AjaxExpandableRenderer extends OutputRenderer {

    private String smallLayout;
    private String smallSchema;
    private final Map<String, String> smallProperties = new HashMap<String, String>();

    private String expandedLayout;
    private String expandedSchema;
    private final Map<String, String> expandedProperties = new HashMap<String, String>();

    private Map<String, String> getSmallPropertiesMap() {
	return smallProperties;
    }

    public void setSmallSubProperty(String property, String value) {
	smallProperties.put(property, value);
    }

    public String getSmallSubProperty(String property) {
	return smallProperties.get(property);
    }

    private Map<String, String> getExpandedPropertiesMap() {
	return expandedProperties;
    }

    public void setExpandedSubProperty(String property, String value) {
	expandedProperties.put(property, value);
    }

    public String getExpandedSubProperty(String property) {
	return expandedProperties.get(property);
    }

    public String getSmallLayout() {
	return smallLayout;
    }

    public void setSmallLayout(String smallLayout) {
	this.smallLayout = smallLayout;
    }

    public String getSmallSchema() {
	return smallSchema;
    }

    public void setSmallSchema(String smallSchema) {
	this.smallSchema = smallSchema;
    }

    public String getExpandedLayout() {
	return expandedLayout;
    }

    public void setExpandedLayout(String expandedLayout) {
	this.expandedLayout = expandedLayout;
    }

    public String getExpandedSchema() {
	return expandedSchema;
    }

    public void setExpandedSchema(String expandedSchema) {
	this.expandedSchema = expandedSchema;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
	return new Layout() {

	    @Override
	    public HtmlComponent createComponent(Object object, Class type) {
		HtmlBlockContainer largerContainer = new HtmlBlockContainer();
		DomainObject domainObject = (DomainObject) object;

		HtmlBlockContainer swapingContainer = new HtmlBlockContainer();

		largerContainer.addChild(swapingContainer);
		String swapingContainerId = HtmlComponent.getValidIdOrName(domainObject.getClass().getSimpleName()
			+ domainObject.getExternalId());
		swapingContainer.setId(swapingContainerId);

		HtmlComponent resultRenderer = getActualRendereringResult(object, type);
		swapingContainer.addChild(resultRenderer);

		HtmlInlineContainer viewMoreSpan = new HtmlInlineContainer();
		String viewMoreId = HtmlComponent.getValidIdOrName("moreAbout" + domainObject.getExternalId());
		viewMoreSpan.setId(viewMoreId);

		HtmlInlineContainer viewLessSpan = new HtmlInlineContainer();
		String viewLessId = HtmlComponent.getValidIdOrName("viewLessAbout" + domainObject.getExternalId());
		viewLessSpan.setId(viewLessId);

		viewMoreSpan.addChild(new HtmlText(RenderUtils.getResourceString("MYORG_RESOURCES", "label.viewMore")));
		viewLessSpan.addChild(new HtmlText(RenderUtils.getResourceString("MYORG_RESOURCES", "label.viewLess")));

		viewMoreSpan.setClasses("link");
		viewLessSpan.setClasses("link");
		viewLessSpan.setStyle("display: none;");

		largerContainer.addChild(viewMoreSpan);
		largerContainer.addChild(viewLessSpan);

		largerContainer.addChild(getScript(domainObject, swapingContainerId, viewMoreId, viewLessId));
		return largerContainer;
	    }

	    private HtmlComponent getScript(DomainObject domainObject, String swapingContainerId, String viewMoreId,
		    String viewLessId) {
		HtmlScript script = new HtmlScript();

		String scriptText =

		"ajaxRequestOnClick(\"#" + viewMoreId + "\",\"" + getExpandLink(domainObject) + "\",\"#" + swapingContainerId
			+ " *\",\"#" + viewMoreId + "\",\"#" + viewLessId + "\");\n" + "ajaxRequestOnClick(\"#" + viewLessId
			+ "\",\"" + getSmallLink(domainObject) + "\",\"#" + swapingContainerId + " *\",\"#" + viewLessId
			+ "\",\"#" + viewMoreId + "\");";

		script.setContentType("text/javascript");
		script.setScript(scriptText);

		return script;
	    }

	    private String getSmallLink(DomainObject domainObject) {
		String url = RenderersRequestProcessorImpl.getCurrentRequest().getContextPath()
			+ "/render.do?method=renderOutput&oid=" + domainObject.getExternalId() + "&schema=" + getSmallSchema()
			+ "&layout=" + getSmallLayout() + "&properties=" + convertProperties(getSmallPropertiesMap());

		String calculateChecksum = GenericChecksumRewriter.calculateChecksum(url);
		return url + "&" + GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME + "=" + calculateChecksum;
	    }

	    private String getExpandLink(DomainObject domainObject) {
		String url = RenderersRequestProcessorImpl.getCurrentRequest().getContextPath()
			+ "/render.do?method=renderOutput&oid=" + domainObject.getExternalId() + "&schema=" + getExpandedSchema()
			+ "&layout=" + getExpandedLayout() + "&properties=" + convertProperties(getExpandedPropertiesMap());

		String calculateChecksum = GenericChecksumRewriter.calculateChecksum(url);
		return url + "&" + GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME + "=" + calculateChecksum;
	    }

	    private String convertProperties(Map<String, String> propertyMap) {
		StringBuilder convertedProperties = new StringBuilder();
		Iterator<String> iterator = propertyMap.keySet().iterator();
		while (iterator.hasNext()) {
		    String key = iterator.next();
		    convertedProperties.append(key);
		    convertedProperties.append(":");
		    convertedProperties.append(propertyMap.get(key));
		    if (iterator.hasNext()) {
			convertedProperties.append("|");
		    }
		}
		return convertedProperties.toString();
	    }

	    private HtmlComponent getActualRendereringResult(Object object, Class type) {
		OutputContext context = getOutputContext();

		context.setSchema(RenderKit.getInstance().findSchema(getSmallSchema()));
		context.setLayout(getSmallLayout());
		context.setProperties(getProperties(getSmallPropertiesMap()));
		RenderKit instance = RenderKit.getInstance();
		DomainObject domainObject = (DomainObject) object;
		context.setMetaObject(MetaObjectFactory.createObject(domainObject, instance.findSchema(getSmallSchema())));

		OutputRenderer renderer = (OutputRenderer) instance.getRenderer(context, domainObject.getClass(),
			getSmallLayout());
		return instance.renderUsing(renderer, context, domainObject, domainObject.getClass());

	    }

	    private Properties getProperties(Map<String, String> smallPropertiesMap) {
		Properties properties = new Properties();
		for (String property : smallPropertiesMap.keySet()) {
		    properties.put(property, smallPropertiesMap.get(property));
		}
		return properties;
	    }

	};
    }
}
