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

package org.apache.james.smtpserver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.ParseException;

import org.apache.james.dnsservice.api.DNSService;
import org.apache.james.dnsservice.api.mock.MockDNSService;
import org.apache.james.protocols.smtp.BaseFakeSMTPSession;
import org.apache.james.protocols.smtp.SMTPSession;
import org.apache.james.protocols.smtp.hook.HookReturnCode;
import org.apache.james.smtpserver.fastfail.ValidRcptMX;
import org.apache.mailet.MailAddress;

import junit.framework.TestCase;

public class ValidRcptMXTest extends TestCase {

    private final static String INVALID_HOST = "invalid.host.de";

    private final static String INVALID_MX = "mx." + INVALID_HOST;

    private final static String LOOPBACK = "127.0.0.1";

    private SMTPSession setupMockedSMTPSession(final MailAddress rcpt) {
        SMTPSession session = new BaseFakeSMTPSession() {
            HashMap state = new HashMap();

            public Map getState() {
                return state;
            }

            public String getRemoteIPAddress() {
                return "127.0.0.1";
            }

        };
        return session;
    }

    private DNSService setupMockedDNSServer() {
        DNSService dns = new MockDNSService() {

            public Collection findMXRecords(String hostname) {
                Collection mx = new ArrayList();

                if (hostname.equals(INVALID_HOST)) {
                    mx.add(INVALID_MX);
                }
                return mx;
            }

            public InetAddress getByName(String host) throws UnknownHostException {
                if (host.equals(INVALID_MX) || host.equals(LOOPBACK)) {
                    return InetAddress.getByName(LOOPBACK);
                } else if (host.equals("255.255.255.255")) {
                    return InetAddress.getByName("255.255.255.255");
                }
                throw new UnknownHostException("Unknown host");
            }

        };

        return dns;
    }

    public void testRejectLoopbackMX() throws ParseException {
        Collection bNetworks = new ArrayList();
        bNetworks.add("127.0.0.1");

        DNSService dns = setupMockedDNSServer();
        MailAddress mailAddress = new MailAddress("test@" + INVALID_HOST);
        SMTPSession session = setupMockedSMTPSession(mailAddress);
        ValidRcptMX handler = new ValidRcptMX();

        handler.setDNSService(dns);
        handler.setBannedNetworks(bNetworks, dns);
        int rCode = handler.doRcpt(session, null, mailAddress).getResult();

        assertEquals("Reject", rCode, HookReturnCode.DENY);
    }

}
