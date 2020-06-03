package net.alantea.horizon.demos.simple;

import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.message.Mode;

/**
 * Simple basic messaging demonstration. The Listener instance is waiting for a message (any kind of message)
 * and is happy to receive one.
 */
public class FunctionalListenerDemo
{
   private static final String TEXT = "Something that do not matter";
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
      
      // Create and register specific listener
      Messenger.addListener(TEXT, (message) -> System.out.println("TEXT got '" + message.getContent() + "'"));
      
      // Create and register class listener
      Messenger.addListener(String.class, (message) -> System.out.println("A string got '" + message.getContent() + "'"));
      
      // Send a message
      Messenger.sendMessage(TEXT, "Test", "Simple content");
      // Send a message
      Messenger.sendMessage("its a test", "Test", "Another simple content");
   }
   
}
