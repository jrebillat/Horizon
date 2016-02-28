package net.alantea.horizon.event;

/**
 * The Interface HorizonSource. Please do not try to override default methods !
 */
public interface HorizonSource
{
   
   /**
    * Send event.
    *
    * @param name the name
    * @param parameters the parameters
    */
   public default void sendEvent(String name, Object parameters)
   {
      HorizonEventManager.sendEvent(new HorizonEvent(this, name, parameters));
   }
   
   /**
    * Send event.
    *
    * @param event the event
    */
   public default void sendEvent(HorizonEvent event)
   {
      HorizonEventManager.sendEvent(event);
   }
   
   /**
    * Adds the horizon listener.
    *
    * @param listener the listener
    */
   public default void addHorizonListener(HorizonListener listener)
   {
      HorizonEventManager.addHorizonListener(this, listener);
   }
   
   /**
    * Removes the horizon listener.
    *
    * @param listener the listener
    */
   public default void removeHorizonListener(HorizonListener listener)
   {
      HorizonEventManager.removeHorizonListener(this, listener);
   }
   
}
