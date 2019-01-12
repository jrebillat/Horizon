package net.alantea.horizon.testng;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.alantea.horizon.message.Message;
import net.alantea.horizon.message.MessageSource;
import net.alantea.horizon.message.MessageSubscriber;
import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.message.Mode;
import net.alantea.horizon.testng.model.TheListener;

public class SimpleMessageExchangeTest implements MessageSource, MessageSubscriber
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
      this.addHorizonListener(listener1);
   }
   
   @Test(dependsOnMethods = {"testSetMode"})
   public void testSendMessageToOneListener()
   {
      sendMessage(listener2, TheListener.FIRSTID, Math.PI);
      Assert.assertFalse(listener2.isSpecific());
      Assert.assertFalse(listener2.isSpecial());
      Assert.assertTrue(listener2.isBackup());
      Assert.assertEquals(listener2.getId(), TheListener.FIRSTID);
      Assert.assertEquals(listener2.getContent(), Math.PI);

      Message message = new Message(this, listener2, TheListener.SPECIFICID, "Test", false);
      sendMessage(message);
      Assert.assertTrue(listener2.isSpecific());
      Assert.assertFalse(listener2.isSpecial());
      Assert.assertFalse(listener2.isBackup());
      Assert.assertEquals(listener2.getId(), TheListener.SPECIFICID);
      Assert.assertEquals(listener2.getContent(), "Test");
   }
   
   @Test(dependsOnMethods = {"testSetMode"})
   public void testConfidentialMessageToOneListener()
   {
      sendConfidentialMessage(listener2, TheListener.FIRSTID, Math.PI);
      Assert.assertFalse(listener2.isSpecific());
      Assert.assertFalse(listener2.isSpecial());
      Assert.assertTrue(listener2.isBackup());
      Assert.assertEquals(listener2.getId(), TheListener.FIRSTID);
      Assert.assertEquals(listener2.getContent(), Math.PI);

      Message message = new Message(this, listener2, TheListener.SPECIFICID, "Test", false);
      sendMessage(message);
      Assert.assertTrue(listener2.isSpecific());
      Assert.assertFalse(listener2.isSpecial());
      Assert.assertFalse(listener2.isBackup());
      Assert.assertEquals(listener2.getId(), TheListener.SPECIFICID);
      Assert.assertEquals(listener2.getContent(), "Test");
   }
   
   @Test(dependsOnMethods = {"testSetFirstListener"})
   public void testSendMessageToAllListeners()
   {
      this.addHorizonListener(listener1);
      
      sendMessage(TheListener.SECONDID, 666);
      Assert.assertFalse(listener1.isSpecific());
      Assert.assertFalse(listener1.isSpecial());
      Assert.assertTrue(listener1.isBackup());
      Assert.assertEquals(listener1.getId(), TheListener.SECONDID);
      Assert.assertEquals(listener1.getContent(), 666);
      
      sendMessage(TheListener.ANOTHERID);
      Assert.assertFalse(listener1.isSpecific());
      Assert.assertFalse(listener1.isSpecial());
      Assert.assertFalse(listener1.isBackup());
      Assert.assertEquals(listener1.getId(), TheListener.ANOTHERID);
   }

   @Test(dependsOnMethods = {"testSetFirstListener"})
   public void testRemoveListener()
   {
      this.removeHorizonListener(listener1);
   }

   @Test(dependsOnMethods = {"testSetFirstListener"})
   public void testSubscribe()
   {
      Messenger.addSubscription(TheListener.ANOTHERID, this);
   }

   @Test(dependsOnMethods = {"testSubscribe"})
   public void testUnsubscribe()
   {
      Messenger.removeSubscription(TheListener.ANOTHERID, this);
   }

   @Override
   public void onMessage(Message message)
   {
      
   }
}