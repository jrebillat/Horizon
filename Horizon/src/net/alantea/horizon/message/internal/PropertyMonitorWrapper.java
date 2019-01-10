package net.alantea.horizon.message.internal;

import net.alantea.horizon.message.Message;
import net.alantea.liteprops.Property;

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
      Object destination = property.get();
      if ((destination != null) && (destination.getClass().isAssignableFrom(message.getContent().getClass())))
      {
         property.set(message.getContent());
      }
   }

}
