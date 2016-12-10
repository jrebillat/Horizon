package net.alantea.horizon.testng;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.testng.model.TheListener;

public class HorizonListenerTest
{
   private TheListener listener1 = new TheListener("One");
   private TheListener listener2 = new TheListener("Two");
   
   @Test
   public void testSetMode()
   {
      Messenger.setMode(Messenger.Mode.SYNCHRONOUS);
   }
   
   @Test(dependsOnMethods = {"testSetMode"})
   public void testSendMessageInTheVoid()
   {
      Messenger.sendMessage(this, null, TheListener.FIRSTID, 666, false);
   }
   
   @Test(dependsOnMethods = {"testSendMessageInTheVoid"})
   public void testSetBadListener()
   {
      Messenger.addHorizonListener(this, null);
   }
   
   @Test(dependsOnMethods = {"testSendMessageInTheVoid"})
   public void testSetSillyListener()
   {
      Messenger.addHorizonListener(null, null);
   }
   
   @Test(dependsOnMethods = {"testSendMessageInTheVoid"})
   public void testSetListenerToVoid()
   {
      Messenger.addHorizonListener(null, this);
   }
   
   @Test(dependsOnMethods = {"testSetBadListener"})
   public void testSetFirstListener()
   {
      Messenger.addHorizonListener(this, listener1);
   }
   
   @Test(groups="basis", dependsOnMethods = {"testSetMode", "testSetFirstListener"})
   public void testSendGlobalMessageToOneListener()
   {
      Messenger.sendMessage(this, null, TheListener.FIRSTID, 666, false);
      System.out.println(listener1);
      Assert.assertFalse(listener1.isSpecific());
      Assert.assertFalse(listener1.isSpecial());
      Assert.assertEquals(listener1.getId(), TheListener.FIRSTID);
      Assert.assertEquals(listener1.getContent(), 666);
      Messenger.sendMessage(this, null, TheListener.SECONDID, Math.PI, false);
      System.out.println(listener1);
      Assert.assertFalse(listener1.isSpecific());
      Assert.assertFalse(listener1.isSpecial());
      Assert.assertEquals(listener1.getId(), TheListener.SECONDID);
      Assert.assertEquals(listener1.getContent(), Math.PI);
   }
   
   @Test(dependsOnMethods = {"testSetBadListener"})
   public void testResetFirstListener()
   {
      Messenger.addHorizonListener(this, listener1);
   }
   
   @Test(dependsOnMethods = {"testResetFirstListener"})
   public void testSetSecondListener()
   {
      Messenger.addHorizonListener(this, listener2);
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
   }
   
   @Test(dependsOnMethods = {"testSendMessage2"})
   public void testRemoveSecondListener()
   {
      Messenger.removeHorizonListener(this, listener2);
   }
   
   @Test(dependsOnMethods = {"testSendMessage2"})
   public void testRemoveFromNothingListener()
   {
      Messenger.removeHorizonListener(null, listener2);
   }
   
   @Test(dependsOnMethods = {"testSendMessage2"})
   public void testRemoveFromAnythingListener()
   {
      Messenger.removeHorizonListener("AnyString", listener2);
   }
   
   @Test(dependsOnMethods = {"testSendMessage2"})
   public void testRemoveNotAListener()
   {
      Messenger.addHorizonListener("SomeString", listener1);
      Messenger.removeHorizonListener("SomeString", listener2);
      
      // Clean
      Messenger.removeHorizonListener("SomeString", listener1);
   }
   
   @Test(dependsOnMethods = {"testRemoveSecondListener"})
   public void testSendGlobalMessageToOnlyOneListener()
   {
      Messenger.sendMessage(this, null, TheListener.FIRSTID, 128, false);
      Assert.assertFalse(listener1.isSpecific());
      Assert.assertFalse(listener1.isSpecial());
      Assert.assertEquals(listener1.getId(), TheListener.FIRSTID);
      Assert.assertEquals(listener1.getContent(), 128);
      Assert.assertEquals(listener2.getContent(), 666);
   }
   
   @Test(dependsOnMethods = {"testSendGlobalMessageToOnlyOneListener"})
   public void testRemoveAllListeners()
   {
      Messenger.removeAllHorizonListeners(this);
   }
   
   @Test(dependsOnMethods = {"testSendGlobalMessageToOnlyOneListener"})
   public void testRemoveSillyListeners()
   {
      Messenger.removeAllHorizonListeners(null);
   }
   
   @Test(dependsOnMethods = {"testSendGlobalMessageToOnlyOneListener"})
   public void testRemoveEmptyListeners()
   {
      Messenger.removeAllHorizonListeners("BadIdentifier");
   }
   
   @Test(dependsOnMethods = {"testRemoveSecondListener"})
   public void testSendGlobalMessageToNoListener()
   {
      Messenger.sendMessage(this, null, TheListener.FIRSTID, 666, false);
   }
}
