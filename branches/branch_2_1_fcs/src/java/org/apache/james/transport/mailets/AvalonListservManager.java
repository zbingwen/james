/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

package org.apache.james.transport.mailets;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.james.Constants;
import org.apache.james.services.UsersRepository;
import org.apache.james.services.UsersStore;
import org.apache.mailet.MailAddress;

/**
 * Adds or removes an email address to a listserv.
 *
 * <p>Sample configuration:
 * <br>&lt;mailet match="CommandForListserv=james@list.working-dogs.com" class="AvalonListservManager"&gt;
 * <br>&lt;repositoryName&gt;name of user repository configured in UsersStore block &lt;/repositoryName&gt;
 * <br>&lt;/mailet&gt;
 *
 * @author  <a href="sergek@lokitech.com">Serge Knystautas </a>
 * @version This is $Revision: 1.6.4.1 $
 */
public class AvalonListservManager extends GenericListservManager {

    private UsersRepository members;

    /**
     * Initialize the mailet
     */
    public void init() {
        ComponentManager compMgr = (ComponentManager)getMailetContext().getAttribute(Constants.AVALON_COMPONENT_MANAGER);
        try {
            UsersStore usersStore = (UsersStore) compMgr.lookup("org.apache.james.services.UsersStore");
            String repName = getInitParameter("repositoryName");

            members = (UsersRepository) usersStore.getRepository(repName);
        } catch (ComponentException cnfe) {
            log("Failed to retrieve Store component:" + cnfe.getMessage());
        } catch (Exception e) {
            log("Failed to retrieve Store component:" + e.getMessage());
        }
    }

    /**
     * Add an address to the list.
     *
     * @param address the address to add
     *
     * @return true if successful, false otherwise
     */
    public boolean addAddress(MailAddress address) {
        members.addUser(address.toString(), "");
        return true;
    }

    /**
     * Remove an address from the list.
     *
     * @param address the address to remove
     *
     * @return true if successful, false otherwise
     */
    public boolean removeAddress(MailAddress address) {
        members.removeUser(address.toString());
        return true;
    }

    public boolean existsAddress(MailAddress address) {
        return members.contains(address.toString());
    }

    /**
     * Return a string describing this mailet.
     *
     * @return a string describing this mailet
     */
    public String getMailetInfo() {
        return "AvalonListservManager Mailet";
    }
}

