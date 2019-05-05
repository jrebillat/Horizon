package net.alantea.horizon.testng;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.message.Mode;
import net.alantea.horizon.testng.model.TheControler;
import net.alantea.horizon.testng.model.TheListener;

public class SimpleMessageControlerTest
{
   @Test
   public void testSendGlobalMessageToControler()
   {
      Messenger.setMode(Mode.SYNCHRONOUS);
      Assert.assertFalse(TheControler.isCalled());
      Assert.assertFalse(TheControler.isMe());
      Assert.assertTrue(TheControler.getWhat() == 0.0);
      
      Messenger.sendMessage(this, null, TheListener.FIRSTID, 666, false);
      Assert.assertFalse(TheControler.isCalled());      
      Assert.assertFalse(TheControler.isMe());
      Assert.assertTrue(TheControler.getWhat() == 0.0);
      
      Messenger.sendMessage(this, null, "TheControler::call", "You", false);
      Assert.assertTrue(TheControler.isCalled());
      Assert.assertFalse(TheControler.isMe());
      Assert.assertTrue(TheControler.getWhat() == 0.0);
      
      Messenger.sendMessage(this, null, "TheControler::call", "Me", false);
      Assert.assertTrue(TheControler.isCalled());
      Assert.assertTrue(TheControler.isMe());
      Assert.assertTrue(TheControler.getWhat() == 0.0);
      
      Messenger.sendMessage(this, null, "TheControler::call", "Me", false);
      Assert.assertTrue(TheControler.isCalled());
      Assert.assertTrue(TheControler.isMe());
      Assert.assertTrue(TheControler.getWhat() == 0.0);
      
      Messenger.sendMessage(this, null, TheListener.FIRSTID, 666, false);
      Assert.assertTrue(TheControler.isCalled());
      Assert.assertTrue(TheControler.isMe());
      Assert.assertTrue(TheControler.getWhat() == 0.0);
      
      Messenger.sendMessage(this, null, "TheControler::call", 3.14, false);
      Assert.assertFalse(TheControler.isCalled());
      Assert.assertFalse(TheControler.isMe());
      Assert.assertTrue(TheControler.getWhat() == 3.14);
      
      Messenger.sendMessage(this, null, "TheControler::call", "You", false);
      Assert.assertTrue(TheControler.isCalled());
      Assert.assertFalse(TheControler.isMe());
      Assert.assertTrue(TheControler.getWhat() == 0.0);
   }
}