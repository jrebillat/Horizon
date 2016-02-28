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
    * Send a message. Please consider it carefully before overriding !
    *
    * @param receiver the receiver
    * @param id the id
    * @param content the content
    */
   public default void sendMessage(HorizonSubscriber receiver, String id, Object content)
   {
      HorizonMessageManager.sendMessage(this, receiver, id, content);
   }
}
