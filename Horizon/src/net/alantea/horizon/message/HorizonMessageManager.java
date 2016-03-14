package net.alantea.horizon.message;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The Class HorizonMessageManager.
 */
public final class HorizonMessageManager
{
   
   /**
    * The Enum Mode.
    */
   public enum Mode { THREADED, HYPERTHREADED, CONCURRENT, SYNCHRONOUS };
   
   /** The "catch all" message constant. */
   public static final String DEFAULTID = "";
   
   /** The Constant DEFAULTINTERVAL. */
   private static final int DEFAULTINTERVAL = 250;
   
   /** The Constant method header. */
   private static final String METHODHEADER = "on";
   
   /** The Constant method footer. */
   private static final String METHODFOOTER = "Message";
   
   /** The queued messages list. */
   private static ConcurrentLinkedQueue<HorizonMessage> queuelist = new ConcurrentLinkedQueue<>();
   
   /** The interval between polling. */
   private static int interval = DEFAULTINTERVAL;
   
   /** The queue thread. */
   private static Thread queueThread;
   
   /** The subscribe map. */
   private static Map<String, List<Object>> subscribeMap = new HashMap<>();
   
   /** The listener map. */
   private static Map<Object, List<Object>> listenermap = new ConcurrentHashMap<>();
   
   /** The listener classes map. */
   private static Map<Class<? extends Object>, Map<String, Method>> classesmap = new ConcurrentHashMap<>();
   
   /** The mesage sending mode. */
   private static Mode mode = Mode.HYPERTHREADED;
   
   /**
    * Start queue thread.
    */
   private static void startQueueThread()
   {
      queueThread = new Thread(() ->
      {
         while(true)
         {
            HorizonMessage message;
            do
            {
               message = queuelist.poll();
               sendSingleMessage(message);
            }
            while (message != null);
            try
            {
               Thread.sleep(interval);
            }
            catch (Exception e)
            {
               // really : don't care !
            }
         }
      });
      queueThread.setDaemon(true);
      queueThread.start();
   }

   /**
    * Sets the interval.
    *
    * @param value the new interval
    */
   public static void setInterval(int value)
   {
      if (value > 0)
      {
         interval = value;
      }
   }
   
   /**
    * Register to all messages that we are waiting for. This method search for all @Listen(message="ID") or
    * @Listen(messages={"ID1", "ID2"...}) annotations. It also look to all "onXXXXMessage" methods using
    * XXXX as ID. Then it subscribes the object to all corresponding IDs messages.
    *
    * @param object the object
    * @return the number of messages ID registered
    */
   public static int register(Object object)
   {
      if (object == null)
      {
         return 0;
      }
      return registerToMessages(object);
   }
   
   /**
    * Send synchronous message.
    *
    * @param message the message
    */
   public static final void sendSychronousMessage(HorizonMessage message)
   {
      sendSingleMessage(message);
   }
   
   /**
    * Send message.
    *
    * @param message the message
    */
   public static final void sendMessage(HorizonMessage message)
   {
      if ((message == null))
      {
         return;
      }
      
      if (mode == Mode.THREADED || mode == Mode.HYPERTHREADED)
      {
         if (queueThread == null)
         {
            startQueueThread();
         }
      
         synchronized(queuelist)
         {
            queuelist.add(message);
         }
      }
      else
      {
         sendSingleMessage(message);
      }
   }
   
   /**
    * Send synchronous message to a receiver.
    *
    * @param sender the sender
    * @param receiver the receiver
    * @param id the id
    * @param content the content
    * @param conf the confidentiality
    */
   public static final void sendSynchronousMessage(Object sender, Object receiver, String id, Object content, boolean conf)
   {
      sendSingleMessage(new HorizonMessage(sender, receiver, id, content, conf));
   }
   
   /**
    * Send message to a receiver.
    *
    * @param sender the sender
    * @param receiver the receiver
    * @param id the id
    * @param content the content
    * @param conf the confidentiality
    */
   public static final void sendMessage(Object sender, Object receiver, String id, Object content, boolean conf)
   {
      sendMessage(new HorizonMessage(sender, receiver, id, content, conf));
   }
   
   /**
    * Send synchronous message to the world.
    *
    * @param sender the sender
    * @param id the id
    * @param content the content
    */
   public static final void sendSynchronousMessage(Object sender, String id, Object content)
   {
      sendSingleMessage(new HorizonMessage(sender, null, id, content, false));
   }
   
   /**
    * Send message to the world.
    *
    * @param sender the sender
    * @param id the id
    * @param content the content
    */
   public static final void sendMessage(Object sender, String id, Object content)
   {
      sendMessage(new HorizonMessage(sender, null, id, content, false));
   }
   
   /**
    * Add a subscriber to a message type.
    *
    * @param id the id
    * @param subscriber the subscriber
    */
   public static final void addSubscription(String id, Object subscriber)
   {
      if ((id == null) || subscriber == null)
      {
         return;
      }
      List<Object> list = subscribeMap.get(id);
      if (list == null)
      {
         list = new ArrayList<Object>();
         subscribeMap.put(id, list);
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
      if ((id == null) || subscriber == null)
      {
         return;
      }
      List<Object> list = subscribeMap.get(id);
      if ((list != null) && (list.contains(subscriber)))
      {
         list.remove(subscriber);
      }
   }
   
   /**
    * Adds a horizon listener to a specific source.
    *
    * @param horizonSource the horizon source
    * @param listener the listener
    */
   public static final boolean addHorizonListener(Object horizonSource, Object listener)
   {
      if ((horizonSource == null) || (listener == null))
      {
         return false;
      }
      
      synchronized(listenermap)
      {
         List<Object> listeners = listenermap.get(horizonSource);
         
         if (listeners == null)
         {
            listeners = new CopyOnWriteArrayList<>();
            listenermap.put(horizonSource, listeners);
         }
         if (!listeners.contains(listener))
         {
            listeners.add(listener);
            getMethods(listener);
         }
      }
      return true;
   }

   /**
    * Removes a horizon listener from a specific source.
    *
    * @param horizonSource the horizon source
    * @param listener the listener
    */
   public static final boolean removeHorizonListener(Object horizonSource, Object listener)
   {
      synchronized(listenermap)
      {
         List<Object> listeners = listenermap.get(horizonSource);
         
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
    * @param horizonSource the horizon source
    */
   public static final boolean removeAllHorizonListeners(Object horizonSource)
   {
      synchronized(listenermap)
      {
         List<Object> listeners = listenermap.get(horizonSource);
         
         if (listeners != null)
         {
            listeners.clear();
            return true;
         }
      }
      return false;
   }

   /**
    * Send message to all subscribers.
    *
    * @param message the message
    */
   private static void sendToWorld(HorizonMessage message)
   {
      List<Object> list = subscribeMap.get(message.getIdentifier());
      if (list != null)
      {
         list.forEach((listener) -> sendMessageToReceiver(message, listener));
      }
      if (message.getSender() != null)
      {
         List<Object> listeners = listenermap.get(message.getSender());
         if (listeners != null)
         {
            listeners.forEach((listener) -> sendMessageToReceiver(message, listener));
         }
      }
   }
   
   /**
    *  Send message to a receiver.
    *
    * @param message the message
    * @param receiver the receiver
    */
   private static void sendMessageToReceiver(HorizonMessage message, Object receiver)
   {
      Class<?> theClass = receiver.getClass();

      Method method = getMethod(receiver.getClass(), message.getIdentifier());

      if (method == null)
      {
         String methodName = METHODHEADER + message.getIdentifier() + METHODFOOTER;
         try
         {
            method = theClass.getMethod(methodName, HorizonMessage.class);
         }
         catch (NoSuchMethodException | SecurityException e)
         {
            // No problemo !
         }
      }
      if (method == null)
      {
         try
         {
            method = theClass.getMethod(METHODHEADER + METHODFOOTER, HorizonMessage.class);
         }
         catch (NoSuchMethodException | SecurityException e)
         {
            // Do not care
         }
      }

      if (method != null)
      {
         if (mode == Mode.CONCURRENT || mode == Mode.HYPERTHREADED)
         {
            final Method theMethod = method;
            new Thread(() -> sendToMethod(theMethod, receiver, message)).start();
         }
         else
         {
            sendToMethod(method, receiver, message);
         }
      }
   }

   private static void sendToMethod(Method method, Object receiver, HorizonMessage message)
   {
      try
      {
         method.setAccessible(true);
         method.invoke(receiver, message);
      }
      catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
      {
         e.printStackTrace();
         // it is a pity...
      }
   }

   /**
    * Gets the method.
    *
    * @param cl the cl class to parse
    * @param id the id identifier searched for
    * @return the method found or null
    */
   private static Method getMethod(Class<?> cl, String id)
   {
      Map<String, Method> map = getMethods(cl);
      Method ret = map.get(id);
      
      if ((ret == null) && (!DEFAULTID.equals(id)))
      {
         ret = getMethod(cl, DEFAULTID);
      }
      return ret;
   }
   
   /**
    * Register as listener to messages.
    *
    * @param object the object
    * @return the number of messages ID registered
    */
   private static int registerToMessages(Object object)
   {
      Map<String, Method> map = getMethods(object);
      map.forEach((id, meth) ->
      {
         addSubscription(id, object);
      });
      return map.size();
   }
   
   /**
    * Parses the methods.
    *
    * @param listener the listener
    */
   private static final Map<String, Method> getMethods(Object listener)
   {
      Class<?> theClass;
      if (listener instanceof Class<?>)
      {
         theClass = (Class<?>) listener;
      }
      else
      {
         theClass = listener.getClass();
      }

      Map<String, Method> methodmap = classesmap.get(theClass);
      if (methodmap == null)
      {
         // Search for compatible methods
         methodmap = new ConcurrentHashMap<>();
         Method[] methods = theClass.getMethods();
         for (Method method : methods)
         {
            if ((method.getParameterCount() == 1)
                  && (method.getParameterTypes()[0].isAssignableFrom(HorizonMessage.class)))
            {
               if (method.isAnnotationPresent(Listen.class))
               {
                  Listen annotation =  method.getAnnotation(Listen.class);
                  if (!DEFAULTID.equals(annotation.message()))
                  {
                     methodmap.put(annotation.message(), method);
                  }
               }
               else 
               {
                  String name = method.getName();
                  if ((name.startsWith(METHODHEADER))
                        && (name.endsWith(METHODFOOTER)))
                  {
                     String id = name.substring(METHODHEADER.length(), name.length() - METHODFOOTER.length());
                     methodmap.put(id, method);
                  }
               }
            }
         }
         classesmap.put(theClass,  methodmap);
      }
      return methodmap;
   }
   
   /**
    * Send immediately a single message.
    *
    * @param message the message
    */
   private static void sendSingleMessage(HorizonMessage message)
   {
      if (message == null)
      {
         return;
      }
      
      if (message.getReceiver() != null)
      {
         sendMessageToReceiver(message,  message.getReceiver());
      }
      if (! message.isConfidential())
      {
         sendToWorld(message);
      }
   }
}
