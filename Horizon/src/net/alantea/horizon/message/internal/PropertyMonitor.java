package net.alantea.horizon.message.internal;

import net.alantea.horizon.message.Message;
import net.alantea.liteprops.Property;

public class PropertyMonitor extends SendingManager
{
   
   /** Monitor a liteprops property. Each change on property will trigger the given message type.
    * The property is set as source and the new value is given as content.
    * @param property to be monitored.
    * @param identifier to use for sending messages.
    */
   public static final void monitorProperty(Property<?> property, String identifier)
   {
      monitorProperty(DEFAULTCONTEXT, property, identifier, (String)null);
   }
   
   /**
    *  Monitor a liteprops property. Each change on property will trigger the given message type.
    * The property is set as source and the new value is given as content.
    *
    * @param property to be monitored.
    * @param getIdentifier the message identifier to use to get value when changed
    * @param setIdentifier the message identifier to use to set the value to the message content.
    */
   public static final void monitorProperty(Property<?> property, String getIdentifier, String setIdentifier)
   {
      monitorProperty(DEFAULTCONTEXT, property, getIdentifier, setIdentifier);
   }
   
   /**
    *  Monitor a liteprops property. Each change on property will trigger the given message type.
    * The property is set as source and the new value is given as content.
    *
    * @param context the context
    * @param property to be monitored.
    * @param getIdentifier the message identifier to use to get value when changed
    * @param setIdentifier the message identifier to use to set the value to the message content.
    */
   public static final void monitorProperty(Object context, Property<?> property, String getIdentifier, String setIdentifier)
   {
      if (getIdentifier != null)
      {
         property.addListener((o, n) -> { 
            internalSendMessage(new Message(context, property, null, getIdentifier, n, false)); });
      }
      
      if ((setIdentifier != null) && (property instanceof Property))
      {
         PropertyMonitorWrapper wrapper = new PropertyMonitorWrapper((Property<?>) property);
         addSubscription(context, setIdentifier, wrapper);
      }
   }
}
