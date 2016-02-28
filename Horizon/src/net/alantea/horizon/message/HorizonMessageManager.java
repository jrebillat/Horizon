package net.alantea.horizon.message;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The Class HorizonMessageManager.
 */
public final class HorizonMessageManager
{
   /** The Constant DEFAULTINTERVAL. */
   private static final int DEFAULTINTERVAL = 250;
   
   /** The queued messages list. */
   private static ConcurrentLinkedQueue<HorizonMessage> queuelist = new ConcurrentLinkedQueue<>();
   
   /** The interval between polling. */
   private static int interval = DEFAULTINTERVAL;
   
   /** The queue thread. */
   private static Thread queueThread;
   
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
               if (message != null)
               {
                  reallySendMessage(message);
               }
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
    * Send message.
    *
    * @param message the message
    */
   static final void sendMessage(HorizonMessage message)
   {
      if ((message == null) || (message.getReceiver() == null))
      {
         return;
      }
      
      if (queueThread == null)
      {
         startQueueThread();
      }
      
      synchronized(queuelist)
      {
         queuelist.add(message);
      }
   }
   
   /**
    * Send message.
    *
    * @param sender the sender
    * @param receiver the receiver
    * @param id the id
    * @param content the content
    */
   static final void sendMessage(HorizonSubscriber sender, HorizonSubscriber receiver, String id, Object content)
   {
      sendMessage(new HorizonMessage(sender, receiver, id, content));
   }
   
   /**
    * Really send message.
    *
    * @param message the message
    */
   private static void reallySendMessage(HorizonMessage message)
   {
      HorizonSubscriber receiver = message.getReceiver();
      Class<? extends HorizonSubscriber> theClass = receiver.getClass();
      String methodName = "on" + message.getIdentifier() + "Message";
      Method method = null;
      try
      {
         method = theClass.getMethod(methodName, HorizonMessage.class);
      }
      catch (NoSuchMethodException | SecurityException e)
      {
         // No problemo !
      }
      if (method == null)
      {
         message.getReceiver().onMessage(message);
      }
      else
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
   }
}
