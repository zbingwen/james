/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.james.userrepository;

import org.apache.avalon.cornerstone.services.store.ObjectRepository;
import org.apache.avalon.cornerstone.services.store.Store;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.james.services.User;
import org.apache.james.services.UsersRepository;

import java.io.File;
import java.util.Iterator;

/**
 * Implementation of a Repository to store users on the File System.
 *
 * Requires a configuration element in the .conf.xml file of the form:
 *  <repository destinationURL="file://path-to-root-dir-for-repository"
 *              type="USERS"
 *              model="SYNCHRONOUS"/>
 * Requires a logger called UsersRepository.
 *
 * @version 1.0.0, 24/04/1999
 * @author  Federico Barbieri <scoobie@pop.systemy.it>
 * @author  <a href="mailto:charles@benett1.demon.co.uk">Charles Benett</a>
 *
 * Last changed by: $Author: pgoldstein $ on $Date: 2002/08/17 18:33:28 $
 * $Revision: 1.7 $
 */
public class UsersFileRepository
    extends AbstractLogEnabled
    implements UsersRepository, Component, Configurable, Composable, Initializable {
 
    protected static boolean DEEP_DEBUG = true;

    /** @deprecated what was this for? */
    private static final String TYPE = "USERS";

    private Store store;
    private ObjectRepository or;
    private String destination;

    /**
     * Pass the <code>Configuration</code> to the instance.
     *
     * @param configuration the class configurations.
     * @throws ConfigurationException if an error occurs
     */
    public void configure( final Configuration configuration )
        throws ConfigurationException {

        destination = configuration.getChild( "destination" ).getAttribute( "URL" );

        if (!destination.endsWith(File.separator)) {
            destination += File.separator;
        }
    }

    /**
     * Pass the <code>ComponentManager</code> to the <code>composer</code>.
     * The instance uses the specified <code>ComponentManager</code> to 
     * acquire the components it needs for execution.
     *
     * @param componentManager The <code>ComponentManager</code> which this
     *                <code>Composable</code> uses.
     * @throws ComponentException if an error occurs
     */
    public void compose( final ComponentManager componentManager )
        throws ComponentException {

        try {
            store = (Store)componentManager.
                lookup( "org.apache.avalon.cornerstone.services.store.Store" );
        } catch (Exception e) {
            final String message = "Failed to retrieve Store component:" + e.getMessage();
            getLogger().error( message, e );
            throw new ComponentException( message, e );
        }
    }

    /**
     * Initialize the component. Initialization includes
     * allocating any resources required throughout the
     * components lifecycle.
     *
     * @throws Exception if an error occurs
     */
    public void initialize()
        throws Exception {

        try {
            //prepare Configurations for object and stream repositories
            final DefaultConfiguration objectConfiguration
                = new DefaultConfiguration( "repository",
                                            "generated:UsersFileRepository.compose()" );

            objectConfiguration.setAttribute( "destinationURL", destination );
            objectConfiguration.setAttribute( "type", "OBJECT" );
            objectConfiguration.setAttribute( "model", "SYNCHRONOUS" );

            or = (ObjectRepository)store.select( objectConfiguration );
            if (getLogger().isDebugEnabled()) {
                StringBuffer logBuffer =
                    new StringBuffer(192)
                            .append(this.getClass().getName())
                            .append(" created in ")
                            .append(destination);
                getLogger().debug(logBuffer.toString());
            }
        } catch (Exception e) {
            if (getLogger().isErrorEnabled()) {
                getLogger().error("Failed to initialize repository:" + e.getMessage(), e );
            }
            throw e;
        }
    }

    public Iterator list() {
        return or.list();
    }

    public synchronized boolean addUser(User user) {
        String username = user.getUserName();
        if (contains(username)) {
            return false;
        }
        try {
            or.put(username, user);
        } catch (Exception e) {
            throw new RuntimeException("Exception caught while storing user: " + e );
        }
        return true;
    }

    public synchronized void addUser(String name, Object attributes) {
        if (attributes instanceof String) {
            User newbie = new DefaultUser(name, "SHA");
            newbie.setPassword( (String) attributes);
            addUser(newbie);
        }
        else {
            throw new RuntimeException("Improper use of deprecated method" 
                                       + " - use addUser(User user)");
        }
    }

    public synchronized User getUserByName(String name) {
        if (contains(name)) {
            try {
                return (User)or.get(name);
            } catch (Exception e) {
                throw new RuntimeException("Exception while retrieving user: "
                                           + e.getMessage());
            }
        } else {
            return null;
        }
    }

    public User getUserByNameCaseInsensitive(String name) {
        String realName = getRealName(name);
        // TODO: This clause is in violation of the contract for the
        //       interface - class should return false if the user
        //       doesn't exist
        if (realName == null ) {
            throw new RuntimeException("No such user");
        }
        return getUserByName(realName);
    }

    public String getRealName(String name) {
        Iterator it = list();
        while (it.hasNext()) {
            String temp = (String) it.next();
            if (name.equalsIgnoreCase(temp)) {
                return temp;
            }
        }
        return null;
    }

    public Object getAttributes(String name) {       
        throw new UnsupportedOperationException("Improper use of deprecated method - read javadocs");
    }

    public boolean updateUser(User user) {
    String username = user.getUserName();
    if (!contains(username)) {
        return false;
    }
        try {
            or.put(username, user);
        } catch (Exception e) {
            throw new RuntimeException("Exception caught while storing user: " + e );
        }
        return true;
    }

    public synchronized void removeUser(String name) {
        or.remove(name);
    }

    public boolean contains(String name) {
        return or.containsKey(name);
    }

    public boolean containsCaseInsensitive(String name) {
        Iterator it = list();
        while (it.hasNext()) {
            if (name.equalsIgnoreCase((String)it.next())) {
                return true;
            }
        }
        return false;
    }

    public boolean test(String name, Object attributes) {
        try {
            return attributes.equals(or.get(name));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean test(String name, String password) {
        User user;
        try {
            if (contains(name)) {
                user = (User) or.get(name);
            } else {
               return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception retrieving User" + e);
        }
        return user.verifyPassword(password);
    }

    public int countUsers() {
        int count = 0;
        for (Iterator it = list(); it.hasNext(); it.next()) {
            count++;
        }
        return count;
    }

}
