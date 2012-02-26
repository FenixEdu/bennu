/* 
* @(#)TestTransport.java 
* 
* Copyright 2010 Instituto Superior Tecnico 
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
package myorg.communication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import myorg._development.PropertiesManager;
import myorg.communication.transport.PrimiviteWithEnumConverter;
import pt.ist.fenixWebFramework.FenixWebFramework;
import pt.utl.ist.fenix.tools.util.i18n.Language;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

/**
 * 
 * @author  Paulo Abrantes
 * 
*/
public class TestTransport {

    public static void init() {
	final File dir = new File("/Users/ghost/dev/workspace/myorg/build_expenditures/WEB-INF/classes");
	final List<String> urls = new ArrayList<String>();
	for (final File file : dir.listFiles()) {
	    if (file.isFile() && file.getName().endsWith(".dml")) {
		try {
		    urls.add(file.getCanonicalPath());
		} catch (IOException e) {
		    e.printStackTrace();
		    throw new RuntimeException(e);
		}
	    }
	}

	Collections.sort(urls);
	final String[] paths = new String[urls.size()];
	for (int i = 0; i < urls.size(); i++) {
	    paths[i] = urls.get(i);
	}
	try {
	    FenixWebFramework.initialize(PropertiesManager.getFenixFrameworkConfig(paths));
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    public static void main(String[] args) {

	init();

	PrimiviteWithEnumConverter test = new PrimiviteWithEnumConverter();
	// String representation = test.serializeToSend(Boolean.FALSE);
	// System.out.println(representation);
	// Boolean x = test.readObject(representation);
	// System.out.println(x);
	//
	// String representation2 = test.serializeToSend(Boolean.FALSE,
	// Boolean.TRUE, "bla blabla", 12421);
	// Object[] objects = test.readMultipleObjects(representation2);
	// for (Object object : objects) {
	// System.out.println(object.getClass() + " : " + object);
	// }
	//
	// System.out.println(Type.INSTANCE);
	// String representationX = test.serializeToSend(Type.INSTANCE);
	// System.out.println(representationX);
	//
	// Type type = test.readEnum(representationX);
	// System.out.println(type);

	MultiLanguageString mls = new MultiLanguageString();
	mls.setContent(Language.pt, "weeee");
	mls.setContent(Language.en, "weeeehhh");

	String serializeToSend = test.serializeToSend(mls);
	System.out.println(serializeToSend);

	MultiLanguageString mls2 = test.readValueType(serializeToSend);
	for (String content : mls2.getAllContents()) {
	    System.out.println(content);
	}

	System.out.println(mls2.getContent(Language.pt));
	System.out.println(mls2.getContent(Language.en));
    }
}
