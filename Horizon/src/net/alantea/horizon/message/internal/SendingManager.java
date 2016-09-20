package net.alantea.horizon.message.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.alantea.horizon.message.Message;

/**
 * The Class SendingManager. Deals with management of sending messages
 */
public class SendingManager extends RegisterManager
{
   /**
    * The Mode enum. Select mode of transmission. By default, the mode is preset (see mode field).
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

   /** The Constant DEFAULTINTERVAL. */
   private static final int DEFAULTINTERVAL = 250;

   /** The Constant MININTERVAL. */
   public static final int MININTERVAL = 50;

   /** The queued messages list. */
   private static ConcurrentLinkedQueue<Message> queuelist = new ConcurrentLinkedQueue<>();

   /** The interval between polling. */
   private static int interval = DEFAULTINTERVAL;

   /** The queue thread. */
   private static Thread queueThread;

   /** The message sending mode. */
   private static Mode mode = Mode.HYPERTHREADED;

   /**
    * Sets the interval between message list polling. This value must be positive and greater than MININTERVAL.
    *
    * @param value the new interval
    */
   public static final void setInterval(int value)
   {
      // Don't loop too fast
      if (value >= MININTERVAL)
      {
         interval = value;
      }
   }

   /**
    * Sets the threading mode. Note that this will not stop the queue thread, but it may not receive messages to send.
    *
    * @param newMode the new mode
    */
   public static final void setMode(Mode newMode)
   {
      // change mode
      mode = newMode;
   }

   /**
    * Send a message to all subscribers.
    *
    * @param message the message
    */
   private static void sendToWorld(Message message)
   {
      // Get context
      Object context = (message.getContext() == null) ? DEFAULTCONTEXT : message.getContext();

      // get subscription map for context
      Map<Object, List<Object>> specificContextMap = getSubscribeMap().get(message.getIdentifier());
      Map<Object, List<Object>> contextMap = null;
      Set<Object> set = null;

      // if there is something to do
      if (specificContextMap != null)
      {
         contextMap = new HashMap<>(specificContextMap);
         set = specificContextMap.keySet();
      }
      else
      {
         contextMap = new HashMap<>();
         set = contextMap.keySet();
      }


      // get subscription map for all identifiers
      Map<Object, List<Object>> allContextMap = getSubscribeMap().get("");
      if (allContextMap != null)
      {
         for (Object o : allContextMap.keySet())
         {
            if (! set.contains(o))
            {
               contextMap.put(o, allContextMap.get(o));
            }
         }
      }

      // Special case : do it for all contexts
      if (context.equals(ALLCONTEXTS))
      {
         // Loop on contexts
         for (Object ctx : contextMap.keySet())
         {
            // get listener list
            List<Object> list = contextMap.get(ctx);
            if (list != null)
            {
               // Loop on subscribers
               list.forEach((subscriber) -> {
                  // send message
                  sendMessageToReceiver(message, subscriber);
               });
            }
         }; 
      }
      else
      {
         // get listener list
         List<Object> list = contextMap.get(context);
         if (list != null)
         {
            Object[] objs = list.toArray();
            // Loop on subscribers
            for(Object subscriber : objs) 
            {
               // send message
               sendMessageToReceiver(message, subscriber);
            }
         }
      }

      if (message.getSender() != null)
      {
         // Send message to objects listening to this source
         List<Object> listeners = getListenermap().get(message.getSender());
         if (listeners != null)
         {
            // loop on listeners
            listeners.forEach((listener) -> sendMessageToReceiver(message, listener));
         }
      }
      
      // Loop on objects wanting to catch all messages
      getCatchAllList().forEach((listener) -> {
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
      // get method to use
      Method method = getMethod(receiver.getClass(), message.getIdentifier(), message.getContent().getClass());

      if (method != null)
      {
         if (mode == Mode.CONCURRENT || mode == Mode.HYPERTHREADED)
         {
            // Create a thread to launch the call
            final Method theMethod = method;
            new Thread(() -> sendToMethod(theMethod, receiver, message)).start();
         }
         else
         {
            // directly send
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
         // Ensure to be able to call method
         method.setAccessible(true);
         
         // Case : the method has a Message as argument
         if (method.getParameterTypes()[0].isAssignableFrom(Message.class))
         {
            method.invoke(receiver, message);
         }
         // Case : the method waits for a specific object corresponding to what is sent 
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
    * Start queue thread.
    */
   private static void startQueueThread()
   {
      queueThread = new Thread(() -> {
         while (true)
         {
            Message message;
            // Loop on messages in the queue
            do
            {
               // get next message in queue
               message = queuelist.poll();
               // send it
               sendSingleMessage(message);
            }
            while (message != null);
            
            // sleep a while
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
      
      // the queue is a daemon
      queueThread.setDaemon(true);
      queueThread.start();
   }

   /**
    * Send immediately a single message.
    *
    * @param message the message
    */
   protected static void sendSingleMessage(Message message)
   {
      // Silly call
      if (message == null)
      {
         return;
      }
      
      // Verify receiver
      if (message.getReceiver() != null)
      {
         // send
         sendMessageToReceiver(message, message.getReceiver());
      }
      
      // Non confidential messages are also sent to the world
      if (!message.isConfidential())
      {
         sendToWorld(message);
      }
   }

   /**
    * Really send a message.
    *
    * @param message the message
    */
   protected synchronized static final void internalSendMessage(Message message)
   {
      // Silly call
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

         // add mesage to queue
         synchronized (queuelist)
         {
            queuelist.add(message);
         }
      }
      else
      {
         // Just send
         sendSingleMessage(message);
      }
   }
}
