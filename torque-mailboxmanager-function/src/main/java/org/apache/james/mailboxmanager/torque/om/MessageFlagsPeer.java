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
package org.apache.james.mailboxmanager.torque.om;

import org.apache.torque.util.Criteria;

import javax.mail.Flags;

/**
 * The skeleton for this class was autogenerated by Torque on:
 *
 * [Wed Sep 06 19:48:03 CEST 2006]
 *
 *  You should add additional methods to this class to meet the
 *  application requirements.  This class will only be generated as
 *  long as it does not already exist in the output directory.
 */
public class MessageFlagsPeer
    extends org.apache.james.mailboxmanager.torque.om.BaseMessageFlagsPeer
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 4709341310937090513L;

    public static void addFlagsToCriteria(Flags flags,boolean value,Criteria c) {
        if (flags.contains(Flags.Flag.ANSWERED)) {
            c.add(ANSWERED,value);
        }
        if (flags.contains(Flags.Flag.DELETED)) {
            c.add(DELETED,value);
        }
        if (flags.contains(Flags.Flag.DRAFT)) {
            c.add(DRAFT,value);
        }
        if (flags.contains(Flags.Flag.FLAGGED)) {
            c.add(FLAGGED,value);
        }
        if (flags.contains(Flags.Flag.RECENT)) {
            c.add(RECENT,value);
        }
        if (flags.contains(Flags.Flag.SEEN)) {
            c.add(SEEN,value);
        }
    }
    

}
