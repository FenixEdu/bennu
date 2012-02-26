/* 
* @(#)DbQuery.java 
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
package myorg.persistenceTier.externalRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @author  Luis Cruz
 * 
*/
public abstract class DbQuery {

    protected abstract String getQueryString();

    protected abstract void processResultSet(final ResultSet resultSet) throws SQLException;

    final Connection connection;

    public DbQuery(final DbHandler dbHandler) {
	connection = dbHandler == null ? null : dbHandler.getConnection();
    }

    public void execute() {
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	try {
	    preparedStatement = connection.prepareStatement(getQueryString());
	    resultSet = preparedStatement.executeQuery();
	    processResultSet(resultSet);
	} catch (final SQLException exception) {
	    throw new Error(exception);
	} finally {
	    if (resultSet != null) {
		try {
		    resultSet.close();
		} catch (final SQLException e) {
		    e.printStackTrace();
		}
	    }
	    if (preparedStatement != null) {
		try {
		    preparedStatement.close();
		} catch (final SQLException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

}
