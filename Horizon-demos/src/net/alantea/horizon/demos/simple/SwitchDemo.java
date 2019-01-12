package net.alantea.horizon.demos.simple;

import net.alantea.horizon.message.Message;
import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.message.Mode;

/**
 * Simple basic messaging demonstration. The Listener instance is waiting for a message (any kind of message)
 * and is happy to receive one.
 */
public class SwitchDemo
{
   /**
    * The main method.
    *
    * @param args the arguments
    * @throws InterruptedException the interrupted exception
    */
   public static void main(String[] args) throws InterruptedException
   {
      // This is for demo purpose : if Messenger is threaded, the program ends before the message is dispatched.
      Messenger.setMode(Mode.SYNCHRONOUS);
      
      // Create and register listener
      Listener listener = new Listener();
      Messenger.register(listener);
      
      // Send a message
      Messenger.sendMessage(null, "SimpleTest", "Text content");
      Messenger.sendMessage(null, "Anything", 256);
      Messenger.sendMessage(null, "NeverMind", listener);
   }
   
   /**
    * Listener waiting for a message.
    */
   private static class Listener
   {
      /**
       * What to do on message reception.
       *
       * @param message the message
       */
      @SuppressWarnings("unused")
      private void onMessage(String content)
      {
         System.out.println("Got text message : " + content);
      }
      
      /**
       * What to do on message reception.
       *
       * @param message the message
       */
      @SuppressWarnings("unused")
      private void onMessage(Integer content)
      {
         System.out.println("Got integer message : " + content);
      }
      
      /**
       * What to do on message reception.
       *
       * @param message the message
       */
      @SuppressWarnings("unused")
      private void onMessage(Message message)
      {
         System.out.println("Got unknown message content.");
      }
   }

}
