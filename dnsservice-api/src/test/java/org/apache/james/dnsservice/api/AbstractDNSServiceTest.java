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
package org.apache.james.dnsservice.api;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.james.dnsservice.api.mock.MockDNSService;

import junit.framework.TestCase;

/**
 * Basic tests for AbstractDNSServer. The goal is to verify that the interface
 * remains constants and that the built platform has access to the Internet.
 */
public class AbstractDNSServiceTest extends TestCase {

    /**
     * Simple Mock DNSService relaying on InetAddress.
     */
    private static final DNSService DNS_SERVER = new MockDNSService() {

        public String getHostName(InetAddress inet) {
            return inet.getCanonicalHostName();
        }

        public InetAddress[] getAllByName(String name) throws UnknownHostException {
            return InetAddress.getAllByName(name);
        }

        public InetAddress getLocalHost() throws UnknownHostException {
            return InetAddress.getLocalHost();
        }

        public InetAddress getByName(String host) throws UnknownHostException {
            return InetAddress.getByName(host);
        }

    };

    /**
     * Simple localhost resolution.
     * 
     * @throws UnknownHostException
     */
    public void testLocalhost() throws UnknownHostException {

        assertEquals("localhost/127.0.0.1", DNS_SERVER.getByName("localhost").toString());

        String localHost = DNS_SERVER.getHostName(InetAddress.getByName("127.0.0.1")).toString();
        // We only can check if the returned localhost is not empty. Its value
        // depends on the hosts file.
        assertTrue(localHost.length() > 0);

    }

    /**
     * Simple apache.org resolution.
     * 
     * @throws UnknownHostException
     */
    public void testApache() throws UnknownHostException {
        assertEquals(true, DNS_SERVER.getByName("www.apache.org").toString().startsWith("www.apache.org"));
    }

}
