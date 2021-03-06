package net.alantea.horizon.message.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.alantea.horizon.message.FunctionalSubscription;


/**
 * The Class ListenerManager. It contains the methods to manage the listening process for sources.
 */
public class ListenerManager
{
   /** The Constant EMPTYLIST. */
   protected static final List<Object> EMPTYLIST = Collections.unmodifiableList(new ArrayList<Object>());

   /** The listener map. A map < message source, List<subscribers>>*/
   private static Map<Object, List<Object>> listenermap = new ConcurrentHashMap<>();
   
   /**
    * Adds an horizon listener to a specific source.
    *
    * @param source the horizon source
    * @param listener the listener
    * @return true, if successful
    */
   public static final boolean addHorizonListener(Object source, Object listener)
   {
      if ((source == null) || (listener == null))
      {
         return false;
      }

      // the map shall not be modified during addition.
      synchronized (listenermap)
      {
         // Get already registered listeners.
         List<Object> listeners = listenermap.get(source);

         // First time : the list do not exist.
         if (listeners == null)
         {
            // create empty list and add it to map.
            listeners = new ArrayList<>();
            listenermap.put(source, listeners);
         }
         
         if (!listeners.contains(listener))
         {
            //Add listener if it is not already registered
            listeners.add(listener);
            // prepare list of methods for later use
            if (!(listener instanceof FunctionalSubscription))
            {
               MethodsManager.getMethods(listener.getClass());
            }
         }
      }
      return true;
   }

   /**
    * Removes an horizon listener from a specific source.
    *
    * @param source the horizon source
    * @param listener the listener
    * @return true, if successful
    */
   public static final boolean removeHorizonListener(Object source, Object listener)
   {
      // Silly case
      if (source == null)
      {
         return false;
      }
      
      // Don't modify while removing
      synchronized (listenermap)
      {
         // Get registered listeners.
         List<Object> listeners = listenermap.get(source);

         // First time : the list do not exist.
         if (listeners != null)
         {
            // if listener is registered
            if (listeners.contains(listener))
            {
               // remove from list
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
    * @param source the horizon source
    * @return true, if successful
    */
   public static final boolean removeAllHorizonListeners(Object source)
   {
      // Silly case
      if (source == null)
      {
         return false;
      }

      // Don't modify while removing
      synchronized (listenermap)
      {
         List<Object> listeners = listenermap.get(source);

         // First time : the list do not exist.
         if (listeners != null)
         {
            // clear list
            listeners.clear();
            return true;
         }
      }
      return false;
   }

   /**
    * Gets the listeners for a source.
    *
    * @param source the source
    * @return the listener map
    */
   public static final List<Object> getListeners(Object source)
   {
      if (source == null)
      {
         return EMPTYLIST;
      }
      
      List<Object> listeners = listenermap.get(source);
      // First time : the list do not exist.
      if (listeners == null)
      {
         // create empty list and add it to map.
         listeners = new ArrayList<>();
         listenermap.put(source, listeners);
      }
      
      if (!(source instanceof Class))
      {
         Class<?> theClass = source.getClass();
         while (theClass != Object.class)
         {
            // get list of listeners for the class
            List<Object> classListeners = listenermap.get(theClass);
            if (classListeners != null)
            {
               // add it to list.
               listeners.addAll(classListeners);
            }
            theClass = theClass.getSuperclass();
         }
      }
      return listeners;
   }
   
   /**
    * Checks if listener is listening to the source.
    *
    * @param source the source
    * @param listener the listener
    * @return true, if is listening
    */
   public static boolean isListening(Object source, Object listener)
   {
      // Silly case
      if ((source == null) || (listener == null))
      {
         return false;
      }
      List<Object> list = listenermap.get(source);
      if (list == null)
      {
         return false;
      }
      return list.contains(listener);
   }
}
