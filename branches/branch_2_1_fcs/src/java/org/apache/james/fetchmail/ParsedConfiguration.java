/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache", "Jakarta", "JAMES" and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * Portions of this software are based upon public domain software
 * originally written at the National Center for Supercomputing Applications,
 * University of Illinois, Urbana-Champaign.
 */
 
package org.apache.james.fetchmail;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.mail.internet.ParseException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.james.services.MailServer;
import org.apache.mailet.MailAddress;
import org.apache.james.services.UsersRepository;

/**
 * <p>Parses and validates an 
 * <code>org.apache.avalon.framework.configuration.Configuration</code>.</p>
 * 
 * <p>Creation Date: 27-May-03</p>
 * 
 */
class ParsedConfiguration
{
    /**
     * The logger.
     */
    private Logger fieldLogger;
    
    /**
     * The name of the folder to fetch from the javamail provider
     *
     */
    private String fieldJavaMailFolderName = "INBOX";    
    

    /**
     * The name of the javamail provider we want to user (pop3,imap,nntp,etc...)
     *
     */
    private String fieldJavaMailProviderName = "pop3";


    /**
     * Returns the javaMailFolderName.
     * @return String
     */
    public String getJavaMailFolderName()
    {
        return fieldJavaMailFolderName;
    }


    /**
     * Returns the javaMailProviderName.
     * @return String
     */
    public String getJavaMailProviderName()
    {
        return fieldJavaMailProviderName;
    }


    /**
     * Sets the javaMailFolderName.
     * @param javaMailFolderName The javaMailFolderName to set
     */
    protected void setJavaMailFolderName(String javaMailFolderName)
    {
        fieldJavaMailFolderName = javaMailFolderName;
    }


    /**
     * Sets the javaMailProviderName.
     * @param javaMailProviderName The javaMailProviderName to set
     */
    protected void setJavaMailProviderName(String javaMailProviderName)
    {
        fieldJavaMailProviderName = javaMailProviderName;
    }


    
    
    /**
     * Fetch both old (seen) and new messages from the mailserver.  The default
     * is to fetch only messages the server are not marked as seen.
     */
    private boolean fieldFetchAll = false;


    /**
     * The unique, identifying name for this task
     */
    private String fieldFetchTaskName;
    

    /**
     * The server host name for this fetch task
     */
    private String fieldHost;

    /**
     * Keep retrieved messages on the remote mailserver.  Normally, messages
     * are deleted from the folder on the mailserver after they have been retrieved
     */
    private boolean fieldLeave = false;
    
    /**
     * Keep blacklisted messages on the remote mailserver.  Normally, messages
     * are kept in the folder on the mailserver if they have been rejected
     */
    private boolean fieldLeaveBlacklisted = true;
    
    /**
     * Keep messages for remote recipients on the remote mailserver.  Normally,
     * messages are kept in the folder on the mailserver if they have been 
     * rejected.
     */
    private boolean fieldLeaveRemoteRecipient = true;    
    
    /**
     * Keep messages for undefined users on the remote mailserver.  Normally, 
     * messages are kept in the folder on the mailserver if they have been 
     * rejected.
     */
    private boolean fieldLeaveUserUndefined = true;
    
    /**
     * Keep undeliverable messages on the remote mailserver.  Normally, 
     * messages are kept in the folder on the mailserver if they cannot 
     * be delivered.
     */
    private boolean fieldLeaveUndeliverable = true;            
    

    /**
     * Mark retrieved messages on the remote mailserver as seen.  Normally, 
     * messages are marked as seen after they have been retrieved
     */
    private boolean fieldMarkSeen = true;
    
    /**
     * Mark blacklisted messages on the remote mailserver as seen.  Normally, 
     * messages are not marked as seen if they have been rejected
     */
    private boolean fieldMarkBlacklistedSeen = false;
    
    /**
     * Mark remote recipient messages on the remote mailserver as seen. Normally, 
     * messages are not marked as seen if they have been rejected
     */
    private boolean fieldMarkRemoteRecipientSeen = false;    
    
    /**
     * Mark messages for undefined users on the remote mail server as seen.
     * Normally, messages are not marked as seen if they have been rejected.
     */
    private boolean fieldMarkUserUndefinedSeen = false;
    
    /**
     * Mark undeliverable messages on the remote mail server as seen.
     * Normally, messages are not marked as seen if they are undeliverable.
     */
    private boolean fieldMarkUndeliverableSeen = false; 
    
    /**
     * Defer processing of messages for which the intended recipient cannot
     * be determined to the next pass.
     */
    private boolean fieldDeferRecipientNotFound = false;                 


    /**
     * Recurse folders if available?
     */
    private boolean fieldRecurse = false;
    

    /**
     * The MailServer service
     */
    private MailServer fieldServer;    
    

    /**
     * The domain part to use to complete partial addresses
     */
    private String fieldDefaultDomainName; 
    
    /**
     * Only accept mail for defined recipients.
     * All other mail is rejected.
     */
    private boolean fieldRejectUserUndefined;
    
    /**
     * Reject messages for which a recipient could not be determined.
     */
    private boolean fieldRejectRecipientNotFound;
    
    /**
     * Leave messages on the server for which a recipient could not be
     * determined.
     */
    private boolean fieldLeaveRecipientNotFound; 
    
    /**
     * Mark as seen messages on the server for which a recipient could not be
     * determined.
     */
    private boolean fieldMarkRecipientNotFoundSeen;       
    
    /**
     * Reject mail for blacklisted users
     */
    private boolean fieldRejectBlacklisted;

    /**
     * Only accept mail for local recipients.
     * All other mail is rejected.
     */
    private boolean fieldRejectRemoteRecipient;    

    /**
     * The Set of MailAddresses for whom mail should be rejected
     */    
    private Set fieldBlacklist;   

   /**
     * The Local Users repository
     */
    private UsersRepository fieldLocalUsers;    



    /**
     * Constructor for ParsedConfiguration.
     */
    private ParsedConfiguration()
    {
        super();
    }
    
    /**
     * Constructor for ParsedConfiguration.
     * @param configuration
     * @param logger
     * @param server
     * @param localUsers
     * @throws ConfigurationException
     */
    public ParsedConfiguration(Configuration configuration, Logger logger, MailServer server, UsersRepository localUsers) throws ConfigurationException
    {
        this();
        setLogger(logger);
        setServer(server);
        setLocalUsers(localUsers);      
        configure(configuration);
    }
    
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
     */
    protected void configure(Configuration conf) throws ConfigurationException
    {   
        setHost(conf.getChild("host").getValue());
//      setUser(conf.getChild("user").getValue());
//      setPassword(conf.getChild("password").getValue());

        setFetchTaskName(conf.getAttribute("name"));
        setJavaMailProviderName(
            conf.getChild("javaMailProviderName").getValue());
        setJavaMailFolderName(conf.getChild("javaMailFolderName").getValue());
        setRecurse(conf.getChild("recursesubfolders").getValueAsBoolean());

//      Configuration recipient = conf.getChild("recipient");
//      setRecipient(recipient.getValue());
//      setIgnoreOriginalRecipient(
//          recipient.getAttributeAsBoolean("ignorercpt-header"));
            
        Configuration recipientNotFound = conf.getChild("recipientnotfound");
        setDeferRecipientNotFound(
            recipientNotFound.getAttributeAsBoolean("defer"));         
        setRejectRecipientNotFound(
            recipientNotFound.getAttributeAsBoolean("reject"));     
        setLeaveRecipientNotFound(recipientNotFound.getAttributeAsBoolean("leaveonserver"));
        setMarkRecipientNotFoundSeen(recipientNotFound.getAttributeAsBoolean("markseen"));          

        Configuration defaultDomainName = conf.getChild("defaultdomain", false);
        if (null != defaultDomainName)
            setDefaultDomainName(defaultDomainName.getValue());

        setFetchAll(conf.getChild("fetchall").getValueAsBoolean());

        Configuration fetched = conf.getChild("fetched");
        setLeave(fetched.getAttributeAsBoolean("leaveonserver"));
        setMarkSeen(fetched.getAttributeAsBoolean("markseen"));

        Configuration remoterecipient = conf.getChild("remoterecipient");
        setRejectRemoteRecipient(
            remoterecipient.getAttributeAsBoolean("reject"));
        setLeaveRemoteRecipient(
            remoterecipient.getAttributeAsBoolean("leaveonserver"));
        setMarkRemoteRecipientSeen(
            remoterecipient.getAttributeAsBoolean("markseen"));

        Configuration blacklist = conf.getChild("blacklist");
        setBlacklist(blacklist.getValue(""));
        setRejectBlacklisted(
            blacklist.getAttributeAsBoolean("reject"));     
        setLeaveBlacklisted(blacklist.getAttributeAsBoolean("leaveonserver"));
        setMarkBlacklistedSeen(blacklist.getAttributeAsBoolean("markseen"));

        Configuration userundefined = conf.getChild("userundefined");
        setRejectUserUndefined(userundefined.getAttributeAsBoolean("reject"));
        setLeaveUserUndefined(
            userundefined.getAttributeAsBoolean("leaveonserver"));
        setMarkUserUndefinedSeen(
            userundefined.getAttributeAsBoolean("markseen"));
            
        Configuration undeliverable = conf.getChild("undeliverable");      
        setLeaveUndeliverable(
            undeliverable.getAttributeAsBoolean("leaveonserver"));
        setMarkUndeliverableSeen(
            undeliverable.getAttributeAsBoolean("markseen"));                       

        if (getLogger().isDebugEnabled())
        {
            getLogger().info(
                "Configured FetchMail fetch task " + getFetchTaskName());
        }
    }


        
    
    /**
     * Returns the fetchAll.
     * @return boolean
     */
    public boolean isFetchAll()
    {
        return fieldFetchAll;
    }

    /**
     * Returns the fetchTaskName.
     * @return String
     */
    public String getFetchTaskName()
    {
        return fieldFetchTaskName;
    }

    /**
     * Returns the host.
     * @return String
     */
    public String getHost()
    {
        return fieldHost;
    }

    /**
     * Returns the keep.
     * @return boolean
     */
    public boolean isLeave()
    {
        return fieldLeave;
    }

    /**
     * Returns the markSeen.
     * @return boolean
     */
    public boolean isMarkSeen()
    {
        return fieldMarkSeen;
    }
    
    /**
     * Answers true if the folder should be opened read only.
     * For this to be true the configuration options must not require
     * folder updates.
     * 
     * @return boolean
     */
    protected boolean isOpenReadOnly()
    {
        return isLeave()
            && !isMarkSeen()
            && isLeaveBlacklisted()
            && !isMarkBlacklistedSeen()
            && isLeaveRemoteRecipient()
            && !isMarkRemoteRecipientSeen()
            && isLeaveUserUndefined()
            && !isMarkUserUndefinedSeen()
            && isLeaveUndeliverable()
            && !isMarkUndeliverableSeen()                                   
            ;
    }   

    /**
     * Returns the recurse.
     * @return boolean
     */
    public boolean isRecurse()
    {
        return fieldRecurse;
    }

    /**
     * Returns the server.
     * @return MailServer
     */
    public MailServer getServer()
    {
        return fieldServer;
    }

    /**
     * Sets the fetchAll.
     * @param fetchAll The fetchAll to set
     */
    protected void setFetchAll(boolean fetchAll)
    {
        fieldFetchAll = fetchAll;
    }

    /**
     * Sets the fetchTaskName.
     * @param fetchTaskName The fetchTaskName to set
     */
    protected void setFetchTaskName(String fetchTaskName)
    {
        fieldFetchTaskName = fetchTaskName;
    }

    /**
     * Sets the host.
     * @param host The host to set
     */
    protected void setHost(String host)
    {
        fieldHost = host;
    }

    /**
     * Sets the keep.
     * @param keep The keep to set
     */
    protected void setLeave(boolean keep)
    {
        fieldLeave = keep;
    }

    /**
     * Sets the markSeen.
     * @param markSeen The markSeen to set
     */
    protected void setMarkSeen(boolean markSeen)
    {
        fieldMarkSeen = markSeen;
    }

    /**
     * Sets the recurse.
     * @param recurse The recurse to set
     */
    protected void setRecurse(boolean recurse)
    {
        fieldRecurse = recurse;
    }

    /**
     * Sets the server.
     * @param server The server to set
     */
    protected void setServer(MailServer server)
    {
        fieldServer = server;
    }

    /**
     * Returns the logger.
     * @return Logger
     */
    public Logger getLogger()
    {
        return fieldLogger;
    }

    /**
     * Sets the logger.
     * @param logger The logger to set
     */
    protected void setLogger(Logger logger)
    {
        fieldLogger = logger;
    }

/**
 * Returns the localUsers.
 * @return UsersRepository
 */
public UsersRepository getLocalUsers()
{
    return fieldLocalUsers;
}

/**
 * Sets the localUsers.
 * @param localUsers The localUsers to set
 */
protected void setLocalUsers(UsersRepository localUsers)
{
    fieldLocalUsers = localUsers;
}

    /**
     * Returns the keepRejected.
     * @return boolean
     */
    public boolean isLeaveBlacklisted()
    {
        return fieldLeaveBlacklisted;
    }

    /**
     * Returns the markRejectedSeen.
     * @return boolean
     */
    public boolean isMarkBlacklistedSeen()
    {
        return fieldMarkBlacklistedSeen;
    }

    /**
     * Sets the keepRejected.
     * @param keepRejected The keepRejected to set
     */
    protected void setLeaveBlacklisted(boolean keepRejected)
    {
        fieldLeaveBlacklisted = keepRejected;
    }

    /**
     * Sets the markRejectedSeen.
     * @param markRejectedSeen The markRejectedSeen to set
     */
    protected void setMarkBlacklistedSeen(boolean markRejectedSeen)
    {
        fieldMarkBlacklistedSeen = markRejectedSeen;
    }

    /**
     * Returns the blacklist.
     * @return Set
     */
    public Set getBlacklist()
    {
        return fieldBlacklist;
    }

    /**
     * Sets the blacklist.
     * @param blacklist The blacklist to set
     */
    protected void setBlacklist(Set blacklist)
    {
        fieldBlacklist = blacklist;
    }
    
    /**
     * Sets the blacklist.
     * @param blacklist The blacklist to set
     */
    protected void setBlacklist(String blacklistValue)
        throws ConfigurationException
    {
        StringTokenizer st = new StringTokenizer(blacklistValue, ", \t", false);
        Set blacklist = new HashSet();
        String token = null;
        while (st.hasMoreTokens())
        {
            try
            {
                token = st.nextToken();
                blacklist.add(new MailAddress(token));
            }
            catch (ParseException pe)
            {
                throw new ConfigurationException(
                    "Invalid blacklist mail address specified: " + token);
            }
        }
        setBlacklist(blacklist);
    }   

    /**
     * Returns the localRecipientsOnly.
     * @return boolean
     */
    public boolean isRejectUserUndefined()
    {
        return fieldRejectUserUndefined;
    }

    /**
     * Sets the localRecipientsOnly.
     * @param localRecipientsOnly The localRecipientsOnly to set
     */
    protected void setRejectUserUndefined(boolean localRecipientsOnly)
    {
        fieldRejectUserUndefined = localRecipientsOnly;
    }

    /**
     * Returns the markExternalSeen.
     * @return boolean
     */
    public boolean isMarkUserUndefinedSeen()
    {
        return fieldMarkUserUndefinedSeen;
    }

    /**
     * Sets the markExternalSeen.
     * @param markExternalSeen The markExternalSeen to set
     */
    protected void setMarkUserUndefinedSeen(boolean markExternalSeen)
    {
        fieldMarkUserUndefinedSeen = markExternalSeen;
    }

    /**
     * Returns the leaveExternal.
     * @return boolean
     */
    public boolean isLeaveUserUndefined()
    {
        return fieldLeaveUserUndefined;
    }

    /**
     * Sets the leaveExternal.
     * @param leaveExternal The leaveExternal to set
     */
    protected void setLeaveUserUndefined(boolean leaveExternal)
    {
        fieldLeaveUserUndefined = leaveExternal;
    }

    /**
     * Returns the leaveRemoteRecipient.
     * @return boolean
     */
    public boolean isLeaveRemoteRecipient()
    {
        return fieldLeaveRemoteRecipient;
    }

    /**
     * Returns the markRemoteRecipientSeen.
     * @return boolean
     */
    public boolean isMarkRemoteRecipientSeen()
    {
        return fieldMarkRemoteRecipientSeen;
    }

    /**
     * Sets the leaveRemoteRecipient.
     * @param leaveRemoteRecipient The leaveRemoteRecipient to set
     */
    protected void setLeaveRemoteRecipient(boolean leaveRemoteRecipient)
    {
        fieldLeaveRemoteRecipient = leaveRemoteRecipient;
    }

    /**
     * Sets the markRemoteRecipientSeen.
     * @param markRemoteRecipientSeen The markRemoteRecipientSeen to set
     */
    protected void setMarkRemoteRecipientSeen(boolean markRemoteRecipientSeen)
    {
        fieldMarkRemoteRecipientSeen = markRemoteRecipientSeen;
    }

    /**
     * Returns the rejectRemoteRecipient.
     * @return boolean
     */
    public boolean isRejectRemoteRecipient()
    {
        return fieldRejectRemoteRecipient;
    }

    /**
     * Sets the rejectRemoteRecipient.
     * @param rejectRemoteRecipient The rejectRemoteRecipient to set
     */
    protected void setRejectRemoteRecipient(boolean rejectRemoteRecipient)
    {
        fieldRejectRemoteRecipient = rejectRemoteRecipient;
    }

    /**
     * Returns the defaultDomainName. Lazy initializes if required.
     * @return String
     */
    public String getDefaultDomainName()
    {
        String defaultDomainName = null;
        if (null == (defaultDomainName = getDefaultDomainNameBasic()))
        {
            updateDefaultDomainName();
            return getDefaultDomainName();
        }   
        return defaultDomainName;
    }
    
    /**
     * Returns the defaultDomainName.
     * @return String
     */
    private String getDefaultDomainNameBasic()
    {
        return fieldDefaultDomainName;
    }   

    /**
     * Validates and sets the defaultDomainName.
     * @param defaultDomainName The defaultDomainName to set
     */
    protected void setDefaultDomainName(String defaultDomainName) throws ConfigurationException
    {
        validateDefaultDomainName(defaultDomainName);
        setDefaultDomainNameBasic(defaultDomainName);
    }

    /**
     * Sets the defaultDomainName.
     * @param defaultDomainName The defaultDomainName to set
     */
    private void setDefaultDomainNameBasic(String defaultDomainName)
    {
        fieldDefaultDomainName = defaultDomainName;
    }

    /**
     * Validates the defaultDomainName.
     * @param defaultDomainName The defaultDomainName to validate
     */
    protected void validateDefaultDomainName(String defaultDomainName) throws ConfigurationException
    {
        if (!getServer().isLocalServer(defaultDomainName))
        {
            throw new ConfigurationException(
                "Default domain name is not a local server: "
                    + defaultDomainName);
        }
    }
    
    /**
     * Computes the defaultDomainName.
     */
    protected String computeDefaultDomainName()
    {
        String hostName = null;
        try
        {
            // These shenanigans are required to get the fully qualified
            // hostname prior to JDK 1.4 in which get getCanonicalHostName()
            // does the job for us
            InetAddress addr1 = java.net.InetAddress.getLocalHost();
            InetAddress addr2 = addr1.getByName(addr1.getHostAddress());
            hostName = addr2.getHostName();
        }
        catch (UnknownHostException ue)
        {
            hostName = "localhost";
        }
        return hostName;
    }   
    
    /**
     * Updates the defaultDomainName.
     */
    protected void updateDefaultDomainName()
    {
        setDefaultDomainNameBasic(computeDefaultDomainName());
    }   

    /**
     * Returns the leaveUndeliverable.
     * @return boolean
     */
    public boolean isLeaveUndeliverable()
    {
        return fieldLeaveUndeliverable;
    }

    /**
     * Returns the markUndeliverableSeen.
     * @return boolean
     */
    public boolean isMarkUndeliverableSeen()
    {
        return fieldMarkUndeliverableSeen;
    }

    /**
     * Sets the leaveUndeliverable.
     * @param leaveUndeliverable The leaveUndeliverable to set
     */
    protected void setLeaveUndeliverable(boolean leaveUndeliverable)
    {
        fieldLeaveUndeliverable = leaveUndeliverable;
    }

    /**
     * Sets the markUndeliverableSeen.
     * @param markUndeliverableSeen The markUndeliverableSeen to set
     */
    protected void setMarkUndeliverableSeen(boolean markUndeliverableSeen)
    {
        fieldMarkUndeliverableSeen = markUndeliverableSeen;
    }

    /**
     * Returns the rejectBlacklisted.
     * @return boolean
     */
    public boolean isRejectBlacklisted()
    {
        return fieldRejectBlacklisted;
    }

    /**
     * Sets the rejectBlacklisted.
     * @param rejectBlacklisted The rejectBlacklisted to set
     */
    protected void setRejectBlacklisted(boolean rejectBlacklisted)
    {
        fieldRejectBlacklisted = rejectBlacklisted;
    }

    /**
     * Returns the leaveRecipientNotFound.
     * @return boolean
     */
    public boolean isLeaveRecipientNotFound()
    {
        return fieldLeaveRecipientNotFound;
    }

    /**
     * Returns the markRecipientNotFoundSeen.
     * @return boolean
     */
    public boolean isMarkRecipientNotFoundSeen()
    {
        return fieldMarkRecipientNotFoundSeen;
    }

    /**
     * Returns the rejectRecipientNotFound.
     * @return boolean
     */
    public boolean isRejectRecipientNotFound()
    {
        return fieldRejectRecipientNotFound;
    }

    /**
     * Sets the leaveRecipientNotFound.
     * @param leaveRecipientNotFound The leaveRecipientNotFound to set
     */
    protected void setLeaveRecipientNotFound(boolean leaveRecipientNotFound)
    {
        fieldLeaveRecipientNotFound = leaveRecipientNotFound;
    }

    /**
     * Sets the markRecipientNotFoundSeen.
     * @param markRecipientNotFoundSeen The markRecipientNotFoundSeen to set
     */
    protected void setMarkRecipientNotFoundSeen(boolean markRecipientNotFoundSeen)
    {
        fieldMarkRecipientNotFoundSeen = markRecipientNotFoundSeen;
    }

    /**
     * Sets the rejectRecipientNotFound.
     * @param rejectRecipientNotFound The rejectRecipientNotFound to set
     */
    protected void setRejectRecipientNotFound(boolean rejectRecipientNotFound)
    {
        fieldRejectRecipientNotFound = rejectRecipientNotFound;
    }

    /**
     * Returns the deferRecipientNotFound.
     * @return boolean
     */
    public boolean isDeferRecipientNotFound()
    {
        return fieldDeferRecipientNotFound;
    }

    /**
     * Sets the deferRecipientNotFound.
     * @param deferRecipientNotFound The deferRecepientNotFound to set
     */
    protected void setDeferRecipientNotFound(boolean deferRecipientNotFound)
    {
        fieldDeferRecipientNotFound = deferRecipientNotFound;
    }

}
