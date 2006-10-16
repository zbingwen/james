package org.apache.james.mailboxmanager.torque.om;


import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.torque.TorqueException;
import org.apache.torque.om.BaseObject;
import org.apache.torque.om.ComboKey;
import org.apache.torque.om.DateKey;
import org.apache.torque.om.NumberKey;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.om.SimpleKey;
import org.apache.torque.om.StringKey;
import org.apache.torque.om.Persistent;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.Transaction;



                                

/**
 * This class was autogenerated by Torque on:
 *
 * [Tue Sep 19 10:06:28 CEST 2006]
 *
 * You should not use this class directly.  It should not even be
 * extended all references should be to MessageFlags
 */
public abstract class BaseMessageFlags extends BaseObject
{
    /** The Peer class */
    private static final MessageFlagsPeer peer =
        new MessageFlagsPeer();

        
    /** The value for the mailboxId field */
    private long mailboxId;
      
    /** The value for the uid field */
    private long uid;
                                                                
    /** The value for the answered field */
    private boolean answered = false;
                                                                
    /** The value for the deleted field */
    private boolean deleted = false;
                                                                
    /** The value for the draft field */
    private boolean draft = false;
                                                                
    /** The value for the flagged field */
    private boolean flagged = false;
                                                                
    /** The value for the recent field */
    private boolean recent = false;
                                                                
    /** The value for the seen field */
    private boolean seen = false;
  
            
    /**
     * Get the MailboxId
     *
     * @return long
     */
    public long getMailboxId()
    {
        return mailboxId;
    }

                              
    /**
     * Set the value of MailboxId
     *
     * @param v new value
     */
    public void setMailboxId(long v) throws TorqueException
    {
    
                  if (this.mailboxId != v)
              {
            this.mailboxId = v;
            setModified(true);
        }
    
                                  
                if (aMessageRow != null && !(aMessageRow.getMailboxId() == v))
                {
            aMessageRow = null;
        }
      
              }
          
    /**
     * Get the Uid
     *
     * @return long
     */
    public long getUid()
    {
        return uid;
    }

                              
    /**
     * Set the value of Uid
     *
     * @param v new value
     */
    public void setUid(long v) throws TorqueException
    {
    
                  if (this.uid != v)
              {
            this.uid = v;
            setModified(true);
        }
    
                                  
                if (aMessageRow != null && !(aMessageRow.getUid() == v))
                {
            aMessageRow = null;
        }
      
              }
          
    /**
     * Get the Answered
     *
     * @return boolean
     */
    public boolean getAnswered()
    {
        return answered;
    }

                        
    /**
     * Set the value of Answered
     *
     * @param v new value
     */
    public void setAnswered(boolean v) 
    {
    
                  if (this.answered != v)
              {
            this.answered = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Deleted
     *
     * @return boolean
     */
    public boolean getDeleted()
    {
        return deleted;
    }

                        
    /**
     * Set the value of Deleted
     *
     * @param v new value
     */
    public void setDeleted(boolean v) 
    {
    
                  if (this.deleted != v)
              {
            this.deleted = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Draft
     *
     * @return boolean
     */
    public boolean getDraft()
    {
        return draft;
    }

                        
    /**
     * Set the value of Draft
     *
     * @param v new value
     */
    public void setDraft(boolean v) 
    {
    
                  if (this.draft != v)
              {
            this.draft = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Flagged
     *
     * @return boolean
     */
    public boolean getFlagged()
    {
        return flagged;
    }

                        
    /**
     * Set the value of Flagged
     *
     * @param v new value
     */
    public void setFlagged(boolean v) 
    {
    
                  if (this.flagged != v)
              {
            this.flagged = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Recent
     *
     * @return boolean
     */
    public boolean getRecent()
    {
        return recent;
    }

                        
    /**
     * Set the value of Recent
     *
     * @param v new value
     */
    public void setRecent(boolean v) 
    {
    
                  if (this.recent != v)
              {
            this.recent = v;
            setModified(true);
        }
    
          
              }
          
    /**
     * Get the Seen
     *
     * @return boolean
     */
    public boolean getSeen()
    {
        return seen;
    }

                        
    /**
     * Set the value of Seen
     *
     * @param v new value
     */
    public void setSeen(boolean v) 
    {
    
                  if (this.seen != v)
              {
            this.seen = v;
            setModified(true);
        }
    
          
              }
  
      
        
                            
    
        private MessageRow aMessageRow;

    /**
     * Declares an association between this object and a MessageRow object
     *
     * @param v MessageRow
     * @throws TorqueException
     */
    public void setMessageRow(MessageRow v) throws TorqueException
    {
            if (v == null)
        {
                          setMailboxId( 0);
              }
        else
        {
            setMailboxId(v.getMailboxId());
        }
            if (v == null)
        {
                          setUid( 0);
              }
        else
        {
            setUid(v.getUid());
        }
            aMessageRow = v;
    }

                                        
    /**
     * Returns the associated MessageRow object.
           * If it was not retrieved before, the object is retrieved from
     * the database
           *
     * @return the associated MessageRow object
           * @throws TorqueException
           */
    public MessageRow getMessageRow()
              throws TorqueException
          {
              if (aMessageRow == null && (this.mailboxId != 0 && this.uid != 0))
        {
                              aMessageRow = MessageRowPeer.retrieveByPK(this.mailboxId, this.uid);
                  
            /* The following can be used instead of the line above to
               guarantee the related object contains a reference
               to this object, but this level of coupling
               may be undesirable in many circumstances.
               As it can lead to a db query with many results that may
               never be used.
               MessageRow obj = MessageRowPeer.retrieveByPK(this.mailboxId, this.uid);
               obj.add${pCollName}(this);
            */
        }
              return aMessageRow;
    }

    /**
     * Return the associated MessageRow object
     * If it was not retrieved before, the object is retrieved from
     * the database using the passed connection
     *
     * @param connection the connection used to retrieve the associated object
     *        from the database, if it was not retrieved before
     * @return the associated MessageRow object
     * @throws TorqueException
     */
    public MessageRow getMessageRow(Connection connection)
        throws TorqueException
    {
        if (aMessageRow == null && (this.mailboxId != 0 && this.uid != 0))
        {
                          aMessageRow = MessageRowPeer.retrieveByPK(this.mailboxId, this.uid, connection);
              
            /* The following can be used instead of the line above to
               guarantee the related object contains a reference
               to this object, but this level of coupling
               may be undesirable in many circumstances.
               As it can lead to a db query with many results that may
               never be used.
               MessageRow obj = MessageRowPeer.retrieveByPK(this.mailboxId, this.uid, connection);
               obj.add${pCollName}(this);
            */
        }
        return aMessageRow;
    }

    /**
     * Provides convenient way to set a relationship based on a
     * ObjectKey, for example
     * <code>bar.setFooKey(foo.getPrimaryKey())</code>
     *
         * Note: It is important that the xml schema used to create this class
     * maintains consistency in the order of related columns between
     * message_flags and message.
     * If for some reason this is impossible, this method should be
     * overridden in <code>MessageFlags</code>.
         */
    public void setMessageRowKey(ObjectKey key) throws TorqueException
    {
              SimpleKey[] keys = (SimpleKey[]) key.getValue();
                
                            setMailboxId(((NumberKey) keys[0]).longValue());
                  
                            setUid(((NumberKey) keys[1]).longValue());
                            }
       
                
    private static List fieldNames = null;

    /**
     * Generate a list of field names.
     *
     * @return a list of field names
     */
    public static synchronized List getFieldNames()
    {
        if (fieldNames == null)
        {
            fieldNames = new ArrayList();
              fieldNames.add("MailboxId");
              fieldNames.add("Uid");
              fieldNames.add("Answered");
              fieldNames.add("Deleted");
              fieldNames.add("Draft");
              fieldNames.add("Flagged");
              fieldNames.add("Recent");
              fieldNames.add("Seen");
              fieldNames = Collections.unmodifiableList(fieldNames);
        }
        return fieldNames;
    }

    /**
     * Retrieves a field from the object by name passed in as a String.
     *
     * @param name field name
     * @return value
     */
    public Object getByName(String name)
    {
          if (name.equals("MailboxId"))
        {
                return new Long(getMailboxId());
            }
          if (name.equals("Uid"))
        {
                return new Long(getUid());
            }
          if (name.equals("Answered"))
        {
                return Boolean.valueOf(getAnswered());
            }
          if (name.equals("Deleted"))
        {
                return Boolean.valueOf(getDeleted());
            }
          if (name.equals("Draft"))
        {
                return Boolean.valueOf(getDraft());
            }
          if (name.equals("Flagged"))
        {
                return Boolean.valueOf(getFlagged());
            }
          if (name.equals("Recent"))
        {
                return Boolean.valueOf(getRecent());
            }
          if (name.equals("Seen"))
        {
                return Boolean.valueOf(getSeen());
            }
          return null;
    }

    /**
     * Retrieves a field from the object by name passed in
     * as a String.  The String must be one of the static
     * Strings defined in this Class' Peer.
     *
     * @param name peer name
     * @return value
     */
    public Object getByPeerName(String name)
    {
          if (name.equals(MessageFlagsPeer.MAILBOX_ID))
        {
                return new Long(getMailboxId());
            }
          if (name.equals(MessageFlagsPeer.UID))
        {
                return new Long(getUid());
            }
          if (name.equals(MessageFlagsPeer.ANSWERED))
        {
                return Boolean.valueOf(getAnswered());
            }
          if (name.equals(MessageFlagsPeer.DELETED))
        {
                return Boolean.valueOf(getDeleted());
            }
          if (name.equals(MessageFlagsPeer.DRAFT))
        {
                return Boolean.valueOf(getDraft());
            }
          if (name.equals(MessageFlagsPeer.FLAGGED))
        {
                return Boolean.valueOf(getFlagged());
            }
          if (name.equals(MessageFlagsPeer.RECENT))
        {
                return Boolean.valueOf(getRecent());
            }
          if (name.equals(MessageFlagsPeer.SEEN))
        {
                return Boolean.valueOf(getSeen());
            }
          return null;
    }

    /**
     * Retrieves a field from the object by Position as specified
     * in the xml schema.  Zero-based.
     *
     * @param pos position in xml schema
     * @return value
     */
    public Object getByPosition(int pos)
    {
            if (pos == 0)
        {
                return new Long(getMailboxId());
            }
              if (pos == 1)
        {
                return new Long(getUid());
            }
              if (pos == 2)
        {
                return Boolean.valueOf(getAnswered());
            }
              if (pos == 3)
        {
                return Boolean.valueOf(getDeleted());
            }
              if (pos == 4)
        {
                return Boolean.valueOf(getDraft());
            }
              if (pos == 5)
        {
                return Boolean.valueOf(getFlagged());
            }
              if (pos == 6)
        {
                return Boolean.valueOf(getRecent());
            }
              if (pos == 7)
        {
                return Boolean.valueOf(getSeen());
            }
              return null;
    }
     
    /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.
     *
     * @throws Exception
     */
    public void save() throws Exception
    {
          save(MessageFlagsPeer.getMapBuilder()
                .getDatabaseMap().getName());
      }

    /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.
       * Note: this code is here because the method body is
     * auto-generated conditionally and therefore needs to be
     * in this file instead of in the super class, BaseObject.
       *
     * @param dbName
     * @throws TorqueException
     */
    public void save(String dbName) throws TorqueException
    {
        Connection con = null;
          try
        {
            con = Transaction.begin(dbName);
            save(con);
            Transaction.commit(con);
        }
        catch(TorqueException e)
        {
            Transaction.safeRollback(con);
            throw e;
        }
      }

      /** flag to prevent endless save loop, if this object is referenced
        by another object which falls in this transaction. */
    private boolean alreadyInSave = false;
      /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.  This method
     * is meant to be used as part of a transaction, otherwise use
     * the save() method and the connection details will be handled
     * internally
     *
     * @param con
     * @throws TorqueException
     */
    public void save(Connection con) throws TorqueException
    {
          if (!alreadyInSave)
        {
            alreadyInSave = true;


  
            // If this object has been modified, then save it to the database.
            if (isModified())
            {
                if (isNew())
                {
                    MessageFlagsPeer.doInsert((MessageFlags) this, con);
                    setNew(false);
                }
                else
                {
                    MessageFlagsPeer.doUpdate((MessageFlags) this, con);
                }
                }

                      alreadyInSave = false;
        }
      }

                                              
  
    private final SimpleKey[] pks = new SimpleKey[2];
    private final ComboKey comboPK = new ComboKey(pks);

    /**
     * Set the PrimaryKey with an ObjectKey
     *
     * @param key
     */
    public void setPrimaryKey(ObjectKey key) throws TorqueException
    {
        SimpleKey[] keys = (SimpleKey[]) key.getValue();
        SimpleKey tmpKey = null;
                      setMailboxId(((NumberKey)keys[0]).longValue());
                        setUid(((NumberKey)keys[1]).longValue());
              }

    /**
     * Set the PrimaryKey using SimpleKeys.
     *
         * @param mailboxId long
         * @param uid long
         */
    public void setPrimaryKey( long mailboxId, long uid)
        throws TorqueException
    {
            setMailboxId(mailboxId);
            setUid(uid);
        }

    /**
     * Set the PrimaryKey using a String.
     */
    public void setPrimaryKey(String key) throws TorqueException
    {
        setPrimaryKey(new ComboKey(key));
    }
  
    /**
     * returns an id that differentiates this object from others
     * of its class.
     */
    public ObjectKey getPrimaryKey()
    {
              pks[0] = SimpleKey.keyFor(getMailboxId());
                  pks[1] = SimpleKey.keyFor(getUid());
                  return comboPK;
      }
 

    /**
     * Makes a copy of this object.
     * It creates a new object filling in the simple attributes.
       * It then fills all the association collections and sets the
     * related objects to isNew=true.
       */
      public MessageFlags copy() throws TorqueException
    {
        return copyInto(new MessageFlags());
    }
  
    protected MessageFlags copyInto(MessageFlags copyObj) throws TorqueException
    {
          copyObj.setMailboxId(mailboxId);
          copyObj.setUid(uid);
          copyObj.setAnswered(answered);
          copyObj.setDeleted(deleted);
          copyObj.setDraft(draft);
          copyObj.setFlagged(flagged);
          copyObj.setRecent(recent);
          copyObj.setSeen(seen);
  
                            copyObj.setMailboxId( 0);
                                      copyObj.setUid( 0);
                                                
                return copyObj;
    }

    /**
     * returns a peer instance associated with this om.  Since Peer classes
     * are not to have any instance attributes, this method returns the
     * same instance for all member of this class. The method could therefore
     * be static, but this would prevent one from overriding the behavior.
     */
    public MessageFlagsPeer getPeer()
    {
        return peer;
    }


    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append("MessageFlags:\n");
        str.append("MailboxId = ")
               .append(getMailboxId())
             .append("\n");
        str.append("Uid = ")
               .append(getUid())
             .append("\n");
        str.append("Answered = ")
               .append(getAnswered())
             .append("\n");
        str.append("Deleted = ")
               .append(getDeleted())
             .append("\n");
        str.append("Draft = ")
               .append(getDraft())
             .append("\n");
        str.append("Flagged = ")
               .append(getFlagged())
             .append("\n");
        str.append("Recent = ")
               .append(getRecent())
             .append("\n");
        str.append("Seen = ")
               .append(getSeen())
             .append("\n");
        return(str.toString());
    }
}
