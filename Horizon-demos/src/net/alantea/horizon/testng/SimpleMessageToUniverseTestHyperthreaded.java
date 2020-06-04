package net.alantea.horizon.testng;


import org.junit.Test;

import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.message.Mode;
import net.alantea.horizon.testng.model.TheListener;

public class SimpleMessageToUniverseTestHyperthreaded
{
   private TheListener listener1 = new TheListener("One");
   private TheListener listener2 = new TheListener("Two");
   
   @Test
   public void testSetMode()
   {
      Messenger.setMode(Mode.HYPERTHREADED);
      Messenger.setInterval(-200);
      Messenger.setInterval(200);
   }
   
   @Test
   public void testSetFirstListener()
   {
      Messenger.registerAllMessages(listener1);
   }
   
   @Test
   public void testSendGlobalMessageToOneListener()
   {
      Messenger.sendMessage(this, null, TheListener.FIRSTID, 666, false);
      Messenger.sendMessage(this, null, TheListener.SECONDID, Math.PI, false);
   }
   
   @Test
   public void testSendSpecificMessage()
   {
      Messenger.sendMessage(this, null, TheListener.SPECIFICID, "specific", false);
      
      Messenger.sendMessage(this, null, TheListener.SPECIFICID, 123L, false);
   }
   
   @Test
   public void testSendSpecialMessage()
   {
      Messenger.sendMessage(this, null, TheListener.SPECIALID, listener1, false);
   }
   
   @Test
   public void testSendUnknownMessage()
   {
      Messenger.sendMessage(this, null, TheListener.ANOTHERID, true, false);
   }
   
   @Test
   public void testSetSecondListener()
   {
      Messenger.registerAllMessages(listener2);
   }
   
   @Test
   public void testSendMessage2()
   {
      Messenger.sendMessage(this, null, TheListener.FIRSTID, 666, false);
      Messenger.sendMessage(this, null, TheListener.SECONDID, Math.PI, false);
   }
}