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
public final class MessageManager
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
   private static ConcurrentLinkedQueue<Message> queuelist = new ConcurrentLinkedQueue<>();
   
   /** The interval between polling. */
   private static int interval = DEFAULTINTERVAL;
   
   /** The queue thread. */
   private static Thread queueThread;
   
   /** The subscribe map. */
   private static Map<String, List<Object>> subscribeMap = new HashMap<>();
   
   /** The listener map. */
   private static Map<Object, List<Object>> listenermap = new ConcurrentHashMap<>();
   
   /** The listener classes map. */
   private static Map<Class<? extends Object>, Map<String, Map<Class<?>, Method>>> classesmap = new ConcurrentHashMap<>();
   
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
            Message message;
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
    * Sets the mode.
    *
    * @param newMode the new mode
    */
   public static void setMode(Mode newMode)
   {
      mode = newMode;
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
   public static final void sendSychronousMessage(Message message)
   {
      sendSingleMessage(message);
   }
   
   /**
    * Send message.
    *
    * @param message the message
    */
   public static final void sendMessage(Message message)
   {
      if ((message == null))
      {
         return;
      }
      
      // if threaded, add the message to the queue
      if (mode == Mode.THREADED || mode == Mode.HYPERTHREADED)
      {
         if (queueThread == null)
         {
            // start queue if not already started
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
      sendSingleMessage(new Message(sender, receiver, id, content, conf));
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
      sendMessage(new Message(sender, receiver, id, content, conf));
   }
   
   /**
    * Send confidential message to a receiver.
    *
    * @param sender the sender
    * @param receiver the receiver
    * @param id the id
    * @param content the content
    */
   public static final void sendMessage(Object sender, Object receiver, String id, Object content)
   {
      sendMessage(new Message(sender, receiver, id, content, true));
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
      sendSingleMessage(new Message(sender, null, id, content, false));
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
      sendMessage(new Message(sender, null, id, content, false));
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
   private static void sendToWorld(Message message)
   {
      List<Object> list = subscribeMap.get(message.getIdentifier());
      if (list != null)
      {
         
         list.forEach((listener) -> {
            sendMessageToReceiver(message, listener);
         });
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
   private static void sendMessageToReceiver(Message message, Object receiver)
   {
      Class<?> theClass = receiver.getClass();

      Method method = getMethod(receiver.getClass(), message.getIdentifier(), message.getContent().getClass());

      if (method == null)
      {
         method = getMethod(receiver.getClass(), message.getIdentifier(), Message.class);
      }
      if (method == null)
      {
         String methodName = METHODHEADER + message.getIdentifier() + METHODFOOTER;
         try
         {
            method = theClass.getMethod(methodName, Message.class);
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
            method = theClass.getMethod(METHODHEADER + METHODFOOTER, Message.class);
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

   private static void sendToMethod(Method method, Object receiver, Message message)
   {
      try
      {
         method.setAccessible(true);
         if (method.getParameterTypes()[0].isAssignableFrom(Message.class))
         {
            method.invoke(receiver, message);
         }
         else if (method.getParameterTypes()[0].isAssignableFrom(message.getContent().getClass()))
         {
            method.invoke(receiver, message.getContent());
         }
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
    * @param param the parameter to search for
    * @return the method found or null
    */
   private static Method getMethod(Class<?> cl, String id, Class<?> param)
   {
      Map<String, Map<Class<?>, Method>> map = getMethods(cl);
      Map<Class<?>, Method> meths = map.get(id);
      if ((meths == null) && (! DEFAULTID.equals(id)))
      {
         return getMethod(cl, DEFAULTID, param);
      }
      else if (meths == null)
      {
         return null;
      }
      
      Method ret = meths.get(param);
      
      // search for parameter superclasses
      Class<?> superparam = param.getSuperclass();
      while ((ret == null) && (superparam != null) && ((!superparam.equals(Object.class))))
      {
         ret = getMethod(cl, id, superparam);
         superparam = superparam.getSuperclass();
      }
      
      // search for parameter interfaces (but just one level deep, don't get interfaces extended by interfaces)
      if ((ret == null))
      {
         superparam = param;
         while ((ret == null) && (superparam != null) && ((!superparam.equals(Object.class))))
         {
            Class<?>[] itfs = superparam.getInterfaces();
            int i = 0;
            while ((ret == null) && (i < itfs.length) && ((!itfs[i].equals(Object.class))))
            {
               ret = getMethod(cl, id, itfs[i]);
               i++;
            }
            superparam = superparam.getSuperclass();
         }
      }
      
      // test if method is for identifier
      if ((ret != null) && (ret.isAnnotationPresent(Listen.class)))
      {
         Listen annotation =  ret.getAnnotation(Listen.class);
         if (!id.equals(annotation.message()))
         {
            ret = null;
         }
      }
      
      // take default
      if ((ret == null) && (!DEFAULTID.equals(id)))
      {
         ret = getMethod(cl, DEFAULTID, param);
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
      Map<String, Map<Class<?>, Method>> map = getMethods(object);
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
   private static final Map<String, Map<Class<?>, Method>> getMethods(Object listener)
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

      Map<String, Map<Class<?>, Method>> methodmap = classesmap.get(theClass);
      if (methodmap == null)
      {
         methodmap = getSubMethodMap(theClass);
         classesmap.put(theClass,  methodmap);
      }
      return methodmap;
   }
   
   private static Map<String, Map<Class<?>, Method>> getSubMethodMap(Class<?> cl)
   {
         // Search for compatible methods
      Map<String, Map<Class<?>, Method>> methodmap = new ConcurrentHashMap<>();
      if (cl == null)
      {
         return methodmap;
      }
      if (!cl.equals(Object.class))
      {
         for (Class<?> cl1 : cl.getInterfaces())
         {
            methodmap.putAll(getSubMethodMap(cl1));
         }
         methodmap.putAll(getSubMethodMap(cl.getSuperclass()));
      }
         Method[] methods = cl.getDeclaredMethods();
         for (Method method : methods)
         {
            if (method.getParameterCount() == 1)
                //  && (method.getParameterTypes()[0].isAssignableFrom(HorizonMessage.class)))
            {
               if (method.isAnnotationPresent(Listen.class))
               {
                  Listen annotation =  method.getAnnotation(Listen.class);
                  if (!DEFAULTID.equals(annotation.message()))
                  {
                     method.setAccessible(true);
                     Map<Class<?>, Method> meths = methodmap.get(annotation.message());
                     if (meths == null)
                     {
                        meths = new HashMap<>();
                     }
                     meths.put(method.getParameterTypes()[0], method);
                     methodmap.put(annotation.message(), meths);
                  }
               }
               else if (method.getParameterTypes()[0].isAssignableFrom(Message.class))
               {
                  String name = method.getName();
                  if ((name.startsWith(METHODHEADER))
                        && (name.endsWith(METHODFOOTER)))
                  {
                     String id = name.substring(METHODHEADER.length(), name.length() - METHODFOOTER.length());
                     method.setAccessible(true);
                     Map<Class<?>, Method> meths = methodmap.get(id);
                     if (meths == null)
                     {
                        meths = new HashMap<>();
                     }
                     meths.put(method.getParameterTypes()[0], method);
                     methodmap.put(id, meths);
                  }
               }
            }
         }
         return methodmap;
   }

   /**
    * Send immediately a single message.
    *
    * @param message the message
    */
   private static void sendSingleMessage(Message message)
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
