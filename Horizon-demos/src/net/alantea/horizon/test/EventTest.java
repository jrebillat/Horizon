package net.alantea.horizon.test;

import net.alantea.horizon.message.Receive;
import net.alantea.horizon.message.internal.ListenerManager;
import net.alantea.horizon.message.internal.RegisterManager;
import net.alantea.horizon.message.internal.SendingManager;
import net.alantea.horizon.message.Message;
import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.message.Mode;

public class EventTest
{
   public static void main(String[] args)
   {
      SendingManager.setMode(Mode.SYNCHRONOUS);
      TheSource source = new TheSource();
      ListenerManager.addHorizonListener(source, new ListenerOne("One"));
      ListenerManager.addHorizonListener(source, new ListenerOne("Two"));
      ListenerTwo l3 = new ListenerTwo("Three");
      RegisterManager.register(l3);
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
      
      @Receive(message="Event2")
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
      
      @Receive(message="Event2")
      public void onMyTwoMessage(Message event)
      {
         System.out.println("Message 2 got in " + getName() + " !");
      }
   }
}
