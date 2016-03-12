package net.alantea.horizon.message;

public interface HorizonSubscriber
{
   
   /**
    * On message reception.
    *
    * @param message the message
    */
   public void onMessage(HorizonMessage message);
   
   /**
    * Subscribe to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    */
   public default void subscribe(String id)
   {
      HorizonMessageManager.addSubscription(id, this);
   }
   
   /**
    * Unsubscribe to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    */
   public default void unsubscribe(String id)
   {
      HorizonMessageManager.removeSubscription(id, this);
   }
}
