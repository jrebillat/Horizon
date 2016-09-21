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
    * Register to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    */
   public default void register(String id)
   {
      Messenger.register(id, this);
   }
   
   /**
    * Subscribe to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    * @param context the context
    */
   public default void register(String id, Object context)
   {
      Messenger.addSubscription(context, id, this);
   }
   
   /**
    * Unregister to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    */
   public default void unregister(String id)
   {
      Messenger.unregister(id, this);
   }
   
   /**
    * Unregister to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    * @param context the context
    */
   public default void unregister(String id, Object context)
   {
      Messenger.removeSubscription(context, id, this);
   }
}
