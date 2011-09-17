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
package org.apache.james.pop3server.netty;

import org.apache.james.pop3server.POP3HandlerConfigurationData;
import org.apache.james.pop3server.POP3Protocol;
import org.apache.james.pop3server.core.CoreCmdHandlerLoader;
import org.apache.james.pop3server.jmx.JMXHandlersLoader;
import org.apache.james.protocols.api.HandlersPackage;
import org.apache.james.protocols.impl.BasicChannelUpstreamHandler;
import org.apache.james.protocols.impl.ResponseEncoder;
import org.apache.james.protocols.lib.netty.AbstractProtocolAsyncServer;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

/**
 * NIO POP3 Server which use Netty
 */
public class POP3Server extends AbstractProtocolAsyncServer implements POP3ServerMBean {

    private final static ResponseEncoder POP3_RESPONSE_ENCODER =  new POP3ResponseEncoder(false);
    /**
     * The configuration data to be passed to the handler
     */
    private POP3HandlerConfigurationData theConfigData = new POP3HandlerConfigurationDataImpl();
    private BasicChannelUpstreamHandler coreHandler;
    
    @Override
    protected int getDefaultPort() {
        return 110;
    }
    
    
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.socket.ServerMBean#getServiceType()
     */
    public String getServiceType() {
        return "POP3 Service";
    }

    /**
     * A class to provide POP3 handler configuration to the handlers
     */
    private class POP3HandlerConfigurationDataImpl implements POP3HandlerConfigurationData {

        /**
         * @see org.apache.james.pop3server.POP3HandlerConfigurationData#getHelloName()
         */
        public String getHelloName() {
            return POP3Server.this.getHelloName();
        }

        /**
         * @see org.apache.james.pop3server.POP3HandlerConfigurationData#getResetLength()
         */
        public int getResetLength() {
            return -1;
        }

        /**
         * @see org.apache.james.pop3server.POP3HandlerConfigurationData#isStartTLSSupported()
         */
        public boolean isStartTLSSupported() {
            return POP3Server.this.isStartTLSSupported();
        }
    }

    @Override
    protected void preInit() throws Exception {
        super.preInit();
        POP3Protocol protocol = new POP3Protocol(getProtocolHandlerChain(), theConfigData, getLogger());
        coreHandler = new BasicChannelUpstreamHandler(getProtocolHandlerChain(), protocol.getProtocolSessionFactory(), getLogger(), getSSLContext(), getEnabledCipherSuites());
    }


    @Override
    protected ChannelPipelineFactory createPipelineFactory(ChannelGroup group) {
         
        final ChannelPipelineFactory cpf = super.createPipelineFactory(group);
        return new ChannelPipelineFactory() {
            
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline cp = cpf.getPipeline();
                cp.addAfter("framer", "chunkHandler", new ChunkedWriteHandler());
                return cp;
            }
        };
    }


    @Override
    protected String getDefaultJMXName() {
        return "pop3server";
    }

    @Override
    protected ChannelUpstreamHandler createCoreHandler() {
        return coreHandler; 
    }

    @Override
    protected OneToOneEncoder createEncoder() {
        return POP3_RESPONSE_ENCODER;
    }


    @Override
    protected Class<? extends HandlersPackage> getCoreHandlersPackage() {
        return CoreCmdHandlerLoader.class;
    }


    @Override
    protected Class<? extends HandlersPackage> getJMXHandlersPackage() {
        return JMXHandlersLoader.class;
    }
}
