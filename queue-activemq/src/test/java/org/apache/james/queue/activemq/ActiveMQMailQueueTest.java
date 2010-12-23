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
package org.apache.james.queue.activemq;

import java.io.IOException;
import java.util.Arrays;

import javax.jms.ConnectionFactory;
import javax.mail.MessagingException;

import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.plugin.StatisticsBrokerPlugin;
import org.apache.commons.logging.impl.SimpleLog;
import org.apache.james.queue.api.MailQueue.MailQueueItem;
import org.apache.james.queue.jms.JMSMailQueue;
import org.apache.james.queue.jms.JMSMailQueueTest;
import org.apache.mailet.Mail;

public class ActiveMQMailQueueTest extends JMSMailQueueTest{


    @Override
    protected BrokerService createBroker() throws Exception {
        BrokerService broker =  super.createBroker();
        // Enable statistics
        broker.setPlugins(new BrokerPlugin[] {new StatisticsBrokerPlugin()});
        broker.setEnableStatistics(true);
        
        // Enable priority support
        PolicyMap pMap = new PolicyMap();
        PolicyEntry entry = new PolicyEntry();
        entry.setPrioritizedMessages(true);
        entry.setQueue("test");
        pMap.setPolicyEntries(Arrays.asList(entry));
        broker.setDestinationPolicy(pMap);
        
        return broker;
    }

    @Override
    protected JMSMailQueue createQueue(ConnectionFactory factory) {
        SimpleLog log = new SimpleLog("MockLog");
        log.setLevel(SimpleLog.LOG_LEVEL_DEBUG);
        ActiveMQMailQueue queue = new ActiveMQMailQueue(factory, "test", false,log);
        return queue;
    }
    
    public void testPrioritySupport() throws InterruptedException, MessagingException, IOException {
            // should be empty
            assertEquals(0, queue.getSize());
            
            Mail mail = createMail();
            Mail mail2 =createMail();
            mail2.setAttribute(ActiveMQMailQueue.MAIL_PRIORITY, ActiveMQMailQueue.HIGH_PRIORITY);

            queue.enQueue(mail);
            queue.enQueue(mail2);
            
            Thread.sleep(200);
            
            assertEquals(2, queue.getSize());

            
            // we should get mail2 first as it has a higher priority set
            assertEquals(2, queue.getSize());
            MailQueueItem item2 = queue.deQueue();
            checkMail(mail2, item2.getMail());
            item2.done(true);

            Thread.sleep(200);

            
            
            assertEquals(1, queue.getSize());
            MailQueueItem item3 = queue.deQueue();
            checkMail(mail, item3.getMail());
            item3.done(true);
            
            Thread.sleep(200);

            // should be empty
            assertEquals(0, queue.getSize());
        }
        

}
