package net.alantea.horizon.test;

import net.alantea.horizon.message.Message;
import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.message.MessageSource;
import net.alantea.horizon.message.MessageSubscriber;
import net.alantea.horizon.message.Receive;
import net.alantea.horizon.message.internal.RegisterManager;

public class MessageTest4
{
   private TheSource source = new TheSource();
   private TheListener1 listener1 = new TheListener1();
   
   public static void main(String[] args)
   {
      new MessageTest4().f();
      try
      {
         Thread.sleep(10000);
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }
   }
   
   public void f()
   {
      source.sendMessage(null, "Test", new Integer(1));
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
      public TheListener1()
      {
         RegisterManager.register(this);
      }
      
      public void onMessage(Message message)
      {
         System.out.println("I am 1, I got " + message.getContent() + " !");
      }
      
      @Receive(message="Test")
      public void test(Integer message)
      {
         System.out.println("I am 1, I got " + message + " as test !");
         Messenger.sendMessage(this, source, "Test", new Integer((Integer)message + 100), true);
      }
      
      @Receive(message="End")
      public void end(Message message)
      {
         System.out.println("I am 2, I got " + message.getContent() + " : exiting.");
         System.exit(0);
      }
   }
}