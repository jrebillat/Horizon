package net.alantea.horizon.testng;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.message.Mode;
import net.alantea.horizon.testng.model.TheListener;

public class ContextsTest
{
   private static final String CONTEXT1 = "Context1";
   private static final String CONTEXT2 = "Context2";
   
   private TheListener listener1 = new TheListener("One");
   private TheListener listener2 = new TheListener("Two");
   
   @Test
   public void testSetMode()
   {
      Messenger.setMode(Mode.SYNCHRONOUS);
   }
   
   @Test(dependsOnMethods = {"testSetMode"})
   public void testRegisterInContext()
   {
      Messenger.register(CONTEXT1, listener1);
      Messenger.register(CONTEXT2, listener2);
   }
   
   @Test(dependsOnMethods = {"testRegisterInContext"})
   public void testSendGlobalMessageToOneListener()
   {
      // send message in context 1
      Messenger.sendMessage(CONTEXT1, this, null, TheListener.SPECIALID, 666, false);
      Assert.assertFalse(listener1.isSpecific());
      Assert.assertTrue(listener1.isSpecial());
      Assert.assertFalse(listener1.isBackup());
      Assert.assertEquals(listener1.getId(), TheListener.SPECIALID);
      Assert.assertEquals(listener1.getContent(), 666);

      // send message in context 2
      Messenger.sendMessage(CONTEXT2, this, TheListener.ANOTHERID, Math.PI);
      // Verify that listener1 has not been changed
      Assert.assertFalse(listener1.isSpecific());
      Assert.assertTrue(listener1.isSpecial());
      Assert.assertFalse(listener1.isBackup());
      Assert.assertEquals(listener1.getId(), TheListener.SPECIALID);
      Assert.assertEquals(listener1.getContent(), 666);
      
      // verify listener 2 has been updated
      Assert.assertFalse(listener2.isSpecific());
      Assert.assertFalse(listener2.isSpecial());
      Assert.assertEquals(listener2.getId(), TheListener.ANOTHERID);
      Assert.assertEquals(listener2.getContent(), Math.PI);

      // re-send message in context 1
      Messenger.sendMessage(CONTEXT1, this, null, TheListener.SPECIALID, 666, false);
      // verify that listener 2 has not been changed
      Assert.assertFalse(listener2.isSpecific());
      Assert.assertFalse(listener2.isSpecial());
      Assert.assertEquals(listener2.getId(), TheListener.ANOTHERID);
      Assert.assertEquals(listener2.getContent(), Math.PI);

   }
}
