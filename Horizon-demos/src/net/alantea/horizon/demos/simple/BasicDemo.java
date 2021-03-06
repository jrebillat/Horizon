package net.alantea.horizon.demos.simple;

import net.alantea.horizon.message.Message;
import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.message.Mode;
import net.alantea.horizon.message.internal.RegisterManager;

/**
 * Simple basic messaging demonstration. The Listener instance is waiting for a message (any kind of message)
 * and is happy to receive one.
 */
public class BasicDemo
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
      RegisterManager.register(listener);
      
      // Send a message
      Messenger.sendMessage(null, "SimpleTest", "Simple content");
   }
   
   /**
    * Listener waiting for a message.
    */
   private static class Listener
   {
      /**
       * What to do on message reception. Will catch any message sent.
       *
       * @param message the message
       */
      @SuppressWarnings("unused")
      private void onMessage(Message message)
      {
         System.out.println("Got message !");
      }
   }

}
