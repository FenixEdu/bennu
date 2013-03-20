/* 
* @(#)FunctionalitiesMethodProvider.java 
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
package pt.ist.bennu.core.presentationTier.renderers.providers;

import java.util.Collections;
import java.util.Map;

import pt.ist.bennu.core.domain.contents.ActionNode;
import pt.ist.bennu.core.presentationTier.servlets.filters.FunctionalityFilter;
import pt.ist.bennu.core.presentationTier.servlets.filters.FunctionalityFilter.FunctionalityInfo;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

/**
 * 
 * @author Nuno Diegues
 * 
 */
public class FunctionalitiesMethodProvider implements DataProvider {

    @Override
    public Converter getConverter() {
        return null;
    }

    @Override
    public Object provide(Object source, Object currentValue) {
        ActionNode node = (ActionNode) source;
        Map<String, FunctionalityInfo> result = FunctionalityFilter.retrieveFunctionalityMappings().get(node.getPath());
        return result == null ? Collections.EMPTY_SET : result.keySet();
    }

}
