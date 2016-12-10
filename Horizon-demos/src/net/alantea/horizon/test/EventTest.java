package net.alantea.horizon.test;

import net.alantea.horizon.message.Listen;
import net.alantea.horizon.message.Message;
import net.alantea.horizon.message.Messenger;

public class EventTest
{
   public static void main(String[] args)
   {
      Messenger.setMode(net.alantea.horizon.message.internal.SendingManager.Mode.SYNCHRONOUS);
      TheSource source = new TheSource();
      Messenger.addHorizonListener(source, new ListenerOne("One"));
      Messenger.addHorizonListener(source, new ListenerOne("Two"));
      ListenerTwo l3 = new ListenerTwo("Three");
      Messenger.register(l3);
      source.sendIt1();
      source.sendIt2();
   }
   
   static class TheSource
   {
      public void sendIt1()
      {
         Messenger.sendMessage(this, "Event1", null);
      }
      public void sendIt2()
      {
         Messenger.sendMessage(this, "Event2", null);
      }
   }
   
   static class ListenerOne
   {
      private String name;

      public ListenerOne(String key)
      {
         this.name = key;
      }
      
      public void onEvent1Message(Message event)
      {
         System.out.println("Message 1 got in " + name + " !");
      }
      
      @Listen(message="Event2")
      public void onMyMessage(Message event)
      {
         System.out.println("Message 2 got in " + name + " !");
      }
      
      public String getName()
      {
         return name;
      }
   }
   
   static class ListenerTwo extends ListenerOne
   {
      public ListenerTwo(String key)
      {
         super(key);
      }
      
      @Listen(message="Event2")
      public void onMyTwoMessage(Message event)
      {
         System.out.println("Message 2 got in " + getName() + " !");
      }
   }
}
