package net.alantea.horizon.message.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class SubscriptionManager, to manage objects that subscribe to one or several identifiers in one or several contexts.
 * It contains the methods to manage the subscription process for identifiers and contexts.
 */
public class SubscriptionManager
{

   /** The default context constant. */
   public static final String DEFAULTCONTEXT = "__Default C0ntext__";

   /** The subscribe map. A map <Message identifier, Map<Context, List<subscribers>>> */
   private static Map<String, Map<Object, List<Object>>> subscribeMap = new HashMap<>();
   
   /**
    * Add a subscriber to a message type in the default context.
    *
    * @param identifier the identifier
    * @param subscriber the subscriber
    */
   public static final void addSubscription(String identifier, Object subscriber)
   {
      addSubscription(DEFAULTCONTEXT, identifier, subscriber);
   }
   
   /**
    * Add a subscriber to a message type in a context.
    *
    * @param context the context or null
    * @param identifier the identifier
    * @param subscriber the subscriber
    */
   public static final void addSubscription(Object context, String identifier, Object subscriber)
   {
      // Silly cases
      if ((identifier == null) || subscriber == null)
      {
         return;
      }
      
      // Calculate context (in case of NULL context given).
      Object realContext = (context == null) ? DEFAULTCONTEXT : context;
      
      // get map of registered subscribers in contexts for the identifier
      Map<Object, List<Object>> contextMap = subscribeMap.get(identifier);
      
      // First time : get null
      if (contextMap == null)
      {
         // create map
         contextMap = new HashMap<Object, List<Object>>();
         // add it to global map
         subscribeMap.put(identifier, contextMap);
      }
      
      // get list of subscribers in the given context
      List<Object> list = contextMap.get(realContext);
      
      // First time : get null
      if (list == null)
      {
         // create list
         list = new ArrayList<Object>();
         // add it to map
         contextMap.put(realContext, list);
      }
      
      // do not add twice
      if (!list.contains(subscriber))
      {
         // add subscriber
         list.add(subscriber);
      }
   }

   /**
    * remove a subscriber from a message type.
    *
    * @param identifier the identifier
    * @param subscriber the subscriber
    */
   public static final void removeSubscription(String identifier, Object subscriber)
   {
      removeSubscription(DEFAULTCONTEXT, identifier, subscriber);
   }

   /**
    * remove a subscriber from a message type.
    *
    * @param context the context or null
    * @param identifier the identifier
    * @param subscriber the subscriber
    */
   public static final void removeSubscription(Object context, String identifier, Object subscriber)
   {
      // Silly cases
      if ((identifier == null) || subscriber == null)
      {
         return;
      }

      // Calculate context (in case of NULL context given).
      Object realContext = (context == null) ? DEFAULTCONTEXT : context;

      // get map of registered subscribers in contexts for the identifier
      Map<Object, List<Object>> contextMap = subscribeMap.get(identifier);

      // If got null
      if (contextMap == null)
      {
         return;
      }
      // get list of subscribers in the given context
      List<Object> list = contextMap.get(realContext);
      
      // Verify subscription
      if (list.contains(subscriber))
      {
         // remove it
         list.remove(subscriber);
      }
   }
   
   /**
    * Gets the subscribers.
    *
    * @param identifier the identifier
    * @return the subscribers
    */
   protected static List<Object> getSubscribers(String identifier)
   {
      return getSubscribers(DEFAULTCONTEXT, identifier);
   }
   
   /**
    * Gets the subscribers for and identifier in a context.
    *
    * @param context the context
    * @param identifier the identifier
    * @return the subscribers
    */
   protected static List<Object> getSubscribers(Object context, String identifier)
   {
      List<Object> ret = ListenerManager.EMPTYLIST;
      
      if (identifier != null)
      {
         // Calculate context (in case of NULL context given).
         Object realContext = (context == null) ? DEFAULTCONTEXT : context;

         // get map of registered subscribers in contexts for the identifier
         Map<Object, List<Object>> contextMap = subscribeMap.get(identifier);
         
         // If got null
         if (contextMap != null)
         {
            // get list of subscribers in the given context
            ret = contextMap.get(realContext);
         }
         else
         {
            ret = ListenerManager.EMPTYLIST;
         }
      }

      return ret;
   }
}
