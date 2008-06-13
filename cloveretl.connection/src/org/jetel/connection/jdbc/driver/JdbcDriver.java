/*
*    jETeL/Clover - Java based ETL application framework.
*    Copyright (C) 2002-04  David Pavlis <david_pavlis@hotmail.com>
*    
*    This library is free software; you can redistribute it and/or
*    modify it under the terms of the GNU Lesser General Public
*    License as published by the Free Software Foundation; either
*    version 2.1 of the License, or (at your option) any later version.
*    
*    This library is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU    
*    Lesser General Public License for more details.
*    
*    You should have received a copy of the GNU Lesser General Public
*    License along with this library; if not, write to the Free Software
*    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
*/
package org.jetel.connection.jdbc.driver;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;

import org.jetel.connection.jdbc.specific.JdbcSpecific;
import org.jetel.exception.ComponentNotReadyException;
import org.jetel.util.string.StringUtils;

/**
 * JDBC driver represents a JDBC driver. Can be create based on JDBC driver description or by all
 * necessary attributes. Method getDriver() is root knowlage of this class.
 * 
 * @author Martin Zatopek (martin.zatopek@javlinconsulting.cz)
 *         (c) Javlin Consulting (www.javlinconsulting.cz)
 *
 * @created 14.9.2007
 */
public class JdbcDriver {
    
    /**
     * Identifier of the JDBC driver.
     */
    private String database;
    
    /**
     * Name of this JDBC driver is used for communication with user - for example in the clover.GUI.
     */
    private String name;
    
    /**
     * Name of the class, which implements java.sql.Driver interface.
     */
    private String dbDriver;
    
    /**
     * Semicolon delimited list of jar/zip libraries of this JDBC driver.
     */
    private URL[] driverLibraries;

    /**
     * Jdbc specific associated by default with this jdbc driver.
     */
    private JdbcSpecific jdbcSpecific;

    /**
     * Class loader used to get instance of java.sql.Driver class.
     */
    private ClassLoader classLoader;
    private Driver driver;
    
    /**
     * Constructor.
     * @param jdbcDriverDescription
     * @throws ComponentNotReadyException
     */
    public JdbcDriver(JdbcDriverDescription jdbcDriverDescription) throws ComponentNotReadyException {
    	this(jdbcDriverDescription.getDatabase(),
    			jdbcDriverDescription.getName(),
    			jdbcDriverDescription.getDbDriver(),
    			jdbcDriverDescription.getDriverLibraryURLs(),
    			jdbcDriverDescription.getJdbcSpecific());
    }

    /**
     * Constructor.
     * @param database
     * @param name
     * @param dbDriver
     * @param driverLibraries
     * @param jdbcSpecific
     * @throws ComponentNotReadyException
     */
    public JdbcDriver(String database, String name, String dbDriver, URL[] driverLibraries, JdbcSpecific jdbcSpecific) throws ComponentNotReadyException {
    	this.database = database;
    	this.name = name;
    	this.dbDriver = dbDriver;
    	this.driverLibraries = driverLibraries;
    	this.jdbcSpecific = jdbcSpecific;
    	
    	prepareClassLoader();
    	prepareDriver();
    }

    /**
     * @return identifier of this jdbc driver. It is usually name of targeted database.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @return human readable name of jdbc driver.
     */
    public String getName() {
        return !StringUtils.isEmpty(name) ? name : database;
    }

    /**
     * @return jdbc specific associated by default with this jdbc driver
     */
    public JdbcSpecific getJdbcSpecific() {
    	return jdbcSpecific;
    }
    
    /**
     * @return class loader, which is used to create java.sql.Driver instance, created in getDriver() method
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }
    
    /**
     * That is major method of this class. Returns a java.sql.Driver represented by this entity.
     */
    public Driver getDriver() {
    	return driver;
    }

    private void prepareClassLoader() throws ComponentNotReadyException {
        if(driverLibraries != null && driverLibraries.length > 0) {
            classLoader = new URLClassLoader(driverLibraries, Thread.currentThread().getContextClassLoader());
        } else {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
    }
    
    private void prepareDriver() throws ComponentNotReadyException {
        try {
            driver = (Driver) Class.forName(dbDriver, true, getClassLoader()).newInstance();
        } catch (ClassNotFoundException ex1) {
            throw new ComponentNotReadyException("Cannot create JDBC driver '" + getName() + "'. Can not find class: " + ex1.getMessage(), ex1);
        } catch (Exception ex1) {
            throw new ComponentNotReadyException("Cannot create JDBC driver '" + getName() + "'. General exception: " + ex1.getMessage(), ex1);
        }
    }

}
