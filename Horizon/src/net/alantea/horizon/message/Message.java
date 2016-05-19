package net.alantea.horizon.message;

/**
 * The Message class.
 */
public class HorizonMessage
{
   
   /** The sender. */
   private Object sender;
   
   /** The receiver. */
   private Object receiver;
   
   /** The identifier. */
   private String id;
   
   /** The content. */
   private Object content;
   
   /** The confidential flag. */
   private boolean confidential;
   
   /**
    * Instantiates a new message.
    *
    * @param sender the sender
    * @param receiver the receiver
    * @param id the identifier
    * @param content the content
    * @param conf the confidentiality
    */
   public HorizonMessage(Object sender, Object receiver, String id, Object content, boolean conf)
   {
      super();
      this.sender = sender;
      this.receiver = receiver;
      this.id = id;
      this.content = content;
      this.confidential = conf;
   }

   /**
    * Gets the sender.
    *
    * @return the sender
    */
   public Object getSender()
   {
      return sender;
   }

   /**
    * Gets the receiver.
    *
    * @return the receiver
    */
   public Object getReceiver()
   {
      return receiver;
   }

   /**
    * Gets the identifier.
    *
    * @return the identifier
    */
   public String getIdentifier()
   {
      return id;
   }

   /**
    * Gets the content.
    *
    * @return the content
    */
   public Object getContent()
   {
      return content;
   }

   /**
    * Checks if is confidential.
    *
    * @return true, if is confidential
    */
   public boolean isConfidential()
   {
      return confidential;
   }

   /**
    * Sets the message as confidential.
    *
    * @param confidential the new confidentiality
    */
   public void setConfidential(boolean confidential)
   {
      this.confidential = confidential;
   }

}