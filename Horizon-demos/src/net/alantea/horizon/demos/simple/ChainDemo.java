package net.alantea.horizon.demos.simple;

import net.alantea.horizon.message.Message;
import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.message.Mode;

/**
 * Simple basic messaging demonstration. The Listener instance is waiting for a message (any kind of message)
 * and is happy to receive one.
 */
public class ChainDemo
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
      Messenger.sendMessage(null, "First", "Sample content");
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
      private void onFirstMessage(Message message)
      {
         System.out.println("Got first message !");
         Messenger.sendMessage(null, "Second", "Sample content");
      }
      
      /**
       * What to do on message reception.
       *
       * @param message the message
       */
      @SuppressWarnings("unused")
      private void onSecondMessage(Message message)
      {
         System.out.println("Got second message !");
         Messenger.sendMessage(null, "Third", "Sample content");
      }
      
      /**
       * What to do on message reception.
       *
       * @param message the message
       */
      @SuppressWarnings("unused")
      private void onThirdMessage(Message message)
      {
         System.out.println("Got third message... exiting !");
         System.exit(0);
      }
   }

}
