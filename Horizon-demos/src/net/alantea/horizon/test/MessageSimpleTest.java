package net.alantea.horizon.test;

import net.alantea.horizon.message.Message;
import net.alantea.horizon.message.MessageSource;
import net.alantea.horizon.message.MessageSubscriber;

public class MessageSimpleTest
{
   private TheSource source = new TheSource();
   private TheListener1 listener1 = new TheListener1();
   private TheListener2 listener2 = new TheListener2();
   private TheListener3 listener3 = new TheListener3();
   
   public static void main(String[] args)
   {
      new Thread(() -> 
      {
         try
         {
            Thread.sleep(1000);
         }
         catch (Exception e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }).start();
      new MessageSimpleTest().f();
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
      }
   }
   
   class TheListener1 implements MessageSubscriber, MessageSource
   {
      public void onMessage(Message message)
      {
         System.out.println("I am 1, I got " + message.getContent() + " !");
         sendMessage(listener2, message.getIdentifier(), new Integer((Integer)message.getContent() + 1));
      }
   }
   
   class TheListener2 implements MessageSubscriber,MessageSource
   {
      public void onMessage(Message message)
      {
         System.out.println("I am 2, I got " + message.getContent() + " !");
         sendMessage(listener3, message.getIdentifier(), new Integer((Integer)message.getContent() + 1));
      }
      public void onTestMessage(Message message)
      {
         System.out.println("I am 2, I got " + message.getContent() + " !");
         sendMessage(listener3, message.getIdentifier(), new Integer((Integer)message.getContent() + 100));
      }
   }
   
   class TheListener3 implements MessageSubscriber,MessageSource
   {
      public void onMessage(Message message)
      {
         System.out.println("I am 3, I got " + message.getContent() + " !");
         sendMessage(source, message.getIdentifier(), new Integer((Integer)message.getContent() + 10));
      }
   }
}
