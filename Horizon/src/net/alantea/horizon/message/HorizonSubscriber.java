package net.alantea.horizon.message;

public interface HorizonSubscriber
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
      MessageManager.addSubscription(id, this);
   }
   
   /**
    * Unsubscribe to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    */
   public default void unsubscribe(String id)
   {
      MessageManager.removeSubscription(id, this);
   }
}
