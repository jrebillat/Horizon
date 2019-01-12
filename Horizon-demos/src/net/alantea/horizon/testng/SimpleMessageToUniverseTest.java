package net.alantea.horizon.testng;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.message.Mode;
import net.alantea.horizon.testng.model.TheListener;

public class SimpleMessageToUniverseTest
{
   private TheListener listener1 = new TheListener("One");
   private TheListener listener2 = new TheListener("Two");
   
   @Test
   public void testSetMode()
   {
      Messenger.setMode(Mode.SYNCHRONOUS);
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
      Assert.assertFalse(listener1.isSpecific());
      Assert.assertFalse(listener1.isSpecial());
      Assert.assertTrue(listener1.isBackup());
      Assert.assertEquals(listener1.getId(), TheListener.FIRSTID);
      Assert.assertEquals(listener1.getContent(), 666);
      Messenger.sendMessage(this, null, TheListener.SECONDID, Math.PI, false);
      Assert.assertFalse(listener1.isSpecific());
      Assert.assertFalse(listener1.isSpecial());
      Assert.assertTrue(listener1.isBackup());
      Assert.assertEquals(listener1.getId(), TheListener.SECONDID);
      Assert.assertEquals(listener1.getContent(), Math.PI);
   }
   
   @Test(groups="basis", dependsOnMethods = {"testSendGlobalMessageToOneListener"})
   public void testSendSpecificMessage()
   {
      Messenger.sendMessage(this, null, TheListener.SPECIFICID, "specific", false);
      Assert.assertTrue(listener1.isSpecific());
      Assert.assertFalse(listener1.isSpecial());
      Assert.assertFalse(listener1.isBackup());
      Assert.assertEquals(listener1.getId(), TheListener.SPECIFICID);
      Assert.assertEquals(listener1.getContent(), "specific");
      
      Messenger.sendMessage(this, null, TheListener.SPECIFICID, 123L, false);
      Assert.assertTrue(listener1.isSpecific());
      Assert.assertFalse(listener1.isSpecial());
      Assert.assertFalse(listener1.isBackup());
      Assert.assertEquals(listener1.getId(), TheListener.SPECIFICID);
      Assert.assertEquals(listener1.getContent(), 123L);
   }
   
   @Test(groups="basis", dependsOnMethods = {"testSendSpecificMessage"})
   public void testSendSpecialMessage()
   {
      Messenger.sendMessage(this, null, TheListener.SPECIALID, listener1, false);
      Assert.assertFalse(listener1.isSpecific());
      Assert.assertTrue(listener1.isSpecial());
      Assert.assertFalse(listener1.isBackup());
      Assert.assertEquals(listener1.getId(), TheListener.SPECIALID);
      Assert.assertEquals(listener1.getContent(), listener1);
   }
   
   @Test(groups="basis", dependsOnMethods = {"testSendSpecialMessage"})
   public void testSendUnknownMessage()
   {
      Messenger.sendMessage(this, null, TheListener.ANOTHERID, true, false);
      Assert.assertFalse(listener1.isSpecific());
      Assert.assertFalse(listener1.isSpecial());
      Assert.assertFalse(listener1.isBackup());
      Assert.assertEquals(listener1.getId(), TheListener.ANOTHERID);
      Assert.assertEquals(listener1.getContent(), true);
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
      Assert.assertFalse(listener1.isSpecific());
      Assert.assertFalse(listener1.isSpecial());
      Assert.assertEquals(listener1.getId(), TheListener.FIRSTID);
      Assert.assertEquals(listener1.getContent(), 666);
      Assert.assertFalse(listener2.isSpecific());
      Assert.assertFalse(listener2.isSpecial());
      Assert.assertEquals(listener2.getId(), TheListener.FIRSTID);
      Assert.assertEquals(listener2.getContent(), 666);
      Messenger.sendMessage(this, null, TheListener.SECONDID, Math.PI, false);
      Assert.assertFalse(listener1.isSpecific());
      Assert.assertFalse(listener1.isSpecial());
      Assert.assertEquals(listener1.getId(), TheListener.SECONDID);
      Assert.assertEquals(listener1.getContent(), Math.PI);
      Assert.assertFalse(listener2.isSpecific());
      Assert.assertFalse(listener2.isSpecial());
      Assert.assertEquals(listener2.getId(), TheListener.SECONDID);
      Assert.assertEquals(listener2.getContent(), Math.PI);
   }
}