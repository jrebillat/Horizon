package net.alantea.horizon.message.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.alantea.horizon.message.Message;

public class SendingManager extends RegisterManager
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

   /** The Constant DEFAULTINTERVAL. */
   private static final int DEFAULTINTERVAL = 250;

   /** The Constant MININTERVAL. */
   private static final int MININTERVAL = 50;

   /** The queued messages list. */
   private static ConcurrentLinkedQueue<Message> queuelist = new ConcurrentLinkedQueue<>();

   /** The interval between polling. */
   private static int interval = DEFAULTINTERVAL;

   /** The queue thread. */
   private static Thread queueThread;

   /** The message sending mode. */
   private static Mode mode = Mode.HYPERTHREADED;

   /**
    * Sets the interval between message list polling.
    *
    * @param value the new interval
    */
   public static final void setInterval(int value)
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
   public static final void setMode(Mode newMode)
   {
      mode = newMode;
   }

   /**
    * Send message to all subscribers.
    *
    * @param message the message
    */
   private static void sendToWorld(Message message)
   {
      Object context = (message.getContext() == null) ? DEFAULTCONTEXT : message.getContext();
      Map<Object, List<Object>> contextMap = getSubscribeMap().get(message.getIdentifier());
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
         List<Object> listeners = getListenermap().get(message.getSender());
         if (listeners != null)
         {
            listeners.forEach((listener) -> sendMessageToReceiver(message, listener));
         }
      }
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

   /**
    * Send immediately a single message.
    *
    * @param message the message
    */
   protected static void sendSingleMessage(Message message)
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
    * Send message.
    *
    * @param message the message
    */
   protected synchronized static final void internalSendMessage(Message message)
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
}
