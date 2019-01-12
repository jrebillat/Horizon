package net.alantea.horizon.message;

import net.alantea.horizon.message.internal.ListenerManager;
import net.alantea.horizon.message.internal.PropertyMonitor;
import net.alantea.horizon.message.internal.RegisterManager;
import net.alantea.horizon.message.internal.SendingManager;
import net.alantea.horizon.message.internal.SubscriptionManager;

/**
 * The Class Messenger.
 */
public final class Messenger extends PropertyMonitor
{


   /**
    * Send immediately a synchronous message.
    *
    * @param message the message
    */
   public static final void sendSynchronousMessage(Message message)
   {
      if (message != null)
      {
         SendingManager.sendSingleMessage(message);
      }
   }

   /**
    * Send immediately a synchronous message to the world.
    *
    * @param sender the sender
    * @param id the id
    * @param content the content
    */
   public static final void sendSynchronousMessage(Object sender, String id, Object content)
   {
      SendingManager.sendSingleMessage(new Message(SubscriptionManager.DEFAULTCONTEXT, sender, null, id, content, false));
   }

   /**
    * Send immediately a synchronous message to the world.
    *
    * @param context the context
    * @param sender the sender
    * @param id the id
    * @param content the context
    */
   public static final void sendSynchronousMessage(Object context, Object sender, String id, Object content)
   {
      SendingManager.sendSingleMessage(new Message(context, sender, null, id, content, false));
   }

   /**
    * Send immediately a synchronous message to a receiver.
    *
    * @param sender the sender
    * @param receiver the receiver
    * @param id the id
    * @param content the content
    * @param conf the confidentiality
    */
   public static final void sendSynchronousMessage(Object sender, Object receiver, String id, Object content,
         boolean conf)
   {
      SendingManager.sendSingleMessage(new Message(SubscriptionManager.DEFAULTCONTEXT, sender, receiver, id, content, conf));
   }

   /**
    * Send immediately a synchronous message to a receiver.
    *
    * @param context the context
    * @param sender the sender
    * @param receiver the receiver
    * @param id the id
    * @param content the context
    * @param conf the confidentiality
    */
   public static final void sendSynchronousMessage(Object context, Object sender, Object receiver, String id, Object content,
         boolean conf)
   {
      SendingManager.sendSingleMessage(new Message(context, sender, receiver, id, content, conf));
   }

   /**
    * Send confidential message to a receiver.
    *
    * @param sender the sender
    * @param receiver the receiver
    * @param id the id
    * @param content the content
    */
   public static final void sendConfidentialMessage(Object sender, Object receiver, String id, Object content)
   {
      sendMessage(sender, receiver, id, content, true);
   }
   /**
    * Send message to the world.
    *
    * @param sender the sender
    * @param id the id
    * @param content the content
    */
   public static final void sendMessage(Object sender, String id, Object content)
   {
      sendMessage(SubscriptionManager.DEFAULTCONTEXT, sender, null, id, content, false);
   }

   /**
    * Send message to a receiver.
    *
    * @param sender the sender
    * @param receiver the receiver
    * @param id the id
    * @param content the content
    * @param conf the confidentiality
    */
   public static final void sendMessage(Object sender, Object receiver, String id, Object content, boolean conf)
   {
      sendMessage(SubscriptionManager.DEFAULTCONTEXT, sender, receiver, id, content, conf);
   }

   /**
    * Send message to the world.
    *
    * @param context the context
    * @param sender the sender
    * @param id the id
    * @param content the content
    */
   public static final void sendMessage(Object context, Object sender, String id, Object content)
   {
      sendMessage(context, sender, null, id, content, false);
   }

   /**
    * Send message to a receiver.
    *
    * @param context the context
    * @param sender the sender
    * @param receiver the receiver
    * @param id the id
    * @param content the content
    * @param conf the confidentiality
    */
   public static final void sendMessage(Object context, Object sender, Object receiver, String id, Object content, boolean conf)
   {
      sendMessage(new Message(context, sender, receiver, id, content, conf));
   }

   /**
    * Send message.
    *
    * @param message the message
    */
   public synchronized static final void sendMessage(Message message)
   {
      SendingManager.internalSendMessage(message);
   }
   
   /**
    * Sets the mode.
    *
    * @param newMode the new mode
    */
   public static final void setMode(Mode newMode)
   {
      SendingManager.setMode(newMode);
   }

   /**
    * Register.
    *
    * @param object the object
    */
   public static void register(Object object)
   {
      RegisterManager.register(object);
   }

   /**
    * Register.
    *
    * @param context the context
    * @param object the object
    */
   public static void register(Object context, Object object)
   {
      RegisterManager.register(context, object);
   }

   /**
    * Unregister.
    *
    * @param listener the listener
    */
   public static final void unregister(Object listener)
   {
      RegisterManager.unregister(listener);
   }

   /**
    * Unregister.
    *
    * @param context the context
    * @param listener the listener
    */
   public static final void unregister(Object context, Object listener)
   {
      RegisterManager.unregister(context, listener);
   }

   /**
    * Register all messages.
    *
    * @param object the object
    */
   public static final void registerAllMessages(Object object)
   {
      RegisterManager.registerAllMessages(object);
   }
   
   /**
    * Unregister all messages.
    *
    * @param object the object
    */
   public static final void unregisterAllMessages(Object object)
   {
      RegisterManager.unregisterAllMessages(object);
   }
   
   /**
    * Adds the subscription.
    *
    * @param identifier the identifier
    * @param subscriber the subscriber
    */
   public static final void addSubscription(String identifier, Object subscriber)
   {
      SubscriptionManager.addSubscription(identifier, subscriber);
   }

   /**
    * Adds the subscription.
    *
    * @param context the context
    * @param identifier the identifier
    * @param subscriber the subscriber
    */
   public static final void addSubscription(Object context, String identifier, Object subscriber)
   {
      SubscriptionManager.addSubscription(context, identifier, subscriber);
   }

   /**
    * Removes the subscription.
    *
    * @param identifier the identifier
    * @param subscriber the subscriber
    */
   public static final void removeSubscription(String identifier, Object subscriber)
   {
      SubscriptionManager.removeSubscription(identifier, subscriber);
   }

   public static final void removeSubscription(Object context, String identifier, Object subscriber)
   {
      SubscriptionManager.removeSubscription(context, identifier, subscriber);
   }

   /**
    * Listen.
    *
    * @param object the object
    */
   public static void addListener(Object object, Object listener)
   {
      ListenerManager.addHorizonListener(object, listener);
   }

   /**
    * Removes the listener.
    *
    * @param source the source
    * @param listener the listener
    */
   public static void removeListener(Object source, Object listener)
   {
      ListenerManager.removeHorizonListener(source, listener);
   }
   
   /**
    * Removes the all listeners.
    *
    * @param source the source
    */
   public static void removeAllListeners(Object source)
   {
      ListenerManager.removeAllHorizonListeners(source);
   }

   /**
    * Sets the interval.
    *
    * @param value the new interval
    */
   public static void setInterval(int value)
   {
      SendingManager.setInterval(value);
   }
   
}
