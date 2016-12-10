package net.alantea.horizon.testng;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.testng.model.TheListener;

public class SynchronousTest
{
   private static final String CONTEXT1 = "Context1";
   private static final String CONTEXT2 = "Context2";
   
   private TheListener listener1 = new TheListener("One");
   private TheListener listener2 = new TheListener("Two");
   

   @Test()
   public void testSendSynchronousMessage()
   {
      // send message in context 1
      Messenger.sendSynchronousMessage(CONTEXT1, this, listener1, TheListener.FIRSTID, 666, false);
      Assert.assertFalse(listener1.isSpecific());
      Assert.assertFalse(listener1.isSpecial());
      Assert.assertTrue(listener1.isBackup());
      Assert.assertEquals(listener1.getId(), TheListener.FIRSTID);
      Assert.assertEquals(listener1.getContent(), 666);
   }
   
   @Test(dependsOnMethods = {"testSendSynchronousMessage"})
   public void testNullMessage()
   {
      Messenger.sendSynchronousMessage(null);
   }
   
   @Test(dependsOnMethods = {"testNullMessage"})
   public void testDefaultSynchronousMessage()
   {
      Messenger.register(CONTEXT2, listener2);
      Messenger.sendSynchronousMessage(CONTEXT2, this, TheListener.ANOTHERID, 666);
      Assert.assertFalse(listener2.isSpecific());
      Assert.assertFalse(listener2.isSpecial());
      Assert.assertFalse(listener2.isBackup());
      Assert.assertEquals(listener2.getId(), TheListener.ANOTHERID);
      Assert.assertEquals(listener2.getContent(), 666);
   }
}
