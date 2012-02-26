/* 
* @(#)MyOrgRestApplication.java 
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
package myorg.presentationTier.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

import pt.ist.fenixWebFramework.servlets.rest.RestAnnotationProcessor;
import pt.utl.ist.fenix.tools.util.FileUtils;

/**
 * 
 * @author  Shezad Anavarali
 * @author  Luis Cruz
 * 
*/
public class MyOrgRestApplication extends Application {

    @Override
    public Restlet createRoot() {
	Router router = new Router(getContext());

	for (final Entry<String, Class<? extends ServerResource>> entry : loadActionsFromFile().entrySet()) {
	    router.attach(entry.getKey(), entry.getValue());
	}

	return router;
    }

    private Map<String, Class<? extends ServerResource>> loadActionsFromFile() {
	final Map<String, Class<? extends ServerResource>> resourcesClasses = new HashMap<String, Class<? extends ServerResource>>();
	final InputStream inputStream = getClass().getResourceAsStream("/.restAnnotationLog");
	if (inputStream != null) {
	    try {
		final String contents = FileUtils.readFile(inputStream);
		for (final String classname : contents.split(RestAnnotationProcessor.ENTRY_SEPERATOR)) {
		    try {
			String[] values = classname.split(RestAnnotationProcessor.LINE_SEPERATOR);
			if (values.length == 2) {
			    ClassLoader loader = Thread.currentThread().getContextClassLoader();
			    Class<?> type = loader.loadClass(values[0]);
			    resourcesClasses.put(values[1], (Class<? extends ServerResource>) type);
			}

		    } catch (final ClassNotFoundException e) {
			e.printStackTrace();
		    }
		}
	    } catch (final IOException e) {
		e.printStackTrace();
	    }
	}
	return resourcesClasses;
    }

}
