/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/



package org.apache.james.smtpserver.core.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.james.smtpserver.CommandHandler;
import org.apache.james.smtpserver.SMTPResponse;
import org.apache.james.smtpserver.SMTPSession;
import org.apache.james.util.mail.dsn.DSNStatus;
import org.apache.mailet.MailAddress;

/**
  * Handles MAIL command
  */
public class MailFilterCmdHandler
    extends AbstractLogEnabled
    implements CommandHandler {

    private final static String MAIL_OPTION_SIZE = "SIZE";

    private final static String MESG_SIZE = "MESG_SIZE"; // The size of the message

    /**
     * handles MAIL command
     *
     * @see org.apache.james.smtpserver.CommandHandler#onCommand(org.apache.james.smtpserver.SMTPSession, java.lang.String, java.lang.String) 
     */
    public SMTPResponse onCommand(SMTPSession session, String command, String arguments) {
        return doMAIL(session, arguments);
    }


    /**
     * @param session SMTP session object
     * @param argument the argument passed in with the command by the SMTP client
     */
    private SMTPResponse doMAIL(SMTPSession session, String argument) {
        String sender = null;
        
        if ((argument != null) && (argument.indexOf(":") > 0)) {
            int colonIndex = argument.indexOf(":");
            sender = argument.substring(colonIndex + 1);
            argument = argument.substring(0, colonIndex);
        }
        if (session.getState().containsKey(SMTPSession.SENDER)) {
            return new SMTPResponse("503", DSNStatus.getStatus(DSNStatus.PERMANENT,DSNStatus.DELIVERY_OTHER)+" Sender already specified");
        } else if (!session.getConnectionState().containsKey(SMTPSession.CURRENT_HELO_MODE) && session.useHeloEhloEnforcement()) {
            return new SMTPResponse("503", DSNStatus.getStatus(DSNStatus.PERMANENT,DSNStatus.DELIVERY_OTHER)+" Need HELO or EHLO before MAIL");
        } else if (argument == null || !argument.toUpperCase(Locale.US).equals("FROM")
                   || sender == null) {
            return new SMTPResponse("501", DSNStatus.getStatus(DSNStatus.PERMANENT,DSNStatus.DELIVERY_INVALID_ARG)+" Usage: MAIL FROM:<sender>");
        } else {
            sender = sender.trim();
            // the next gt after the first lt ... AUTH may add more <>
            int lastChar = sender.indexOf('>', sender.indexOf('<'));
            // Check to see if any options are present and, if so, whether they are correctly formatted
            // (separated from the closing angle bracket by a ' ').
            if ((lastChar > 0) && (sender.length() > lastChar + 2) && (sender.charAt(lastChar + 1) == ' ')) {
                String mailOptionString = sender.substring(lastChar + 2);

                // Remove the options from the sender
                sender = sender.substring(0, lastChar + 1);

                StringTokenizer optionTokenizer = new StringTokenizer(mailOptionString, " ");
                while (optionTokenizer.hasMoreElements()) {
                    String mailOption = optionTokenizer.nextToken();
                    int equalIndex = mailOption.indexOf('=');
                    String mailOptionName = mailOption;
                    String mailOptionValue = "";
                    if (equalIndex > 0) {
                        mailOptionName = mailOption.substring(0, equalIndex).toUpperCase(Locale.US);
                        mailOptionValue = mailOption.substring(equalIndex + 1);
                    }

                    // Handle the SIZE extension keyword

                    if (mailOptionName.startsWith(MAIL_OPTION_SIZE)) {
                        SMTPResponse res = doMailSize(session, mailOptionValue, sender);
                        if (res != null) {
                            return res;
                        }
                    } else {
                        // Unexpected option attached to the Mail command
                        if (getLogger().isDebugEnabled()) {
                            StringBuffer debugBuffer =
                                new StringBuffer(128)
                                    .append("MAIL command had unrecognized/unexpected option ")
                                    .append(mailOptionName)
                                    .append(" with value ")
                                    .append(mailOptionValue);
                            getLogger().debug(debugBuffer.toString());
                        }
                    }
                }
            }
            if ( session.getConfigurationData().useAddressBracketsEnforcement() && (!sender.startsWith("<") || !sender.endsWith(">"))) {
                if (getLogger().isErrorEnabled()) {
                    StringBuffer errorBuffer =
                        new StringBuffer(128)
                            .append("Error parsing sender address: ")
                            .append(sender)
                            .append(": did not start and end with < >");
                    getLogger().error(errorBuffer.toString());
                }
                return new SMTPResponse("501", DSNStatus.getStatus(DSNStatus.PERMANENT,DSNStatus.ADDRESS_SYNTAX_SENDER)+" Syntax error in MAIL command");
            }
            MailAddress senderAddress = null;
            
            if (session.getConfigurationData().useAddressBracketsEnforcement() || (sender.startsWith("<") && sender.endsWith(">"))) {
                //Remove < and >
                sender = sender.substring(1, sender.length() - 1);
            }
            
            if (sender.length() == 0) {
                //This is the <> case.  Let senderAddress == null
            } else {
                 
                if (sender.indexOf("@") < 0) {
                    sender = sender + "@" + session.getConfigurationData().getMailServer().getDefaultDomain();
                }
                
                try {
                    senderAddress = new MailAddress(sender);
                } catch (Exception pe) {
                    if (getLogger().isErrorEnabled()) {
                        StringBuffer errorBuffer =
                            new StringBuffer(256)
                                    .append("Error parsing sender address: ")
                                    .append(sender)
                                    .append(": ")
                                    .append(pe.getMessage());
                        getLogger().error(errorBuffer.toString());
                    }
                    return new SMTPResponse("501", DSNStatus.getStatus(DSNStatus.PERMANENT,DSNStatus.ADDRESS_SYNTAX_SENDER)+" Syntax error in sender address");
                }
            }
         
            // Store the senderAddress in session map
            session.getState().put(SMTPSession.SENDER, senderAddress);
        }
        return null;
    }

    /**
     * Handles the SIZE MAIL option.
     *
     * @param session SMTP session object
     * @param mailOptionValue the option string passed in with the SIZE option
     * @param tempSender the sender specified in this mail command (for logging purpose)
     * @return true if further options should be processed, false otherwise
     */
    private SMTPResponse doMailSize(SMTPSession session, String mailOptionValue, String tempSender) {
        int size = 0;
        try {
            size = Integer.parseInt(mailOptionValue);
        } catch (NumberFormatException pe) {
            getLogger().error("Rejected syntactically incorrect value for SIZE parameter.");
            // This is a malformed option value.  We return an error
            return new SMTPResponse("501", DSNStatus.getStatus(DSNStatus.PERMANENT,DSNStatus.DELIVERY_INVALID_ARG)+" Syntactically incorrect value for SIZE parameter");
        }
        if (getLogger().isDebugEnabled()) {
            StringBuffer debugBuffer =
                new StringBuffer(128)
                    .append("MAIL command option SIZE received with value ")
                    .append(size)
                    .append(".");
                    getLogger().debug(debugBuffer.toString());
        }
        long maxMessageSize = session.getConfigurationData().getMaxMessageSize();
        if ((maxMessageSize > 0) && (size > maxMessageSize)) {
            // Let the client know that the size limit has been hit.
            StringBuffer errorBuffer =
                new StringBuffer(256)
                    .append("Rejected message from ")
                    .append(tempSender != null ? tempSender : null)
                    .append(" from host ")
                    .append(session.getRemoteHost())
                    .append(" (")
                    .append(session.getRemoteIPAddress())
                    .append(") of size ")
                    .append(size)
                    .append(" exceeding system maximum message size of ")
                    .append(maxMessageSize)
                    .append("based on SIZE option.");
            getLogger().error(errorBuffer.toString());
            
            return new SMTPResponse("552", DSNStatus.getStatus(DSNStatus.PERMANENT,DSNStatus.SYSTEM_MSG_TOO_BIG)+" Message size exceeds fixed maximum message size");
        } else {
            // put the message size in the message state so it can be used
            // later to restrict messages for user quotas, etc.
            session.getState().put(MESG_SIZE, new Integer(size));
        }
        return null;
    }
    
    /**
     * @see org.apache.james.smtpserver.CommandHandler#getImplCommands()
     */
    public Collection getImplCommands() {
        Collection implCommands = new ArrayList();
        implCommands.add("MAIL");
        
        return implCommands;
    }

}
