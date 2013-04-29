/* 
 * @(#)FunctionalityFilter.java 
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
package pt.ist.bennu.core.presentationTier.servlets.filters;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;

import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.domain.contents.ActionNode;
import pt.ist.bennu.core.domain.contents.Node;
import pt.ist.bennu.core.presentationTier.Context;
import pt.ist.bennu.core.presentationTier.actions.ContextBaseAction;
import pt.ist.fenixWebFramework.FenixWebFramework;
import pt.ist.fenixWebFramework.servlets.functionalities.FunctionalityAnnotationProcessor;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.core.Project;

/**
 * 
 * @author Nuno Diegues
 * @author Sérgio Silva
 * 
 */
public class FunctionalityFilter implements Filter {

    public class FunctionalityInfo {
        private final String path;
        private final String method;
        private final String methodAlias;

        public FunctionalityInfo(String path, String method, String methodAlias) {
            this.path = path;
            this.method = method;
            this.methodAlias = methodAlias;
        }

        public String getPath() {
            return path;
        }

        public String getMethod() {
            return method;
        }

        public String getMethodAlias() {
            return methodAlias;
        }

        public String getSemanticLink() {
            return path + methodAlias;
        }

    }

    private class SemanticComponent {
        private final String path;
        private final String methodAlias;
        private final Node matchingNode;

        public SemanticComponent(String path, String methodAlias, Node matchingNode) {
            super();
            this.path = path;
            this.methodAlias = methodAlias;
            this.matchingNode = matchingNode;
        }

        public Node getMatchingNode() {
            return matchingNode;
        }

        public String getPath() {
            return path;
        }

        public String getMethodAlias() {
            return methodAlias;
        }
    }

    // relative link -> functionality
    private static final Map<String, FunctionalityInfo> relativeMapping = new HashMap<String, FunctionalityInfo>();
    // mapping path -> method name -> functionality
    private static final Map<String, Map<String, FunctionalityInfo>> reverseMapping =
            new HashMap<String, Map<String, FunctionalityInfo>>();
    private static boolean checkedConsistency = false;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            for (Project artifact : FenixFramework.getProject().getProjects()) {
                try (InputStream stream =
                        loader.getResourceAsStream(artifact.getName() + File.separatorChar
                                + FunctionalityAnnotationProcessor.FUNCTIONALITIES_FILE)) {
                    if (stream != null) {
                        List<String> mappings = IOUtils.readLines(stream);
                        for (String funcMapping : mappings) {
                            if (funcMapping.length() > 0 && funcMapping.charAt(0) == '#') {
                                continue;
                            }

                            // Populating the runtime reification of the
                            // functionalities
                            String[] mappingComponents = funcMapping.split(",");
                            final FunctionalityInfo functionality =
                                    new FunctionalityInfo(mappingComponents[0], mappingComponents[1],
                                            mappingComponents[2].equals("-") ? null : mappingComponents[2]);

                            relativeMapping.put(functionality.getSemanticLink(), functionality);

                            Map<String, FunctionalityInfo> actionFunctionalities = reverseMapping.get(functionality.getPath());
                            if (actionFunctionalities == null) {
                                actionFunctionalities = new HashMap<String, FunctionalityInfo>();
                                reverseMapping.put(functionality.getPath(), actionFunctionalities);
                            }
                            actionFunctionalities.put(functionality.getMethod(), functionality);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkConsistency() {
        for (VirtualHost virtualHost : MyOrg.getInstance().getVirtualHostsSet()) {
            for (FunctionalityInfo functionality : relativeMapping.values()) {
                // Checking for consistency across nodes' tree branches
                for (Node node : virtualHost.getTopLevelNodesSet()) {
                    final Node matchingNode = node.findMatchNode(functionality.getPath(), functionality.getMethod());
                    if (matchingNode != null) {
                        // Fetch the parent, and navigate to siblings. They
                        // cannot
                        // conflict with the matchingNode
                        Node parentNode = matchingNode.getParentNode();
                        if (parentNode != null) {
                            for (Node sibling : parentNode.getChildNodesSet()) {
                                if (sibling != matchingNode) {
                                    checkForAliasConflict(functionality, sibling);
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    public static boolean checkForAliasConflict(FunctionalityInfo functionality, Node nodeToCheck) {
        if (nodeToCheck instanceof ActionNode) {
            ActionNode currentNode = (ActionNode) nodeToCheck;
            String methodAlias = getMethodAlias((currentNode).getPath(), (currentNode).getMethod());
            if (methodAlias != null && methodAlias.equals(functionality.getMethodAlias())) {
                System.out.println("*WARNING* Conflicting functionalities' semantic links on alias: " + functionality.methodAlias
                        + "\n\tStruts Mapping: " + functionality.getPath() + " Class method: " + functionality.getMethod()
                        + "\n\tStruts Mapping: " + currentNode.getPath() + " Class method: " + currentNode.getMethod());
                // Returns true if there is a conflict
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException {
        FunctionalityInfo functionality = null;
        List<SemanticComponent> linkComponents = null;
        String link = null;

        if (!checkedConsistency) {
            checkConsistency();
            checkedConsistency = true;
        }

        if (!checkedConsistency) {
            checkConsistency();
            checkedConsistency = true;
        }

        if (isSemanticURL((HttpServletRequest) request)) {
            final String url = getSemanticURL((HttpServletRequest) request);
            linkComponents = obtainSemanticComponent(url);
            if (linkComponents != null) {
                if (!verifyAccessControl(linkComponents, (HttpServletRequest) request, (HttpServletResponse) response)) {
                    return;
                }
                SemanticComponent lastPart = linkComponents.get(linkComponents.size() - 1);
                link = lastPart.getPath() + lastPart.getMethodAlias();
            }
            functionality = relativeMapping.get(link);
        }
        if (functionality != null && linkComponents != null) {
            final StringBuilder stringBuilder = new StringBuilder();
            for (SemanticComponent part : linkComponents) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(Context.PATH_PART_SEPERATOR);
                }
                stringBuilder.append(part.getMatchingNode().asString());
                request.setAttribute(ContextBaseAction.CONTEXT_PATH, stringBuilder.toString());
            }
            dispatchTo(request, response, functionality.getPath() + ".do?method=" + functionality.getMethod());
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private List<SemanticComponent> obtainSemanticComponent(final String semanticLink) {
        final List<SemanticComponent> result = new ArrayList<SemanticComponent>();
        StringTokenizer tokenizer = new StringTokenizer(semanticLink, "/");
        Node lastNode = null;

        while (tokenizer.hasMoreTokens()) {
            if (lastNode == null) {
                // Top level node. Fetching path+methodAlias.
                final String path = "/" + tokenizer.nextToken();
                if (path.equals("/")) {
                    continue;
                }

                final String methodAlias = tokenizer.hasMoreTokens() ? "/" + tokenizer.nextToken() : null;
                if (methodAlias == null) {
                    return null;
                }

                final String method = getRealMethodName(path, methodAlias);
                if (method == null) {
                    return null;
                }

                Node matchingNode = null;
                final VirtualHost virtualHost = VirtualHost.getVirtualHostForThread();
                for (Node node : virtualHost.getTopLevelNodesSet()) {
                    matchingNode = node.findMatchNode(path, method);
                    if (matchingNode != null) {
                        break;
                    }
                }
                if (matchingNode == null) {
                    return null;
                }

                result.add(new SemanticComponent(path, methodAlias, matchingNode));
                lastNode = matchingNode;
            } else {
                // Child node. Only have methodAlias. Using the last fetched
                // node to obtain the respective path and real method name. This
                // assumes that the methodAlias is unique in this tree branch.
                // That is maintained on this filter init and Node creation.
                final String methodAlias = "/" + tokenizer.nextToken();
                String path = null;
                String method = null;

                for (Node possibleNode : lastNode.getChildNodesSet()) {
                    if (possibleNode instanceof ActionNode) {
                        path = ((ActionNode) possibleNode).getPath();
                        method = getRealMethodName(path, methodAlias);
                        if (method != null) {
                            break;
                        }
                    }
                }

                if (method == null) {
                    return null;
                }

                Node matchingNode = lastNode.findMatchNode(path, method);
                if (matchingNode == null) {
                    return null;
                }

                result.add(new SemanticComponent(path, methodAlias, matchingNode));
                lastNode = matchingNode;
            }
        }

        return result;
    }

    public static Node getNode(String mapping, String method) {
        final VirtualHost virtualHost = VirtualHost.getVirtualHostForThread();
        for (Node node : virtualHost.getTopLevelNodesSet()) {
            Node result = node.findMatchNode(mapping, method);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private boolean verifyAccessControl(final List<SemanticComponent> linkParts, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        for (final SemanticComponent part : linkParts) {
            final Node node = part.getMatchingNode();
            if (!node.isAccessible()) {
                redirectByTampering(request, response);
                return false;
            }
        }
        return true;
    }

    private void redirectByTampering(HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            httpSession.invalidate();
        }
        // TODO not working!
        response.sendRedirect(FenixWebFramework.getConfig().getTamperingRedirect());
    }

    private boolean isSemanticURL(HttpServletRequest request) {
        return !getSemanticURL(request).contains(".do");
    }

    private String getSemanticURL(HttpServletRequest request) {
        String url = request.getServletPath();
        int end = url.indexOf("&");
        if (end > 0) {
            url = url.substring(0, end);
        }
        return url;
    }

    private void dispatchTo(final ServletRequest servletRequest, final ServletResponse servletResponse, String path)
            throws ServletException, IOException {
        final RequestDispatcher requestDispatcher = servletRequest.getRequestDispatcher(path);
        requestDispatcher.forward(servletRequest, servletResponse);
    }

    public static boolean hasSemanticURL(String path, String method) {
        return getSemanticURL(path, method) != null;
    }

    public static String getSemanticURL(String path, String method) {
        final FunctionalityInfo functionality = getFunctionality(path, method);
        return functionality == null ? null : functionality.getSemanticLink();
    }

    public static String getMethodAlias(String path, String method) {
        final FunctionalityInfo functionality = getFunctionality(path, method);
        return functionality == null ? null : functionality.getMethodAlias();
    }

    public static FunctionalityInfo getFunctionality(String path, String method) {
        final Map<String, FunctionalityInfo> actionFunctionalities = reverseMapping.get(path);
        if (actionFunctionalities == null) {
            return null;
        }

        return actionFunctionalities.get(method);
    }

    public static Map<String, Map<String, FunctionalityInfo>> retrieveFunctionalityMappings() {
        return reverseMapping;
    }

    private String getRealMethodName(String path, String methodAlias) {
        final String semanticLink = path + methodAlias;
        return relativeMapping.containsKey(semanticLink) ? relativeMapping.get(semanticLink).getMethod() : null;
    }
}
