package net.alantea.horizon.message;

import net.alantea.horizon.message.internal.RegisterManager;
import net.alantea.horizon.message.internal.SubscriptionManager;

public interface MessageSubscriber extends MessageTarget
{
   /**
    * Register to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    */
   public default void register(String id)
   {
      RegisterManager.register(id, this);
   }
   
   /**
    * Subscribe to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    * @param context the context
    */
   public default void register(String id, Object context)
   {
      SubscriptionManager.addSubscription(context, id, this);
   }
   
   /**
    * Unregister to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    */
   public default void unregister(String id)
   {
      RegisterManager.unregister(id, this);
   }
   
   /**
    * Unregister to a message type. Please consider it carefully before overriding !
    *
    * @param id the id
    * @param context the context
    */
   public default void unregister(String id, Object context)
   {
      SubscriptionManager.removeSubscription(context, id, this);
   }
}
