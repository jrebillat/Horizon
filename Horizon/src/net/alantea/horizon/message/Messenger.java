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

import javafx.beans.property.ReadOnlyProperty;

/**
 * The Class Messenger.
 */
public final class Messenger
{

   /**
    * The Enum Mode.
    */
   public enum Mode
   {
      /** A thread is used to deliver messages in the order they are generated. */
      THREADED,
      /** A thread is used to thread messages (one thread per message). */
      HYPERTHREADED,
      /** Messages are threaded (one thread per message) from the main thread. */
      CONCURRENT,
      /** Messages are processed in order from the main thread. */
      SYNCHRONOUS
   };

   /** The "catch all" message constant. */
   public static final String DEFAULTID = "";

   /** The default context constant. */
   public static final String DEFAULTCONTEXT = "__Default C0ntext__";

   /** The "catch all" context constant. */
   public static final String ALLCONTEXTS = "__All C0ntexts__";

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

   /** The 'subscribers to all events' map. */
   private static List<Object> catchAllList = new ArrayList<>();

   /** The subscribe map. */
   private static Map<String, Map<Object, List<Object>>> subscribeMap = new HashMap<>();

   /** The listener map. */
   private static Map<Object, List<Object>> listenermap = new ConcurrentHashMap<>();

   /** The listener classes map. */
   private static Map<Class<? extends Object>, Map<String, Map<Class<?>, Method>>> classesmap = new ConcurrentHashMap<>();

   /** The message sending mode. */
   private static Mode mode = Mode.HYPERTHREADED;


   /**
    * Sets the interval between message list polling.
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
    * Sets the threading mode.
    *
    * @param newMode the new mode
    */
   public static void setMode(Mode newMode)
   {
      mode = newMode;
   }

   /**
    * Register to all messages that we are waiting for. This method search for
    * all @Listen(message="ID") or &#64;Listen(messages={"ID1", "ID2"...}) annotations. It also look
    * to all "onXXXXMessage" methods using XXXX as ID. Then it subscribes the object to all
    * corresponding IDs messages.
    *
    * @param object the object
    * @return the number of messages ID registered
    */
   public static int register(Object object)
   {
      return register(object, ALLCONTEXTS);
   }

   /**
    * Register to all messages that we are waiting for in the context. This method search for
    * all @Listen(message="ID") or &#64;Listen(messages={"ID1", "ID2"...}) annotations. It also look
    * to all "onXXXXMessage" methods using XXXX as ID. Then it subscribes the object to all
    * corresponding IDs messages.
    *
    * @param object the object
    * @param object the object
    * @return the number of messages ID registered
    */
   public static int register(Object object, Object context)
   {
      if (object == null)
      {
         return 0;
      }
      return registerToMessages(object, context);
   }

   /**
    * Register to all messages. This method search for all @Listen(message="ID") or
    * &#64;Listen(messages={"ID1", "ID2"...}) annotations. It also look to all "onXXXXMessage"
    * methods using XXXX as ID. Then it subscribes the object to all corresponding IDs messages. It
    * is using "onMessage" method for all other messages.
    *
    * @param object the object
    */
   public static void registerAllMessages(Object object)
   {
      if ((object != null) && (!catchAllList.contains(object)))
      {
         catchAllList.add(object);
      }
   }

   /**
    * Unregister the object from getting all future messages.
    *
    * @param object the object
    */
   public static void unregisterAllMessages(Object object)
   {
      if ((object != null) && (catchAllList.contains(object)))
      {
         catchAllList.remove(object);
      }
   }

   /**
    * Send synchronous message.
    *
    * @param message the message
    */
   public static final void sendSynchronousMessage(Message message)
   {
      if (message != null)
      {
         sendSingleMessage(message);
      }
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
      sendSingleMessage(new Message(sender, null, id, content, null, false));
   }

   /**
    * Send synchronous message to the world.
    *
    * @param sender the sender
    * @param id the id
    * @param content the content
    * @param content the context
    */
   public static final void sendSynchronousMessage(Object sender, String id, Object content, Object context)
   {
      sendSingleMessage(new Message(sender, null, id, content, context, false));
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
   public static final void sendSynchronousMessage(Object sender, Object receiver, String id, Object content,
         boolean conf)
   {
      sendSingleMessage(new Message(sender, receiver, id, content, null, conf));
   }

   /**
    * Send synchronous message to a receiver.
    *
    * @param sender the sender
    * @param receiver the receiver
    * @param id the id
    * @param content the content
    * @param content the context
    * @param conf the confidentiality
    */
   public static final void sendSynchronousMessage(Object sender, Object receiver, String id, Object content,
         Object context, boolean conf)
   {
      sendSingleMessage(new Message(sender, receiver, id, content, context, conf));
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
      sendMessage(sender, null, id, content, null, false);
   }

   /**
    * Send confidential message to a receiver.
    *
    * @param sender the sender
    * @param receiver the receiver
    * @param id the id
    * @param content the content
    */
   public static final void sendConfidentialMessage(Object sender, Object receiver, String id, Object content)
   {
      sendMessage(sender, receiver, id, content, true);
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
      sendMessage(sender, receiver, id, content, null, conf);
   }

   /**
    * Send message to the world.
    *
    * @param sender the sender
    * @param id the id
    * @param content the content
    * @param context the context
    */
   public static final void sendMessage(Object sender, String id, Object content, Object context)
   {
      sendMessage(sender, null, id, content, context, false);
   }

   /**
    * Send message to a receiver.
    *
    * @param sender the sender
    * @param receiver the receiver
    * @param id the id
    * @param content the content
    * @param context the context
    * @param conf the confidentiality
    */
   public static final void sendMessage(Object sender, Object receiver, String id, Object content, Object context, boolean conf)
   {
      sendMessage(new Message(sender, receiver, id, content, context, conf));
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

         synchronized (queuelist)
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
    * Add a subscriber to a message type.
    *
    * @param id the id
    * @param subscriber the subscriber
    * @param context the context or null
    */
   public static final void addSubscription(String id, Object subscriber, Object context)
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
      removeSubscription(id, null, subscriber);
   }

   /**
    * remove a subscriber from a message type.
    *
    * @param id the id
    * @param context the context or null
    * @param subscriber the subscriber
    */
   public static final void removeSubscription(String id, Object context, Object subscriber)
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
   
   /** Monitor a javafx property. Each change on property will trigger the given message type.
    * The property is set as source and the new value is given as content.
    * @param property to be monitored.
    * @param identifier to use for sending messages.
    */
   public void monitorProperty(ReadOnlyProperty<?> property, String identifier)
   {
      property.addListener((v, o, n) -> { sendMessage(property, identifier, n); });
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
         sendMessageToReceiver(message, message.getReceiver());
      }
      if (!message.isConfidential())
      {
         sendToWorld(message);
      }
   }

   /**
    * Send message to all subscribers.
    *
    * @param message the message
    */
   private static void sendToWorld(Message message)
   {
      Object context = (message.getContext() == null) ? DEFAULTCONTEXT : message.getContext();
      Map<Object, List<Object>> contextMap = subscribeMap.get(message.getIdentifier());
      if (contextMap != null)
      {
         if (context.equals(ALLCONTEXTS))
         {
            contextMap.keySet().forEach((ctx) -> {
               List<Object> list = contextMap.get(ctx);
               if (list != null)
               {
                  list.forEach((listener) -> {
                     sendMessageToReceiver(message, listener);
                  });
               }
            }); 
         }
         else
         {
            List<Object> list = contextMap.get(context);
            if (list != null)
            {
               list.forEach((listener) -> {
                  sendMessageToReceiver(message, listener);
               });
            }
         }
      }
      if (message.getSender() != null)
      {
         List<Object> listeners = listenermap.get(message.getSender());
         if (listeners != null)
         {
            listeners.forEach((listener) -> sendMessageToReceiver(message, listener));
         }
      }
      catchAllList.forEach((listener) -> {
         sendMessageToReceiver(message, listener);
      });
   }

   /**
    * Send message to a receiver.
    *
    * @param message the message
    * @param receiver the receiver
    */
   private static void sendMessageToReceiver(Message message, Object receiver)
   {
      Method method = getMethod(receiver.getClass(), message.getIdentifier(), message.getContent().getClass());

      if (method == null)
      {
         method = getMethod(receiver.getClass(), message.getIdentifier(), Message.class);
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

   /**
    * Send to method.
    *
    * @param method the method
    * @param receiver the receiver
    * @param message the message
    */
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
      Method ret = getMethodRecursively(cl, id, param);

      // take default
      if (ret == null)
      {
         ret = getMethodRecursively(cl, DEFAULTID, param);
      }
      
      return ret;
   }

   private static Method getMethodRecursively(Class<?> cl, String id, Class<?> param)
   {
      Map<String, Map<Class<?>, Method>> map = getMethods(cl);
      Map<Class<?>, Method> meths = map.get(id);
      if ((meths == null) && (!DEFAULTID.equals(id)))
      {
         return getMethodRecursively(cl, DEFAULTID, param);
      }

      Method ret = null;
      if (meths != null)
      {

         ret = meths.get(param);

         // search for parameter superclasses
         Class<?> superparam = param.getSuperclass();
         while ((ret == null) && (superparam != null) && ((!superparam.equals(Object.class))))
         {
            ret = getMethodRecursively(cl, id, superparam);
            superparam = superparam.getSuperclass();
         }

         // search for parameter interfaces (but just one level deep,
         // don't get interfaces extended by interfaces)
         if ((ret == null))
         {
            superparam = param;
            while ((ret == null) && (superparam != null) && ((!superparam.equals(Object.class))))
            {
               Class<?>[] itfs = superparam.getInterfaces();
               int i = 0;
               while ((ret == null) && (i < itfs.length) && ((!itfs[i].equals(Object.class))))
               {
                  ret = getMethodRecursively(cl, id, itfs[i]);
                  i++;
               }
               superparam = superparam.getSuperclass();
            }
         }
      }
      return ret;
   }

   /**
    * Register as listener to messages.
    *
    * @param object the object
    * @param context the context
    * @return the number of messages ID registered
    */
   private static int registerToMessages(Object object, Object context)
   {
      Map<String, Map<Class<?>, Method>> map = getMethods(object.getClass());
      for (String id : map.keySet())
      {
         if (id != "")
         {
            addSubscription(id, object, context);
         }
      };
      return map.size();
   }

   /**
    * Parses the methods.
    *
    * @param listener the listener
    */
   private static final Map<String, Map<Class<?>, Method>> getMethods(Class<?> theClass)
   {
      Map<String, Map<Class<?>, Method>> methodmap = getSubMethodMap(theClass);
      classesmap.put(theClass, methodmap);
      return methodmap;
   }

   /**
    * Gets the sub method map.
    *
    * @param cl the cl
    * @return the sub method map
    */
   private static Map<String, Map<Class<?>, Method>> getSubMethodMap(Class<?> cl)
   {
      // Test silly call
      if (cl == null)
      {
         return new ConcurrentHashMap<>();
      }

      // Test if we already have done the work.
      Map<String, Map<Class<?>, Method>> methodmap = classesmap.get(cl);
      if (methodmap != null)
      {
         return methodmap;
      }

      // Search for compatible methods in interfaces (for default methods) and superclass.
      methodmap = new ConcurrentHashMap<>();
      if (!cl.equals(Object.class))
      {
         for (Class<?> cl1 : cl.getInterfaces())
         {
            methodmap.putAll(getSubMethodMap(cl1));
         }
         methodmap.putAll(getSubMethodMap(cl.getSuperclass()));
      }

      // Get methods declared in class itself
      Method[] methods = cl.getDeclaredMethods();
      for (Method method : methods)
      {
         if (method.getParameterCount() == 1)
         {
            String name = method.getName();
            // Method with @Listen
            if (method.isAnnotationPresent(Listen.class))
            {
               Listen annotation = method.getAnnotation(Listen.class);
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
            else if ((name.startsWith(METHODHEADER)) && (name.endsWith(METHODFOOTER)))
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
      return methodmap;
   }
   
   /**
    * Start queue thread.
    */
   private static void startQueueThread()
   {
      queueThread = new Thread(() -> {
         while (true)
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
}
