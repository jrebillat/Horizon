package net.alantea.horizon.message;

import javafx.beans.property.ReadOnlyProperty;
import net.alantea.horizon.message.internal.SendingManager;

/**
 * The Class Messenger.
 */
public final class Messenger extends SendingManager
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
         sendSingleMessage(message);
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
      sendSingleMessage(new Message(DEFAULTCONTEXT, sender, null, id, content, false));
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
      sendSingleMessage(new Message(context, sender, null, id, content, false));
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
      sendSingleMessage(new Message(DEFAULTCONTEXT, sender, receiver, id, content, conf));
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
      sendSingleMessage(new Message(context, sender, receiver, id, content, conf));
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
      sendMessage(DEFAULTCONTEXT, sender, null, id, content, false);
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
      sendMessage(DEFAULTCONTEXT, sender, receiver, id, content, conf);
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
      internalSendMessage(message);
   }
   
   /** Monitor a javafx property. Each change on property will trigger the given message type.
    * The property is set as source and the new value is given as content.
    * @param property to be monitored.
    * @param identifier to use for sending messages.
    */
   public static final void monitorProperty(ReadOnlyProperty<?> property, String identifier)
   {
      monitorProperty(DEFAULTCONTEXT, property, identifier);
   }
   
   /**
    *  Monitor a javafx property. Each change on property will trigger the given message type.
    * The property is set as source and the new value is given as content.
    *
    * @param context the context
    * @param property to be monitored.
    * @param identifier to use for sending messages.
    */
   public static final void monitorProperty(Object context, ReadOnlyProperty<?> property, String identifier)
   {
      property.addListener((v, o, n) -> { sendMessage(context, property, identifier, n); });
   }
}
