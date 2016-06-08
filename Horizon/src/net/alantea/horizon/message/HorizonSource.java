package net.alantea.horizon.message;

/**
 * The Interface HorizonSource. Please do not try to override default methods !
 */
public interface HorizonSource
{
   
   /**
    * Send an event message to all listeners. Listeners may listen to the source or have subscribe to the message id.
    *
    * @param name the name
    */
   public default void sendMessage(String name)
   {
      MessageManager.sendMessage(new Message(this, null, name, null, false));
   }
   
   /**
    * Send an event message to all listeners. Listeners may listen to the source or have subscribe to the message id.
    *
    * @param name the name
    * @param parameters the parameters
    */
   public default void sendMessage(String name, Object parameters)
   {
      MessageManager.sendMessage(new Message(this, null, name, parameters, false));
   }
   
   /**
    * Send an event message to all listeners. Listeners may listen to the source or have subscribe to the message id.
    *
    * @param message the message
    */
   public default void sendMessage(Message message)
   {
      MessageManager.sendMessage(message);
   }
   
   /**
    * Adds an horizon listener.
    *
    * @param listener the listener
    */
   public default void addHorizonListener(Object listener)
   {
      MessageManager.addHorizonListener(this, listener);
   }
   
   /**
    * Removes an horizon listener.
    *
    * @param listener the listener
    */
   public default void removeHorizonListener(Object listener)
   {
      MessageManager.removeHorizonListener(this, listener);
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
      MessageManager.sendMessage(this, receiver, id, content, false);
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
      MessageManager.sendMessage(this, receiver, id, content, true);
   }
}
