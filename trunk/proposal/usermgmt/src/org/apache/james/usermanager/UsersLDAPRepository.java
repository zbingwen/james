/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included  with this distribution in *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.james.usermanager;

import org.apache.avalon.blocks.*;
import org.apache.avalon.*;
import org.apache.avalon.utils.*;
import java.util.*;
import java.io.*;
import javax.naming.*;
import javax.naming.directory.*;

/**
 * Implementation of a Repository to store users.
 * @version 1.0.0, 24/04/1999
 * @author  Federico Barbieri <scoobie@pop.systemy.it>
 */
public class UsersLDAPRepository  implements UsersRepository, Configurable{

   
    private ComponentManager comp;
    //private Store.ObjectRepository or;
    private Logger logger;
    private String path;
    private String name;
    private String destination;
    private String type;
    private String model;
    private DirContext ctx;
    //private org.apache.avalon.Context jamesContext;

    
    private String LDAPHost;
    private String rootNodeDN;
    private String rootURL;
    private String serverRDN;
    private String baseNodeDN;
    private String baseURL;
    private String mailAddressAttr;
    private String identAttr;
    private String authType;
    private String principal;
    private String password;
    private String usersDomain;
    private String membersAttr;
    private boolean manageGroupAttr;
    private String groupAttr;
    private boolean managePasswordAttr;
    private String passwordAttr;
 

    //public static void main (String[] args) {
    // Enumeration users;
    // boolean result;
    // int number;
    //
    //UsersLDAPRepository thisRep = new UsersLDAPRepository();
    //
    //if (args != null && args.length > 0) {
    //    if (args[0].equals("list")){
    //	users = thisRep.list();
    //	while (users.hasMoreElements()) {
    //	    System.out.println(users.nextElement());
    //	}
    //    }
    //    else if (args[0].equals("addUser")) {
    //	thisRep.addUser(args[1], args[2]);
    //    }
    //    else if (args[0].equals("removeUser")) {
    //	thisRep.removeUser(args[1]);
    //    }
    //    else if (args[0].equals("test")) {
    //	result = thisRep.test(args[1], args[2]);
    //	System.out.println("Main: test: " + result);
    //    }
    //    else if (args[0].equals("contains")) {
    //	result = thisRep.contains(args[1]);
    //	System.out.println("Main: contains: " + result);
    //    }
    //    else if (args[0].equals("count")) {
    //	number = thisRep.countUsers();
    //	System.out.println("Main: count: " + number);
    //    }
    // 
    //}
    //}

    // Methods from interface Repository ---------------------------------------------
    public void setAttributes(String name, String destination, String type, String model) {

        this.name = name;
        this.destination = destination;
        this.model = model;
        this.type = type;
    }
        
   public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public String getModel() {
        return model;
    }
    public String getChildDestination(String childName) {

	String destination = null;
	String filter = "cn=" + childName;
	SearchControls ctls = new SearchControls();

	try {

	    NamingEnumeration result  = ctx.search("", filter, ctls);
     
	    if (result.hasMore()) {
		destination = "cn=" + childName + ", " + baseNodeDN;
		logger.log("Pre-exisisting LDAP node: " + destination, "UserManager", logger.INFO);

	    } else {
		
		Attributes attrs = new BasicAttributes(true);
		Attribute objclass = new BasicAttribute("objectclass");
		objclass.add("top");
		objclass.add("rfc822MailGroup");
		attrs.put(objclass);
		Attribute cname = new BasicAttribute("cn");
		cname.add(childName);
		attrs.put(cname);
		Attribute owner = new BasicAttribute("owner");
		owner.add("JAMES-unassigned");
		attrs.put(owner);

		ctx.addToEnvironment(javax.naming.Context.SECURITY_AUTHENTICATION, authType);
		ctx.addToEnvironment(javax.naming.Context.SECURITY_PRINCIPAL, principal);
		ctx.addToEnvironment(javax.naming.Context.SECURITY_CREDENTIALS, password);

		ctx.createSubcontext("cn="+childName, attrs);
		ctx.addToEnvironment(javax.naming.Context.SECURITY_AUTHENTICATION, "none");
	
		destination = "cn=" + childName + ", " + baseNodeDN;
		logger.log("Created new LDAP node: " + destination, "UserManager", logger.INFO);
	
	    }
	} catch (NamingException e) {
	    System.out.println("Problem with child nodes " + e.getMessage());
	    e.printStackTrace();
	}

	return destination;
    }

   public Enumeration list() {
       
       Vector result = new Vector();
       String filter = mailAddressAttr + "=*";
       String[] attrIDs = {membersAttr};

	try {
	    Attribute members = ctx.getAttributes("", attrIDs).get(membersAttr);
	    if (members != null) {
		NamingEnumeration enum = members.getAll();  
		while (enum.hasMore()) {
		    result.add((String)enum.next());
		}
	    }
	} catch (NamingException e) {
	    logger.log("Problem listing mailboxes. " + e , "UserManager", logger.ERROR);
	    //System.out.println(	 "Problem listing mailboxes. "  + e.getMessage());

	}
       return result.elements();
    }

    

    // Methods from Interface Configurable, Composer  ---------------------

    //public UsersLDAPRepository() {
    //LDAPHost = "ldap://localhost:389";
    //rootNodeDN = "dc=apache, dc=org";
    //serverRDN = "cn=mailserver.apache.org";
    //mailAddressAttr = "mail";
    //identAttr = "uid";
    //authType = "simple";
    //principal = "cn=Manager, dc=benett, dc=com";
    //password = "secret";
    //usersDomain = "apache.org";
    //membersAttr = "member";
    //manageGroupAttr = true;
    //groupAttr = "memberOfGroup";
    //managePasswordAttr = false;
    //passwordAttr = "userpassword";


    public void setConfiguration(Configuration conf) {
	LDAPHost = conf.getConfiguration("LDAPServer").getValue();
	rootNodeDN = conf.getConfiguration("LDAPRoot").getValue();
	serverRDN = conf.getConfiguration("ThisServerRDN").getValue();
	mailAddressAttr = conf.getConfiguration("MailAddressAttribute").getValue();
	identAttr = conf.getConfiguration("IdentityAttribute").getValue();
	authType = conf.getConfiguration("AuthenticationType").getValue();
	principal = conf.getConfiguration("Principal").getValue();
	password = conf.getConfiguration("Password").getValue();
	usersDomain = conf.getConfiguration("UsersDomain").getValue();
	membersAttr = conf.getConfiguration("MembersAttribute").getValue();
	manageGroupAttr = conf.getConfiguration("ManageGroupAttribute").getValue().equals("TRUE");
	groupAttr = conf.getConfiguration("GroupAttribute").getValue();
	managePasswordAttr = conf.getConfiguration("ManagePasswordAttribute").getValue().equals("TRUE");
	passwordAttr = conf.getConfiguration("PasswordAttribute").getValue();
	}

    public void setComponentManager(ComponentManager comp) {
	this.comp = comp;
    }

    public void setServerRoot() {
	this.setBase(serverRDN +", " + rootNodeDN);
    }
    
    public void setBase(String base) {
	baseNodeDN = base;
    }

    public void init() throws Exception {
	logger = (Logger) comp.getComponent(Interfaces.LOGGER);
	rootURL = LDAPHost + "/" + rootNodeDN;
	baseURL = LDAPHost + "/" + baseNodeDN;
     
	logger.log("Creating initial context from " + baseURL, "UserManager", logger.INFO);
	//System.out.println("Creating initial context from " + baseURL);

	Hashtable env = new Hashtable();
	env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(javax.naming.Context.PROVIDER_URL, baseURL);

	try {
	

	    ctx = new InitialDirContext(env); // Could throw a NamingExcpetion

     
	} catch (Exception e) {
	    e.getMessage();
	    e.printStackTrace();
	}
    }
    

 

    // Methods from interface UsersRepository --------------------------

    /** Adds userName to the MemberAttribute (specified in conf.xml) of this node.
     * If ManageGroupAttribute (conf.xml) is TRUE then calls addGroupToUser.
     */
    public synchronized void addUser(String userName, Object attributes) {

	String[] attrIDs = {membersAttr};

	// First, add username to mailGroup at baseNode

	try {
	    Attribute members = ctx.getAttributes("", attrIDs).get(membersAttr);

	 
	    if (members != null && members.contains(userName)) {//user already here
		logger.log("Found " + userName + " already in mailGroup. " , "UserManager", logger.INFO);
		//System.out.println("Found " + userName + " already in mailGroup. ");
		
	    } else {
		
		
		ctx.addToEnvironment(javax.naming.Context.SECURITY_AUTHENTICATION, authType);
		ctx.addToEnvironment(javax.naming.Context.SECURITY_PRINCIPAL, principal);
		ctx.addToEnvironment(javax.naming.Context.SECURITY_CREDENTIALS, password);
		
		ModificationItem[] mods = new ModificationItem[1];
		mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute(membersAttr, userName));
		
		ctx.modifyAttributes("", mods);
		
		ctx.addToEnvironment(javax.naming.Context.SECURITY_AUTHENTICATION, "none");
		logger.log(userName + " added to mailGroup " + baseNodeDN , "UserManager", logger.INFO);
		//System.out.println(userName + " added to mailGroup " + baseNodeDN);
	    }
	} catch (NamingException e) {
	     logger.log("Problem adding user " + userName + " to: " + baseNodeDN + e, "UserManager", logger.ERROR);
	     //System.out.println("Problem adding user " + userName + " to: " + baseNodeDN);
	     //System.out.println(e.getMessage());
	     //e.printStackTrace();
	} 

	// Add attributes to user objects, if necessary

	if (manageGroupAttr) addGroupToUser(userName);

	if (managePasswordAttr) {
	    String userPassword = (String) attributes; // Not yet implemented
	}
    }

	private void addGroupToUser(String userName) {
	    String[] attrIDs = {membersAttr};

	    Hashtable env = new Hashtable();
	    env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	    env.put(javax.naming.Context.PROVIDER_URL, rootURL);

	    try {
		DirContext rootCtx = new InitialDirContext(env);
		 
		String[] returnAttrs = {groupAttr};
		SearchControls ctls = new SearchControls();
		ctls.setReturningAttributes(attrIDs);
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String filter = mailAddressAttr + "=" + userName + "@" + usersDomain;
		
		NamingEnumeration enum  = rootCtx.search("", filter, ctls);
		
		if (enum.hasMore()) { // ie User is in Directory
		    SearchResult newSr = (SearchResult)enum.next();
		    String userDN = newSr.getName();
		    Attribute servers = rootCtx.getAttributes(userDN, returnAttrs).get(groupAttr);

		
		    if (servers != null && servers.contains(baseNodeDN)) {//server already registered for user
			logger.log(baseNodeDN + " already in user's Groups. " , "UserManager", logger.INFO);
			//System.out.println(baseNodeDN + " already in user's Groups. ");
			
		    } else {		      
			
			rootCtx.addToEnvironment(javax.naming.Context.SECURITY_AUTHENTICATION, authType);
			rootCtx.addToEnvironment(javax.naming.Context.SECURITY_PRINCIPAL, principal);
			rootCtx.addToEnvironment(javax.naming.Context.SECURITY_CREDENTIALS, password);

			rootCtx.modifyAttributes(userDN, DirContext.ADD_ATTRIBUTE, new BasicAttributes(groupAttr, baseNodeDN, true));
			
			rootCtx.addToEnvironment(javax.naming.Context.SECURITY_AUTHENTICATION, "none");
			logger.log(baseNodeDN + " added to user's groups " , "UserManager", logger.INFO);
			//System.out.println(baseNodeDN + " added to users' groups ");
			
		    }
		    
		} else {
		    logger.log("User " + userName + " not in Directory.", "UserManager", logger.INFO);
		    // System.out.println("User " + userName + " not in Directory.");
		    
		}
		rootCtx.close();
		
		
		
	    } catch (NamingException e) {
		logger.log("Problem adding group to user " + userName, "UserManager" + e, logger.ERROR);
		//System.out.println("Problem adding group to user " + userName);
		//System.out.println(e.getMessage());
		//e.printStackTrace();
	    }
      
	}

 
 

    public synchronized Object getAttributes(String name) {
	return null;
    }
    

    public synchronized void removeUser(String userName) {
	
	
	String[] attrIDs = {membersAttr};

	try {
	    Attribute members = ctx.getAttributes("", attrIDs).get(membersAttr);
	    if (members == null) {
		System.out.println("UsersLDAPRepository - Null list attribute.");
		
	    } else  if (!members.contains(userName)) {//user not here
		logger.log(userName + " missing from mailGroup. " , "UserManager", logger.INFO);
		//System.out.println(userName + " missing from mailGroup. ");
		
	    } else {
		// First, remove username from mailGroup at baseNode
		
		ctx.addToEnvironment(javax.naming.Context.SECURITY_AUTHENTICATION, authType);
		ctx.addToEnvironment(javax.naming.Context.SECURITY_PRINCIPAL, principal);
		ctx.addToEnvironment(javax.naming.Context.SECURITY_CREDENTIALS, password);
		
		ModificationItem[] mods = new ModificationItem[1];
		mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(membersAttr, userName));
		
		ctx.modifyAttributes("", mods);
		
		
		ctx.addToEnvironment(javax.naming.Context.SECURITY_AUTHENTICATION, "none");
		logger.log(userName + " removed from mailGroup. " , "UserManager", logger.INFO);
		//System.out.println(userName + " removed from mailGroup. ");
	    }
	} catch (NamingException e) {
	    logger.log("Problem removing user " + userName + e, "UserManager", logger.ERROR);
	    //System.out.println("Problem removing user " + userName);
	    //System.out.println(e.getMessage());
	    //e.printStackTrace();
	}
	if (manageGroupAttr) removeGroupFromUser(userName);

	if (managePasswordAttr) {
	    // not yet implemented
	}

    }

    public void removeGroupFromUser(String userName) {
     
	Hashtable env = new Hashtable();
	env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(javax.naming.Context.PROVIDER_URL, rootURL);
	

	try {
	    DirContext rootCtx = new InitialDirContext(env);
	    
	    // Find directory entry
	    String[] returnAttrs = {groupAttr};
	    SearchControls ctls = new SearchControls();
	    ctls.setReturningAttributes(returnAttrs);
	    ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	    String filter = mailAddressAttr + "=" + userName + "@" + usersDomain;
	    
	    NamingEnumeration enum  = rootCtx.search("", filter, ctls);
	    
	    if (enum.hasMore()) { // ie User is in Directory
		SearchResult newSr = (SearchResult)enum.next();
		String userDN = newSr.getName();
		
		System.out.println("Found user entry: " + userDN);

		 Attribute servers = rootCtx.getAttributes(userDN, returnAttrs).get(groupAttr);
		 if (servers == null) { //should not happen
		     logger.log("GroupAttribute missing from user: " + userName , "UserManager", logger.INFO);
		     // System.out.println("GroupAttribute missing from user: " + userName );

		 } else if (!servers.contains(baseNodeDN)) {//server not registered for user
		     logger.log(baseNodeDN + " missing from users' Groups. " , "UserManager", logger.INFO);
		     //System.out.println(baseNodeDN + " missing from users' Groups. ");
		    
		 } else {
		   
		     rootCtx.addToEnvironment(javax.naming.Context.SECURITY_AUTHENTICATION, authType);
		     rootCtx.addToEnvironment(javax.naming.Context.SECURITY_PRINCIPAL, principal);
		     rootCtx.addToEnvironment(javax.naming.Context.SECURITY_CREDENTIALS, password);

		     ModificationItem[] mods = new ModificationItem[1];
		     mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(groupAttr, baseNodeDN));

		     rootCtx.modifyAttributes(userDN, mods);
		
		     //rootCtx.modifyAttributes(userDN, DirContext.REPLACE_ATTRIBUTE, changes);
		     
		     rootCtx.addToEnvironment(javax.naming.Context.SECURITY_AUTHENTICATION, "none");
		     logger.log(baseNodeDN + " removed from users' groups " , "UserManager", logger.INFO);
		     //System.out.println(baseNodeDN + " removed from users' groups ");
		     
		 }
		 
	    } else {
		logger.log("User " + userName + " not in Directory.", "UserManager", logger.INFO);
		//System.out.println("User " + userName + " not in Directory.");
		
	    }
	    rootCtx.close();
	    
	} catch (NamingException e) {
	    logger.log("Problem removing user " + userName + e, "UserManager", logger.ERROR);
	    //System.out.println("Problem removing user " + userName);
	    //System.out.println(e.getMessage());
	    //e.printStackTrace();
	}
	
    }

  
    public boolean contains(String name) {
	boolean found = false;
	String[] attrIDs = {membersAttr};

	try {
	    Attribute members = ctx.getAttributes("", attrIDs).get(membersAttr);
	    if (members != null && members.contains(name)) {
		found = true;
		logger.log("Found " + name + " in mailGroup. " , "UserManager", logger.INFO);
		//System.out.println("Found " + name + " in mailGroup. ");
	    }
	} catch (NamingException e) {
	    logger.log("Problem finding user " + name + e, "UserManager", logger.ERROR);
	    //System.out.println("Problem finding user " + name + " : " + e);
	}
	return found;
    }
    

    public boolean test(String name, Object attributes) {
	boolean result = false;
	boolean foundFlag = false;
	String testPassword = (String) attributes;
	String userDN = null;

        try {
	    String[] returnAttrs = {identAttr, passwordAttr};
	    SearchControls ctls = new SearchControls();
	    ctls.setReturningAttributes(returnAttrs);
	    ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	    String filter = mailAddressAttr + "=" + name + "@" + usersDomain;

	    Hashtable env = new Hashtable();
	    env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	    env.put(javax.naming.Context.PROVIDER_URL, rootURL);
	    DirContext rootCtx = new InitialDirContext(env);

	    NamingEnumeration enum  = rootCtx.search("", filter, ctls);
	    if (enum.hasMore()) { // ie User is in Directory
		SearchResult sr = (SearchResult)enum.next();
		String userRDN = sr.getName();
		userDN = userRDN +", " + rootNodeDN;
		foundFlag = true;
		System.out.println("UserDN is : " + userDN);
	    }

	    rootCtx.close();
	} catch (Exception e) {
	    logger.log("Problem finding user " + name + " for password test." +e, "UserManager", logger.ERROR);
	    //e.getMessage();
	    //e.printStackTrace();
	}

	if (foundFlag) { // ie User is in Directory
	    Hashtable env2 = new Hashtable();
	    env2.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	    env2.put(javax.naming.Context.PROVIDER_URL, rootURL);
	    env2.put(javax.naming.Context.SECURITY_AUTHENTICATION, "simple");
	    env2.put(javax.naming.Context.SECURITY_PRINCIPAL, userDN);
	    env2.put(javax.naming.Context.SECURITY_CREDENTIALS, testPassword);
	    System.out.println("Creating initial context from " + baseURL);

	    try {
		DirContext testCtx = new InitialDirContext(env2);
		result = true;
		testCtx.close();
	    
	    } catch (AuthenticationException ae) {
		result = false;
	     logger.log("Attempt to authenticate with incorrect password for " + name + " : " + ae , "UserManager", logger.ERROR);
	     //System.out.println("Attempt to authenticate with incorrect password for " + name + " : " + ae);
		//System.out.println(ae.getMessage());
		//ae.printStackTrace();
	    } catch (Exception e) {
		  logger.log("Problem checking password for " + name + " : " + e , "UserManager", logger.ERROR);
		  //System.out.println("Problem checking password for " + name + " : " + e);
		  //System.out.println(e.getMessage());
		  //e.printStackTrace();
	    }
	}
	return result;
	
    }

    public int countUsers() {
     
       String[] attrIDs = {membersAttr};
       int result = -1;

	try {
	    Attribute members = ctx.getAttributes("", attrIDs).get(membersAttr);
	    if (members != null) {
		result = members.size();
	    } else {
		result = 0;
	    }
	
	   
	} catch (NamingException e) {
	    logger.log("Problem counting users: "  + e, "UserManager", logger.INFO);
	    //System.out.println("Problem counting users. ");
	 
	}
       return result;
    }

    public String getDomains() {
	return usersDomain;
    }

}

    
