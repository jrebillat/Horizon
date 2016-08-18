package net.alantea.horizon.message.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Class RegisterManager. It deals with objects registering to events
 */
public class RegisterManager extends SubscriptionManager
{
   
   /** The Constant ALL_IDENTIFIERS. */
   private static final String ALL_IDENTIFIERS = null;

   /** The 'subscribers to all events' map. */
   private static List<Object> catchAllList = new ArrayList<>();

   /**
    * Register to all messages that we are waiting for in default context. This method search for
    * all @Listen(message="ID") or &#64;Listen(messages={"ID1", "ID2"...}) annotations. It also look
    * to all "onXXXXMessage" methods using XXXX as ID. Then it subscribes the object to all
    * corresponding IDs messages.
    *
    * @param object the object
    * @return the number of messages ID registered
    */
   public static final int register(Object object)
   {
      return register(DEFAULTCONTEXT, object);
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
    * @return the number of messages ID registered
    */
   public static final int register(Object context, Object object)
   {
      // Silly call
      if (object == null)
      {
         return 0;
      }
      // register to all identifiers
      return registerToMessages(context, object, ALL_IDENTIFIERS);
   }

   /**
    * Register to specific messages that we are waiting for in the context. This method search for
    * all @Listen(message="ID") or &#64;Listen(messages={"ID1", "ID2"...}) annotations with correct identifier.
    * It also look to all "onXXXXMessage" methods when XXXX is the identifier. Then it subscribes the object to 
    * corresponding ID messages.
    *
    * @param context the context
    * @param object the object
    * @param identifier the identifier or null for all
    * @return the number of messages ID registered
    */
   public static final int register(Object context, Object object, String identifier)
   {
      // Silly call
      if (object == null)
      {
         return 0;
      }
      return registerToMessages(context, object, identifier);
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
    * @param param the param
    */
   public static final void unregister(Object context, Object param)
   {
      // Silly call
      if (param == null)
      {
         return;
      }

      // Calculate context (in case of NULL context given).
      Object realContext = (context == null) ? DEFAULTCONTEXT : context;
      
      // Search in ids
      for (String id : getSubscribeMap().keySet())
      {
         // get map of concerned contexts
         Map<Object, List<Object>> contextMap = getSubscribeMap().get(id);
         
         // get subscribers in context
         List<Object> list = contextMap.get(realContext);
         if ((list != null) && (list.contains(param)))
         {
            // remove
            list.remove(param);
         }
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
    * Register as listener to messages.
    *
    * @param context the context
    * @param object the object
    * @param identifier the identifier or null for all
    * @return the number of messages ID registered
    */
   private static int registerToMessages(Object context, Object object, String identifier)
   {
      // get map of identifiers managed by object
      Map<String, Map<Class<?>, Method>> map = getMethods(object.getClass());
      
      // Loop on identifiers
      for (String id : map.keySet())
      {
         // test if object is concerned
         if ((id != "") && ((identifier == null) || (id.equals(identifier))))
         {
            // add subscriber
            addSubscription(context, id, object);
         }
      };
      return map.size();
   }
}
