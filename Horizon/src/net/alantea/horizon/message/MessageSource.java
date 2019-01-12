package net.alantea.horizon.message;

import net.alantea.horizon.message.internal.ListenerManager;

/**
 * The Interface MessageSource. Please do not try to override default methods !
 */
public interface MessageSource
{
   
   /**
    * Send an event message to all listeners. Listeners may listen to the source or have subscribe to the message id.
    *
    * @param name the name
    */
   public default void sendMessage(String name)
   {
      Messenger.sendMessage(new Message(null, this, null, name, "", false));
   }
   
   /**
    * Send an event message to all listeners. Listeners may listen to the source or have subscribe to the message id.
    *
    * @param name the name
    * @param parameters the parameters
    */
   public default void sendMessage(String name, Object parameters)
   {
      Messenger.sendMessage(new Message(null, this, null, name, parameters, false));
   }
   
   /**
    * Send an event message to all listeners. Listeners may listen to the source or have subscribe to the message id.
    *
    * @param message the message
    */
   public default void sendMessage(Message message)
   {
      Messenger.sendMessage(message);
   }
   
   /**
    * Send a message. Please consider it carefully before overriding !
    *
    * @param receiver the receiver
    * @param id the id
    * @param content the content
    */
   public default void sendMessage(Object receiver, String id, Object content)
   {
      Messenger.sendMessage(this, receiver, id, content, false);
   }
   
   /**
    * Send a confidential message. Please consider it carefully before overriding !
    *
    * @param receiver the receiver
    * @param id the id
    * @param content the content
    */
   public default void sendConfidentialMessage(Object receiver, String id, Object content)
   {
      Messenger.sendMessage(this, receiver, id, content, true);
   }
   
   /**
    * Adds an horizon listener.
    *
    * @param listener the listener
    */
   public default void addHorizonListener(Object listener)
   {
      ListenerManager.addHorizonListener(this, listener);
   }
   
   /**
    * Removes an horizon listener.
    *
    * @param listener the listener
    */
   public default void removeHorizonListener(Object listener)
   {
      ListenerManager.removeHorizonListener(this, listener);
   }
}
