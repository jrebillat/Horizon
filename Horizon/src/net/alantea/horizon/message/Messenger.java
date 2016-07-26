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
      /** A thread is used to deliver messages in order they are generated. */
      THREADED,
      /** A thread is used to thread messages (one thread generating one thread per message). */
      HYPERTHREADED,
      /** Messages are threaded in order (one thread per message) from the main thread. */
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

   /** The Constant MININTERVAL. */
   private static final int MININTERVAL = 50;

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

   /** The subscribe map. A map <Message identifier, Map<Context, List<subscribers>>> */
   private static Map<String, Map<Object, List<Object>>> subscribeMap = new HashMap<>();

   /** The listener map. A map < message source, List<subscribers>>*/
   private static Map<Object, List<Object>> listenermap = new ConcurrentHashMap<>();

   /** The listener classes map. A map < listening class class, map <Message identifier, map <waited content, target method>>>*/
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
      if (value >= MININTERVAL)
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
      return register(DEFAULTCONTEXT, object);
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
   public static int register(Object context, Object object)
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
   public static void registerAllMessages(Object object)
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
   public static void unregister(Object param)
   {
      unregister(DEFAULTID, param);
   }

   /**
    * Unregister.
    *
    * @param context the context
    * @param param the param
    */
   public static void unregister(Object context, Object param)
   {
      if (param == null)
      {
         return;
      }
      Object realContext = (context == null) ? DEFAULTCONTEXT : context;
      
      for (String id : subscribeMap.keySet())
      {
         Map<Object, List<Object>> contextMap = subscribeMap.get(id);
         List<Object> list = contextMap.get(realContext);
         if (list.contains(param))
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
   public static void unregisterAllMessages(Object object)
   {
      if ((object != null) && (catchAllList.contains(object)))
      {
         catchAllList.remove(object);
      }
   }

   /**
    * Send immediately a synchronous message.
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
    * Send immediately a synchronous message to the world.
    *
    * @param sender the sender
    * @param id the id
    * @param content the content
    */
   public static final void sendSynchronousMessage(Object sender, String id, Object content)
   {
      sendSingleMessage(new Message(DEFAULTCONTEXT, sender, null, id, content, false));
   }

   /**
    * Send immediately a synchronous message to the world.
    *
    * @param context the context
    * @param sender the sender
    * @param id the id
    * @param content the context
    */
   public static final void sendSynchronousMessage(Object context, Object sender, String id, Object content)
   {
      sendSingleMessage(new Message(context, sender, null, id, content, false));
   }

   /**
    * Send immediately a synchronous message to a receiver.
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
      sendSingleMessage(new Message(DEFAULTCONTEXT, sender, receiver, id, content, conf));
   }

   /**
    * Send immediately a synchronous message to a receiver.
    *
    * @param context the context
    * @param sender the sender
    * @param receiver the receiver
    * @param id the id
    * @param content the context
    * @param conf the confidentiality
    */
   public static final void sendSynchronousMessage(Object context, Object sender, Object receiver, String id, Object content,
         boolean conf)
   {
      sendSingleMessage(new Message(context, sender, receiver, id, content, conf));
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
    * Send message to the world.
    *
    * @param sender the sender
    * @param id the id
    * @param content the content
    */
   public static final void sendMessage(Object sender, String id, Object content)
   {
      sendMessage(DEFAULTCONTEXT, sender, null, id, content, false);
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
      sendMessage(DEFAULTCONTEXT, sender, receiver, id, content, conf);
   }

   /**
    * Send message to the world.
    *
    * @param context the context
    * @param sender the sender
    * @param id the id
    * @param content the content
    */
   public static final void sendMessage(Object context, Object sender, String id, Object content)
   {
      sendMessage(context, sender, null, id, content, false);
   }

   /**
    * Send message to a receiver.
    *
    * @param context the context
    * @param sender the sender
    * @param receiver the receiver
    * @param id the id
    * @param content the content
    * @param conf the confidentiality
    */
   public static final void sendMessage(Object context, Object sender, Object receiver, String id, Object content, boolean conf)
   {
      sendMessage(new Message(context, sender, receiver, id, content, conf));
   }

   /**
    * Send message.
    *
    * @param message the message
    */
   public synchronized static final void sendMessage(Message message)
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
   public static void monitorProperty(ReadOnlyProperty<?> property, String identifier)
   {
      monitorProperty(DEFAULTCONTEXT, property, identifier);
   }
   
   /**
    *  Monitor a javafx property. Each change on property will trigger the given message type.
    * The property is set as source and the new value is given as content.
    *
    * @param context the context
    * @param property to be monitored.
    * @param identifier to use for sending messages.
    */
   public static void monitorProperty(Object context, ReadOnlyProperty<?> property, String identifier)
   {
      property.addListener((v, o, n) -> { sendMessage(context, property, identifier, n); });
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
      return getMethodRecursively(cl, id, param);
   }

   /**
    * Gets the method recursively.
    *
    * @param cl the cl
    * @param id the id
    * @param param the param
    * @return the method recursively
    */
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
         while ((ret == null) && (superparam != null))
         {
            ret = meths.get(superparam);
            if (!superparam.equals(Object.class))
            {
               superparam = superparam.getSuperclass();
            }
            else
            {
               superparam = null;
            }
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
      
      if ((ret == null) && (!param.equals(Message.class)))
      {
         ret = getMethodRecursively(cl, id, Message.class);
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

   /**
    * Parses the methods.
    *
    * @param theClass the the class
    * @return the methods
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
            methodmap.putAll(copyMethodsMap(cl1));
         }
         methodmap.putAll(copyMethodsMap(cl.getSuperclass()));
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
                  registerListeningMethod(annotation.message(), method, methodmap);
               }
               for( String id : annotation.messages())
               {
                  registerListeningMethod(id, method, methodmap);
               }
            }
            else if ((name.startsWith(METHODHEADER)) && (name.endsWith(METHODFOOTER)))
            {
                  String id = name.substring(METHODHEADER.length(), name.length() - METHODFOOTER.length());
                  registerListeningMethod(id, method, methodmap);
            }
         }
      }
      return methodmap;
   }
   
   private static Map<String, Map<Class<?>, Method>> copyMethodsMap(Class<?> subclass)
   {
      Map<String, Map<Class<?>, Method>> methodmap = new HashMap<>();
      
      // Warning : copy content, do not just link to it.
      Map<String, Map<Class<?>, Method>> supermap = getSubMethodMap(subclass);
      for(String key : supermap.keySet())
      {
         Map<Class<?>, Method> submap = supermap.get(key);
         Map<Class<?>, Method> meths = methodmap.get(key);
         if (meths == null)
         {
            meths = new HashMap<>();
         }
         for (Class<?> parmClass : submap.keySet())
         {
            meths.put(parmClass, submap.get(parmClass));
         }
         methodmap.put(key, meths);
      }
      return methodmap;
   }
   
   /**
    * Register listening method.
    *
    * @param key the key
    * @param method the method
    * @param methodmap the methodmap
    */
   private static void registerListeningMethod(String key, Method method, Map<String, Map<Class<?>, Method>> methodmap)
   {
      method.setAccessible(true);
      Map<Class<?>, Method> meths = methodmap.get(key);
      if (meths == null)
      {
         meths = new HashMap<>();
      }
      meths.put(method.getParameterTypes()[0], method);
      methodmap.put(key, meths);
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
