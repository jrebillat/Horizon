package net.alantea.horizon.testng;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.message.Mode;
import net.alantea.horizon.testng.model.TheListener;

public class HorizonListenerTest
{
   private TheListener listener1 = new TheListener("One");
   private TheListener listener2 = new TheListener("Two");
   
   @Test
   public void testSetMode()
   {
      Messenger.setMode(Mode.SYNCHRONOUS);
   }
   
   @Test(dependsOnMethods = {"testSetMode"})
   public void testSendMessageInTheVoid()
   {
      Messenger.sendMessage(this, null, TheListener.FIRSTID, 666, false);
   }
   
   @Test(dependsOnMethods = {"testSendMessageInTheVoid"})
   public void testSetBadListener()
   {
      Messenger.addListener(this, null);
   }
   
   @Test(dependsOnMethods = {"testSendMessageInTheVoid"})
   public void testSetSillyListener()
   {
      Messenger.addListener(null, null);
   }
   
   @Test(dependsOnMethods = {"testSendMessageInTheVoid"})
   public void testSetListenerToVoid()
   {
      Messenger.addListener(null, this);
   }
   
   @Test(dependsOnMethods = {"testSetBadListener"})
   public void testSetFirstListener()
   {
      Messenger.addListener(this, listener1);
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
      Messenger.addListener(this, listener1);
   }
   
   @Test(dependsOnMethods = {"testResetFirstListener"})
   public void testSetSecondListener()
   {
      Messenger.addListener(this, listener2);
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
      Messenger.removeListener(this, listener2);
   }
   
   @Test(dependsOnMethods = {"testSendMessage2"})
   public void testRemoveFromNothingListener()
   {
      Messenger.removeListener(null, listener2);
   }
   
   @Test(dependsOnMethods = {"testSendMessage2"})
   public void testRemoveFromAnythingListener()
   {
      Messenger.removeListener("AnyString", listener2);
   }
   
   @Test(dependsOnMethods = {"testSendMessage2"})
   public void testRemoveNotAListener()
   {
      Messenger.removeListener("SomeString", listener1);
      Messenger.removeListener("SomeString", listener2);
      
      // Clean
      Messenger.removeListener("SomeString", listener1);
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
      Messenger.removeAllListeners(this);
   }
   
   @Test(dependsOnMethods = {"testSendGlobalMessageToOnlyOneListener"})
   public void testRemoveSillyListeners()
   {
      Messenger.removeAllListeners(null);
   }
   
   @Test(dependsOnMethods = {"testSendGlobalMessageToOnlyOneListener"})
   public void testRemoveEmptyListeners()
   {
      Messenger.removeAllListeners("BadIdentifier");
   }
   
   @Test(dependsOnMethods = {"testRemoveSecondListener"})
   public void testSendGlobalMessageToNoListener()
   {
      Messenger.sendMessage(this, null, TheListener.FIRSTID, 666, false);
   }
}
