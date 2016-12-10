package net.alantea.horizon.testng;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.testng.model.TheListener;
import net.alantea.horizon.testng.model.TheListener2;

public class ListenMessagesTest
{
   private static final String CONTEXT1 = "Context1";
   
   private TheListener2 listener = new TheListener2("One");

   @Test()
   public void testSendNormalMessage()
   {
      // send message in context 1
      Messenger.sendSynchronousMessage(CONTEXT1, this, listener, TheListener.FIRSTID, 666, false);
      Assert.assertFalse(listener.isSpecific());
      Assert.assertFalse(listener.isSpecial());
      Assert.assertTrue(listener.isBackup());
      Assert.assertNull(listener.getGotIdentifier());
      Assert.assertEquals(listener.getId(), TheListener.FIRSTID);
      Assert.assertEquals(listener.getContent(), 666);
   }

   @Test()
   public void testSendAnotherMessage()
   {
      // send message in context 1
      Messenger.sendSynchronousMessage(CONTEXT1, this, listener, TheListener.ANOTHERID, 666, false);
      Assert.assertFalse(listener.isSpecific());
      Assert.assertFalse(listener.isSpecial());
      Assert.assertFalse(listener.isBackup());
      Assert.assertEquals(listener.getGotIdentifier(), TheListener.ANOTHERID);
      Assert.assertEquals(listener.getId(), TheListener.ANOTHERID);
      Assert.assertEquals(listener.getContent(), 666);
   }
}
