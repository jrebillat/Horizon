package net.alantea.horizon.message;

public interface MessageSubscriber
{
   
   /**
    * On message reception.
    *
    * @param message the message
    */
   public void onMessage(Message message);
   
   /**
    * Subscribe to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    */
   public default void subscribe(String id)
   {
      Messenger.addSubscription(id, this, Messenger.DEFAULTCONTEXT);
   }
   
   /**
    * Subscribe to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    * @param context the context
    */
   public default void subscribe(String id, Object context)
   {
      Messenger.addSubscription(id, this, context);
   }
   
   /**
    * Unsubscribe to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    */
   public default void unsubscribe(String id)
   {
      Messenger.removeSubscription(id, this);
   }
   
   /**
    * Unsubscribe to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    * @param context the context
    */
   public default void unsubscribe(String id, Object context)
   {
      Messenger.removeSubscription(id, this, context);
   }
}
