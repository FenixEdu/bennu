/* 
* @(#)ClassNameResolverRenderer.java 
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
package pt.ist.bennu.core.presentationTier.renderers;

import org.apache.commons.lang.StringUtils;

import pt.ist.bennu.core.i18n.BundleUtil;
import pt.ist.bennu.core.util.legacy.ClassNameBundle;
import pt.ist.fenixWebFramework.renderers.OutputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

/**
 * 
 * @author Luis Cruz
 * @author Paulo Abrantes
 * 
 */
public class ClassNameResolverRenderer extends OutputRenderer {

    @Override
    protected Layout getLayout(Object arg0, Class arg1) {
        return new Layout() {

            @Override
            public HtmlComponent createComponent(Object arg0, Class arg1) {
                Class<?> someClass = (Class<?>) arg0;
                ClassNameBundle annotation = someClass.getAnnotation(ClassNameBundle.class);
                if (annotation != null) {
                    String key = annotation.key();
                    return new HtmlText(BundleUtil.getString(annotation.bundle(), !StringUtils.isEmpty(key) ? key : "label."
                            + someClass.getName()));
                }
                return new HtmlText(someClass.getName());
            }
        };
    }
}
