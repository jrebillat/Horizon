package net.alantea.horizon.demos.simple;

import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.message.Mode;

/**
 * Simple basic messaging demonstration. The Listener instance is waiting for a message (any kind of message)
 * and is happy to receive one.
 */
public class FunctionalDemo
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
      Messenger.addSubscription("SimpleTest", (message) -> System.out.println(message.getContent()));
      
      // Send a message
      Messenger.sendMessage(null, "SimpleTest", "Simple content");
   }
   
}
