package net.alantea.horizon.test;

import net.alantea.horizon.message.Message;
import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.message.MessageSource;
import net.alantea.horizon.message.MessageSubscriber;
import net.alantea.horizon.message.Listen;

public class MessageTest3
{
   private TheSource source = new TheSource();
   private TheListener1 listener1 = new TheListener1();
   
   public static void main(String[] args)
   {
      new MessageTest3().f();
      try
      {
         Thread.sleep(1000000);
      }
      catch (InterruptedException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   public void f()
   {
      source.sendMessage(listener1, "Test", new Integer(1));
   }
   
   class TheSource implements MessageSource
   {
      public void onMessage(Message message)
      {
         System.out.println("I am the source, I got " + message.getContent() + " !");
         sendMessage(listener1, "End", new Integer((Integer)message.getContent() + 1));
      }
   }
   
   class TheListener1 implements MessageSubscriber, MessageSource
   {
      public void onMessage(Message message)
      {
         System.out.println("I am 1, I got " + message.getContent() + " !");
      }
      
      @Listen(message="Test")
      public void test(Message message)
      {
         System.out.println("I am 1, I got " + message.getContent() + " as test !");
         Messenger.sendMessage(this, source, message.getIdentifier(), new Integer((Integer)message.getContent() + 100), true);
      }
      
      @Listen(message="End")
      public void end(Message message)
      {
         System.out.println("I am 2, I got " + message.getContent() + " : exiting.");
         System.exit(0);
      }
   }
}