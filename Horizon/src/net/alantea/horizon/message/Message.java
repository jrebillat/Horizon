package net.alantea.horizon.message;

/**
 * The Message class.
 */
public class Message
{
   
   /** The sender. */
   private Object sender;
   
   /** The receiver. */
   private Object receiver;
   
   /** The identifier. */
   private String id;
   
   /** The content. */
   private Object content;
   
   /** The context. */
   private Object context;
   
   /** The confidential flag. */
   private boolean confidential;
   
   /**
    * Instantiates a new message.
    *
    * @param sender the sender
    * @param receiver the receiver
    * @param id the identifier
    * @param content the content
    * @param context the context
    * @param conf the confidentiality
    */
   public Message(Object sender, Object receiver, String id, Object content, Object context, boolean conf)
   {
      super();
      this.sender = sender;
      this.receiver = receiver;
      this.id = id;
      this.content = (content == null) ? this : content;
      this.context = context;
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
    * Gets the context.
    *
    * @return the context
    */
   public Object getContext()
   {
      return context;
   }

   /**
    * Verify context coherency.
    *
    * @param neededContext the needed context
    * @return the content
    */
   public boolean isInContext(Object neededContext)
   {
      return ((context == null) || (context.equals(neededContext)));
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

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "Message : sender=" + sender + ", receiver=" + receiver + ", id=" + id + ", content=" + content +", confidential=" + confidential;
   }
}
