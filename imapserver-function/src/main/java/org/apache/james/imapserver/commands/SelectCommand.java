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

package org.apache.james.imapserver.commands;

import org.apache.james.imapserver.ImapRequestLineReader;
import org.apache.james.imapserver.ImapResponse;
import org.apache.james.imapserver.ImapSession;
import org.apache.james.imapserver.ProtocolException;
import org.apache.james.imapserver.SelectedMailboxSession;
import org.apache.james.imapserver.store.MailboxException;
import org.apache.james.mailboxmanager.MailboxManagerException;
import org.apache.james.mailboxmanager.MessageResult;
import org.apache.james.mailboxmanager.impl.FetchGroupImpl;
import org.apache.james.mailboxmanager.impl.GeneralMessageSetImpl;
import org.apache.james.mailboxmanager.mailbox.ImapMailbox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Handles processeing for the SELECT imap command.
 *
 * @version $Revision: 109034 $
 */
class SelectCommand extends AuthenticatedStateCommand
{
    public static final String NAME = "SELECT";
    public static final String ARGS = "mailbox";

    /** @see org.apache.james.imapserver.commands.CommandTemplate#doProcess */
    protected void doProcess( ImapRequestLineReader request,
                              ImapResponse response,
                              ImapSession session )
            throws ProtocolException, MailboxException
    {
        String mailboxName = parser.mailbox( request );
        parser.endLine( request );

        session.deselect();

        final boolean isExamine = ( this instanceof ExamineCommand );

        try {
            mailboxName=session.buildFullName(mailboxName);
            selectMailbox(mailboxName, session, isExamine);
            final SelectedMailboxSession selected = session.getSelected();
            ImapMailbox mailbox = selected.getMailbox();
            response.flagsResponse(mailbox.getPermanentFlags());
            final boolean resetRecent = !isExamine;
            response.recentResponse(mailbox.recent(resetRecent, session.getMailboxSession()).length);
            response
                    .okResponse("UIDVALIDITY " + mailbox.getUidValidity(session.getMailboxSession()), null);

            MessageResult firstUnseen = mailbox.getFirstUnseen(FetchGroupImpl.MINIMAL, session.getMailboxSession());
                   
            response.existsResponse(mailbox.getMessageCount(session.getMailboxSession()));

            if (firstUnseen != null) {
                final int msn = selected.msn(firstUnseen.getUid());
                response.okResponse("UNSEEN " + msn, "Message "
                        + msn + " is the first unseen");
            } else {
                response.okResponse(null, "No messages unseen");
            }
            response.permanentFlagsResponse(mailbox.getPermanentFlags());

            if (!mailbox.isWriteable()) {
                response.commandComplete(this, "READ-ONLY");
            } else {
                response.commandComplete(this, "READ-WRITE");
            }
        } catch (MailboxManagerException e) {
            throw new MailboxException(e);
        }
    }

    private boolean selectMailbox(String mailboxName, ImapSession session, boolean readOnly) throws MailboxException, MailboxManagerException {
        ImapMailbox mailbox = session.getMailboxManager().getImapMailbox(mailboxName, false);
        final Iterator it = mailbox.getMessages(GeneralMessageSetImpl
                .all(), FetchGroupImpl.MINIMAL, session.getMailboxSession());
        final List uids = new ArrayList();
        while(it.hasNext()) {
            final MessageResult result = (MessageResult) it.next();
            uids.add(new Long(result.getUid()));
        }
        session.setSelected( mailbox, readOnly, uids );
        return readOnly;
    }

    /** @see ImapCommand#getName */
    public String getName()
    {
        return NAME;
    }

    /** @see CommandTemplate#getArgSyntax */
    public String getArgSyntax()
    {
        return ARGS;
    }
}

/*
6.3.1.  SELECT Command

   Arguments:  mailbox name

   Responses:  REQUIRED untagged responses: FLAGS, EXISTS, RECENT
               OPTIONAL OK untagged responses: UNSEEN, PERMANENTFLAGS

   Result:     OK - select completed, now in selected state
               NO - select failure, now in authenticated state: no
                    such mailbox, can't access mailbox
               BAD - command unknown or arguments invalid

   The SELECT command selects a mailbox so that messages in the
   mailbox can be accessed.  Before returning an OK to the client,
   the server MUST send the following untagged data to the client:

      FLAGS       Defined flags in the mailbox.  See the description
                  of the FLAGS response for more detail.

      <n> EXISTS  The number of messages in the mailbox.  See the
                  description of the EXISTS response for more detail.

      <n> RECENT  The number of messages with the \Recent flag set.
                  See the description of the RECENT response for more
                  detail.

      OK [UIDVALIDITY <n>]
                  The unique identifier validity value.  See the
                  description of the UID command for more detail.

   to define the initial state of the mailbox at the client.

   The server SHOULD also send an UNSEEN response code in an OK
   untagged response, indicating the message sequence number of the
   first unseen message in the mailbox.

   If the client can not change the permanent state of one or more of
   the flags listed in the FLAGS untagged response, the server SHOULD
   send a PERMANENTFLAGS response code in an OK untagged response,
   listing the flags that the client can change permanently.

   Only one mailbox can be selected at a time in a connection;
   simultaneous access to multiple mailboxes requires multiple
   connections.  The SELECT command automatically deselects any
   currently selected mailbox before attempting the new selection.
   Consequently, if a mailbox is selected and a SELECT command that
   fails is attempted, no mailbox is selected.




Crispin                     Standards Track                    [Page 23]

RFC 2060                       IMAP4rev1                   December 1996


   If the client is permitted to modify the mailbox, the server
   SHOULD prefix the text of the tagged OK response with the
         "[READ-WRITE]" response code.

      If the client is not permitted to modify the mailbox but is
      permitted read access, the mailbox is selected as read-only, and
      the server MUST prefix the text of the tagged OK response to
      SELECT with the "[READ-ONLY]" response code.  Read-only access
      through SELECT differs from the EXAMINE command in that certain
      read-only mailboxes MAY permit the change of permanent state on a
      per-user (as opposed to global) basis.  Netnews messages marked in
      a server-based .newsrc file are an example of such per-user
      permanent state that can be modified with read-only mailboxes.

   Example:    C: A142 SELECT INBOX
               S: * 172 EXISTS
               S: * 1 RECENT
               S: * OK [UNSEEN 12] Message 12 is first unseen
               S: * OK [UIDVALIDITY 3857529045] UIDs valid
               S: * FLAGS (\Answered \Flagged \Deleted \Seen \Draft)
               S: * OK [PERMANENTFLAGS (\Deleted \Seen \*)] Limited
               S: A142 OK [READ-WRITE] SELECT completed
*/
