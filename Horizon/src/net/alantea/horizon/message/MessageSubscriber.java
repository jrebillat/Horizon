package net.alantea.horizon.message;

import net.alantea.horizon.message.internal.SubscriptionManager;

public interface MessageSubscriber extends MessageTarget
{
   /**
    * Register to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    */
   public default void subscribe(String id)
   {
      SubscriptionManager.addSubscription(id, this);
   }
   
   /**
    * Subscribe to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    * @param context the context
    */
   public default void subscribe(String id, Object context)
   {
      SubscriptionManager.addSubscription(context, id, this);
   }
   
   /**
    * Unsubscribe to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    */
   public default void unsubscribe(String id)
   {
      SubscriptionManager.removeSubscription(id, this);
   }
   
   /**
    * Unsubscribe to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    * @param context the context
    */
   public default void unsubscribe(String id, Object context)
   {
      SubscriptionManager.removeSubscription(context, id, this);
   }
}
