package net.alantea.horizon.message.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscriptionManager extends ListenerManager
{

   /** The default context constant. */
   public static final String DEFAULTCONTEXT = "__Default C0ntext__";

   /** The "catch all" context constant. */
   public static final String ALLCONTEXTS = "__All C0ntexts__";

   /** The subscribe map. A map <Message identifier, Map<Context, List<subscribers>>> */
   private static Map<String, Map<Object, List<Object>>> subscribeMap = new HashMap<>();

   protected static Map<String, Map<Object, List<Object>>> getSubscribeMap()
   {
      return subscribeMap;
   }
   
   /**
    * Add a subscriber to a message type.
    *
    * @param context the context or null
    * @param id the id
    * @param subscriber the subscriber
    */
   public static final void addSubscription(Object context, String id, Object subscriber)
   {
      if ((id == null) || subscriber == null)
      {
         return;
      }
      Object realContext = (context == null) ? DEFAULTCONTEXT : context;
      
      Map<Object, List<Object>> contextMap = subscribeMap.get(id);
      if (contextMap == null)
      {
         contextMap = new HashMap<Object, List<Object>>();
         subscribeMap.put(id, contextMap);
      }
      List<Object> list = contextMap.get(realContext);
      if (list == null)
      {
         list = new ArrayList<Object>();
         contextMap.put(realContext, list);
      }
      if (!list.contains(subscriber))
      {
         list.add(subscriber);
      }
   }

   /**
    * remove a subscriber from a message type.
    *
    * @param id the id
    * @param subscriber the subscriber
    */
   public static final void removeSubscription(String id, Object subscriber)
   {
      removeSubscription(DEFAULTCONTEXT, id, subscriber);
   }

   /**
    * remove a subscriber from a message type.
    *
    * @param context the context or null
    * @param id the id
    * @param subscriber the subscriber
    */
   public static final void removeSubscription(Object context, String id, Object subscriber)
   {
      if ((id == null) || subscriber == null)
      {
         return;
      }
      Object realContext = (context == null) ? DEFAULTCONTEXT : context;
      
      Map<Object, List<Object>> contextMap = subscribeMap.get(id);
      if (contextMap == null)
      {
         return;
      }
      List<Object> list = contextMap.get(realContext);
      if ((list != null) && (list.contains(subscriber)))
      {
         list.remove(subscriber);
      }
   }

}
