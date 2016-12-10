package net.alantea.horizon.testng;

import org.testng.annotations.Test;

import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.testng.model.TheListener;

public class SimpleMessageToUniverseTestThreaded
{
   private TheListener listener1 = new TheListener("One");
   private TheListener listener2 = new TheListener("Two");
   
   @Test
   public void testSetMode()
   {
      Messenger.setMode(Messenger.Mode.THREADED);
      Messenger.setInterval(-200);
      Messenger.setInterval(200);
   }
   
   @Test
   public void testSetFirstListener()
   {
      Messenger.registerAllMessages(listener1);
   }
   
   @Test(groups="basis", dependsOnMethods = {"testSetMode", "testSetFirstListener"})
   public void testSendGlobalMessageToOneListener()
   {
      Messenger.sendMessage(this, null, TheListener.FIRSTID, 666, false);
      Messenger.sendMessage(this, null, TheListener.SECONDID, Math.PI, false);
   }
   
   @Test(groups="basis", dependsOnMethods = {"testSendGlobalMessageToOneListener"})
   public void testSendSpecificMessage()
   {
      Messenger.sendMessage(this, null, TheListener.SPECIFICID, "specific", false);
      
      Messenger.sendMessage(this, null, TheListener.SPECIFICID, 123L, false);
   }
   
   @Test(groups="basis", dependsOnMethods = {"testSendSpecificMessage"})
   public void testSendSpecialMessage()
   {
      Messenger.sendMessage(this, null, TheListener.SPECIALID, listener1, false);
   }
   
   @Test(groups="basis", dependsOnMethods = {"testSendSpecialMessage"})
   public void testSendUnknownMessage()
   {
      Messenger.sendMessage(this, null, TheListener.ANOTHERID, true, false);
   }
   
   @Test(dependsOnMethods = {"testSendSpecialMessage"})
   public void testSetSecondListener()
   {
      Messenger.registerAllMessages(listener2);
   }
   
   @Test(dependsOnMethods = {"testSendGlobalMessageToOneListener", "testSetSecondListener"})
   public void testSendMessage2()
   {
      Messenger.sendMessage(this, null, TheListener.FIRSTID, 666, false);
      Messenger.sendMessage(this, null, TheListener.SECONDID, Math.PI, false);
   }
}