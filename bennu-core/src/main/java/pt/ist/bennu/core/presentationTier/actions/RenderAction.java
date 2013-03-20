/* 
* @(#)RenderAction.java 
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
package pt.ist.bennu.core.presentationTier.actions;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.renderers.OutputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.contexts.OutputContext;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectFactory;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;
import pt.ist.fenixWebFramework.renderers.utils.RenderKit;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.fenixframework.DomainObject;

@Mapping(path = "/render")
/**
 * 
 * @author  Luis Cruz
 * @author  Paulo Abrantes
 * 
 */
public class RenderAction extends BaseAction {

    public ActionForward renderOutput(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {

        DomainObject domainObject = getDomainObject(request, "oid");
        String layout = request.getParameter("layout");
        String schema = request.getParameter("schema");
        String properties = request.getParameter("properties");

        RenderKit instance = RenderKit.getInstance();
        OutputContext context = new OutputContext();
        Schema realSchema = instance.findSchema(schema);

        context.setLayout(layout);
        context.setSchema(realSchema);
        if (properties != null) {
            context.setProperties(readProperties(properties));
        }

        context.setMetaObject(MetaObjectFactory.createObject(domainObject, realSchema));

        OutputRenderer renderer = (OutputRenderer) instance.getRenderer(context, domainObject.getClass(), layout);
        HtmlComponent component = instance.renderUsing(renderer, context, domainObject, domainObject.getClass());

        response.setContentType("text/html;charset=UTF-8");
        Writer writer = response.getWriter();

        component.draw(writer);
        writer.flush();
        writer.close();

        return null;
    }

    private Properties readProperties(String propertiesString) {
        Properties properties = new Properties();
        for (String property : propertiesString.split("\\|")) {
            String[] parts = property.split(":");
            properties.put(parts[0], parts[1]);
        }
        return properties;
    }
}
