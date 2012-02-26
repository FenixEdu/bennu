/* 
* @(#)FenixFrameworkInitializer.java 
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
package pt.ist.fenixframework;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import myorg._development.PropertiesManager;
import pt.ist.fenixWebFramework.FenixWebFramework;

/**
 * 
 * @author  Luis Cruz
 * 
*/
public class FenixFrameworkInitializer {

    public static final String[] CONFIG_PATHS;

    private static File getApplicationDir() {
	final URL url = FenixFrameworkInitializer.class.getResource("/configuration.properties");
	try {
	    return new File(url.toURI()).getParentFile();
	} catch (final URISyntaxException e) {
	    throw new Error(e);
	}
    }

    static {
	System.out.println("Initializing bennu.");

	final String preInitClassnames = PropertiesManager.getProperty("pre.init.classnames");
	if (preInitClassnames != null) {
	    final String[] classnames = preInitClassnames.split(",");
	    for (final String classname : classnames) {
		if (classname != null && !classname.isEmpty()) {
		    try {
			Class.forName(classname.trim());
		    } catch (final ClassNotFoundException e) {
			throw new Error(e);
		    }
		}
	    }
	}

	final File dir = getApplicationDir();
	final List<String> urls = new ArrayList<String>();
	for (final File file : dir.listFiles()) {
	    if (file.isFile() && file.getName().endsWith(".dml")) {
		try {
		    urls.add(file.getCanonicalPath());
		} catch (IOException e) {
		    throw new Error(e);
		}
	    }
	}
	
	Collections.sort(urls);
	final String[] paths = new String[urls.size()];
	for (int i = 0; i < urls.size(); i++) {
	    paths[i] = urls.get(i);
	}
	try {
	    CONFIG_PATHS = paths;
	    FenixWebFramework.bootStrap(PropertiesManager.getFenixFrameworkConfig(paths));
	} catch (Throwable t) {
	    t.printStackTrace();
	    throw new Error(t);
	}

    }

}
