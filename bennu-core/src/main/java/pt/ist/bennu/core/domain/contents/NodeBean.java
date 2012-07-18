/* 
* @(#)NodeBean.java 
* 
* Copyright 2011 Instituto Superior Tecnico 
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
package pt.ist.bennu.core.domain.contents;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import pt.ist.bennu.core.domain.groups.PersistentGroup;

/**
 * 
 * @author  Nuno Diegues
 * 
*/
public class NodeBean implements Serializable {

    public static final Set<NodeType> nodeTypes = new HashSet<NodeType>();
    
    public static synchronized void registerNodeType(NodeType type) {
	NodeBean.nodeTypes.add(type);
    }
    
    static {
	NodeBean.registerNodeType(new ActionNodeType());
	NodeBean.registerNodeType(new LinkNodeType());
    }

    private NodeType nodeType;
    private String path;
    private String method;
    private String linkBundle;
    private String linkKey;
    private String url;
    private String argument;
    private boolean useBennuLayout = true;
    private PersistentGroup persistentGroup;

    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    public String getMethod() {
	return method;
    }

    public void setMethod(String method) {
	this.method = method;
    }

    public String getLinkBundle() {
	return linkBundle;
    }

    public void setLinkBundle(String linkBundle) {
	this.linkBundle = linkBundle;
    }

    public String getLinkKey() {
	return linkKey;
    }

    public void setLinkKey(String linkKey) {
	this.linkKey = linkKey;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public String getArgument() {
	return argument;
    }

    public void setArgument(String argument) {
	this.argument = argument;
    }

    public NodeType getNodeType() {
	return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
	this.nodeType = nodeType;
    }

    public boolean isUseBennuLayout() {
	return useBennuLayout;
    }

    public void setUseBennuLayout(boolean useBennuLayout) {
	this.useBennuLayout = useBennuLayout;
    }

    public PersistentGroup getPersistentGroup() {
	return persistentGroup;
    }

    public void setPersistentGroup(PersistentGroup persistentGroup) {
	this.persistentGroup = persistentGroup;
    }

}
