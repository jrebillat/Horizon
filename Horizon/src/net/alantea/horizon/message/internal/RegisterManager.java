package net.alantea.horizon.message.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class RegisterManager. It deals with objects registering to events (all identifiers) in contexts, or for all the application.
 */
public class RegisterManager extends SubscriptionManager
{
   /** The 'registered to all events' list. */
   private static List<Object> catchAllList = new ArrayList<>();

   /** The registered map. */
   private static Map<Object, List<Object>> registeredmap = new HashMap<>();

   /**
    * Register to all messages that we are waiting for in default context. This method search for
    * all @Listen(message="ID") or &#64;Listen(messages={"ID1", "ID2"...}) annotations. It also look
    * to all "onXXXXMessage" methods using XXXX as ID. Then it subscribes the object to all
    * corresponding IDs messages.
    *
    * @param object the object
    */
   public static final void register(Object object)
   {
      register(DEFAULTCONTEXT, object);
   }
   
   /**
    * Gets the catch all list.
    *
    * @return the catch all list
    */
   protected static final List<Object> getCatchAllList()
   {
      return catchAllList;
   }

   /**
    * Register to all messages that we are waiting for in the context. This method search for
    * all @Listen(message="ID") or &#64;Listen(messages={"ID1", "ID2"...}) annotations. It also look
    * to all "onXXXXMessage" methods using XXXX as ID. Then it subscribes the object to all
    * corresponding IDs messages.
    *
    * @param context the context
    * @param object the object
    */
   public static final void register(Object context, Object object)
   {
      // Silly call
      if (object == null)
      {
         return;
      }
      
      // Calculate context (in case of NULL context given).
      Object realContext = (context == null) ? DEFAULTCONTEXT : context;
      
      List<Object> registered = registeredmap.get(realContext);
      if (registered == null)
      {
         registered = new ArrayList<Object>();
         registeredmap.put(realContext, registered);
      }
      // register
      if (! registered.contains(object))
      {
         registered.add(object);
      }
   }

   /**
    * Register to all messages in all contexts. This method search for all @Listen(message="ID") or
    * &#64;Listen(messages={"ID1", "ID2"...}) annotations. It also look to all "onXXXXMessage"
    * methods using XXXX as ID. Then it subscribes the object to all corresponding IDs messages. It
    * is using "onMessage" method for all other messages.
    *
    * @param object the object
    */
   public static final void registerAllMessages(Object object)
   {
      if ((object != null) && (!catchAllList.contains(object)))
      {
         catchAllList.add(object);
      }
   }
   
   /**
    * Unregister to default context.
    *
    * @param object the object to unregister
    */
   public static final void unregister(Object object)
   {
      unregister(DEFAULTID, object);
   }

   /**
    * Unregister in given context.
    *
    * @param context the context
    * @param listener the listener
    */
   public static final void unregister(Object context, Object listener)
   {
      // Silly call
      if (listener == null)
      {
         return;
      }
      
      // Calculate context (in case of NULL context given).
      Object realContext = (context == null) ? DEFAULTCONTEXT : context;
      
      List<Object> registered = registeredmap.get(realContext);
      if (registered == null)
      {
         return;
      }
      
      // unregister
      if (registered.contains(listener))
      {
         registered.remove(listener);
      }
   }

   /**
    * Unregister the object from getting all future messages if it was registered for this.
    *
    * @param object the object
    */
   public static final void unregisterAllMessages(Object object)
   {
      // Verify
      if ((object != null) && (catchAllList.contains(object)))
      {
         // remove
         catchAllList.remove(object);
      }
   }
   
   /**
    * Gets the registered.
    *
    * @param context the context
    * @return the registered
    */
   protected static List<Object> getRegistered(Object context)
   {
      // Calculate context (in case of NULL context given).
      Object realContext = (context == null) ? DEFAULTCONTEXT : context;
      
      List<Object> registered = registeredmap.get(realContext);
      if (registered == null)
      {
         registered = new ArrayList<Object>();
         registeredmap.put(realContext, registered);
      }
      return registered;
   }
}
