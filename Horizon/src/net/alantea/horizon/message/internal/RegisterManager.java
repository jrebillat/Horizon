package net.alantea.horizon.message.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegisterManager extends SubscriptionManager
{

   /** The 'subscribers to all events' map. */
   private static List<Object> catchAllList = new ArrayList<>();

   /**
    * Register to all messages that we are waiting for. This method search for
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
      if (object == null)
      {
         return 0;
      }
      return registerToMessages(context, object);
   }

   /**
    * Register to all messages. This method search for all @Listen(message="ID") or
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
    * Unregister.
    *
    * @param param the param
    */
   public static final void unregister(Object param)
   {
      unregister(DEFAULTID, param);
   }

   /**
    * Unregister.
    *
    * @param context the context
    * @param param the param
    */
   public static final void unregister(Object context, Object param)
   {
      if (param == null)
      {
         return;
      }
      Object realContext = (context == null) ? DEFAULTCONTEXT : context;
      
      for (String id : getSubscribeMap().keySet())
      {
         Map<Object, List<Object>> contextMap = getSubscribeMap().get(id);
         List<Object> list = contextMap.get(realContext);
         if ((list != null) && (list.contains(param)))
         {
            list.remove(param);
         }
      }
   }

   /**
    * Unregister the object from getting all future messages.
    *
    * @param object the object
    */
   public static final void unregisterAllMessages(Object object)
   {
      if ((object != null) && (catchAllList.contains(object)))
      {
         catchAllList.remove(object);
      }
   }

   /**
    * Register as listener to messages.
    *
    * @param object the object
    * @param context the context
    * @return the number of messages ID registered
    */
   private static int registerToMessages(Object context, Object object)
   {
      Map<String, Map<Class<?>, Method>> map = getMethods(object.getClass());
      for (String id : map.keySet())
      {
         if (id != "")
         {
            addSubscription(context, id, object);
         }
      };
      return map.size();
   }

}
