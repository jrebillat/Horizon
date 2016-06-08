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
      HorizonMessageManager.sendMessage(new HorizonMessage(this, null, name, null, false));
   }
   
   /**
    * Send an event message to all listeners. Listeners may listen to the source or have subscribe to the message id.
    *
    * @param name the name
    * @param parameters the parameters
    */
   public default void sendMessage(String name, Object parameters)
   {
      HorizonMessageManager.sendMessage(new HorizonMessage(this, null, name, parameters, false));
   }
   
   /**
    * Send an event message to all listeners. Listeners may listen to the source or have subscribe to the message id.
    *
    * @param message the message
    */
   public default void sendMessage(HorizonMessage message)
   {
      HorizonMessageManager.sendMessage(message);
   }
   
   /**
    * Adds an horizon listener.
    *
    * @param listener the listener
    */
   public default void addHorizonListener(Object listener)
   {
      HorizonMessageManager.addHorizonListener(this, listener);
   }
   
   /**
    * Removes an horizon listener.
    *
    * @param listener the listener
    */
   public default void removeHorizonListener(Object listener)
   {
      HorizonMessageManager.removeHorizonListener(this, listener);
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
      HorizonMessageManager.sendMessage(this, receiver, id, content, false);
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
      HorizonMessageManager.sendMessage(this, receiver, id, content, true);
   }
}
