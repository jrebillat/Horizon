package net.alantea.horizon.message.internal;

import javafx.beans.property.Property;
import net.alantea.horizon.message.Message;

public class PropertyMonitorWrapper
{
   @SuppressWarnings("rawtypes")
   private Property property;
   
   public PropertyMonitorWrapper(Property<?> property)
   {
      this.property = property;
   }
   
   @SuppressWarnings({ "unused", "unchecked" })
   private void onMessage(Message message)
   {
      Object destination = property.getValue();
      if ((destination != null) && (destination.getClass().isAssignableFrom(message.getContent().getClass())))
      {
         property.setValue(message.getContent());
      }
   }

}
