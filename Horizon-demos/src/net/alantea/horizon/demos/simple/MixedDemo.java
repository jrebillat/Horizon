package net.alantea.horizon.demos.simple;

import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.message.Mode;
import net.alantea.horizon.message.Receive;

/**
 * Simple basic messaging demonstration. The Listener instance is waiting for a message (any kind of message)
 * and is happy to receive one.
 */
public class MixedDemo
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
      Messenger.sendMessage(null, "First", "Text content");
      Messenger.sendMessage(null, "Second", "Another text");
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
      @Receive(message="First")
      private void manage1(String content)
      {
         System.out.println("Got first message : " + content);
      }
      
      /**
       * What to do on message reception.
       *
       * @param message the message
       */
      @SuppressWarnings("unused")
      private void onSecondMessage(String content)
      {
         System.out.println("Got second message : " + content);
         Messenger.sendMessage(null, "Third", 69);
         Messenger.sendMessage(null, "Third", "Final text");
      }
      
      /**
       * What to do on message reception.
       *
       * @param message the message
       */
      @Receive(message="Third")
      private void manage3(String content)
      {
         System.out.println("Got third text message : " + content);
      }
      
      /**
       * What to do on message reception.
       *
       * @param message the message
       */
      @Receive(message="Third")
      private void manage3(Integer content)
      {
         System.out.println("Got third integer message : " + content);
      }
   }

}
