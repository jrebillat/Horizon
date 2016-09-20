package net.alantea.horizon.message.internal;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import net.alantea.horizon.message.Message;

public class PropertyMonitor extends SendingManager
{
   
   /** Monitor a javafx property. Each change on property will trigger the given message type.
    * The property is set as source and the new value is given as content.
    * @param property to be monitored.
    * @param identifier to use for sending messages.
    */
   public static final void monitorProperty(ReadOnlyProperty<?> property, String identifier)
   {
      monitorProperty(DEFAULTCONTEXT, property, identifier, (String)null);
   }
   
   /** Monitor a javafx property. Each change on property will trigger the given message type.
    * The property is set as source and the new value is given as content.
    * @param property to be monitored.
    * @param identifier to use for sending messages.
    */
   public static final void monitorProperty(ReadOnlyProperty<?> property, String getIdentifier, String setIdentifier)
   {
      monitorProperty(DEFAULTCONTEXT, property, getIdentifier, setIdentifier);
   }
   
   /**
    *  Monitor a javafx property. Each change on property will trigger the given message type.
    * The property is set as source and the new value is given as content.
    *
    * @param context the context
    * @param property to be monitored.
    * @param identifier to use for sending messages.
    */
   public static final void monitorProperty(Object context, ReadOnlyProperty<?> property, String getIdentifier, String setIdentifier)
   {
      if (getIdentifier != null)
      {
         property.addListener((v, o, n) -> { 
            internalSendMessage(new Message(context, property, null, getIdentifier, n, false)); });
      }
      
      if ((setIdentifier != null) && (property instanceof Property))
      {
         PropertyMonitorWrapper wrapper = new PropertyMonitorWrapper((Property<?>) property);
         register(context, wrapper, setIdentifier);
      }
   }
}
