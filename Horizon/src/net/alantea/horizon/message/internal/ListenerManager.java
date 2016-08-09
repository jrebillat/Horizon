package net.alantea.horizon.message.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ListenerManager extends MethodsManager
{

   /** The listener map. A map < message source, List<subscribers>>*/
   private static Map<Object, List<Object>> listenermap = new ConcurrentHashMap<>();
   
   /**
    * Adds an horizon listener to a specific source.
    *
    * @param object the horizon source
    * @param listener the listener
    * @return true, if successful
    */
   public static final boolean addHorizonListener(Object object, Object listener)
   {
      if ((object == null) || (listener == null))
      {
         return false;
      }

      synchronized (listenermap)
      {
         List<Object> listeners = listenermap.get(object);

         if (listeners == null)
         {
            listeners = new CopyOnWriteArrayList<>();
            listenermap.put(object, listeners);
         }
         if (!listeners.contains(listener))
         {
            listeners.add(listener);
            getMethods(listener.getClass());
         }
      }
      return true;
   }

   /**
    * Removes a horizon listener from a specific source.
    *
    * @param object the horizon source
    * @param listener the listener
    * @return true, if successful
    */
   public static final boolean removeHorizonListener(Object object, Object listener)
   {
      synchronized (listenermap)
      {
         List<Object> listeners = listenermap.get(object);

         if (listeners != null)
         {
            if (listeners.contains(listener))
            {
               listeners.remove(listener);
               return true;
            }
         }
      }
      return false;
   }

   /**
    * Removes all horizon listeners from a specific source.
    *
    * @param object the horizon source
    * @return true, if successful
    */
   public static final boolean removeAllHorizonListeners(Object object)
   {
      if (object == null)
      {
         return false;
      }
      
      synchronized (listenermap)
      {
         List<Object> listeners = listenermap.get(object);

         if (listeners != null)
         {
            listeners.clear();
            return true;
         }
      }
      return false;
   }

   /**
    * Gets the listenermap.
    *
    * @return the listenermap
    */
   protected static final Map<Object, List<Object>> getListenermap()
   {
      return listenermap;
   }
}
