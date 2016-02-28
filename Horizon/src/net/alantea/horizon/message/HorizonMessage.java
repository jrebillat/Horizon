package net.alantea.horizon.message;

/**
 * The Message class.
 */
public class HorizonMessage
{
   
   /** The sender. */
   private HorizonSubscriber sender;
   
   /** The receiver. */
   private HorizonSubscriber receiver;
   
   /** The identifier. */
   private String id;
   
   /** The content. */
   private Object content;
   
   /**
    * Instantiates a new message.
    *
    * @param sender the sender
    * @param receiver the receiver
    * @param id the identifier
    * @param content the content
    */
   public HorizonMessage(HorizonSubscriber sender, HorizonSubscriber receiver, String id, Object content)
   {
      super();
      this.sender = sender;
      this.receiver = receiver;
      this.id = id;
      this.content = content;
   }

   /**
    * Gets the sender.
    *
    * @return the sender
    */
   public HorizonSubscriber getSender()
   {
      return sender;
   }

   /**
    * Gets the receiver.
    *
    * @return the receiver
    */
   public HorizonSubscriber getReceiver()
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

}
