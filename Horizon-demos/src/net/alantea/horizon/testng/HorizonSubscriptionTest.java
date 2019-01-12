package net.alantea.horizon.testng;

import org.testng.annotations.Test;

import net.alantea.horizon.message.Message;
import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.message.Mode;
import net.alantea.horizon.testng.model.TheListener;

public class HorizonSubscriptionTest
{
   private TheListener listener1 = new TheListener("One");
   private TheListener listener2 = new TheListener("Two");
   
   @Test
   public void testSetMode()
   {
      Messenger.setMode(Mode.SYNCHRONOUS);
   }
   
   @Test(dependsOnMethods = {"testSetMode"})
   public void testUnsubscribeEmpty()
   {
      Messenger.removeSubscription(TheListener.SPECIFICID, listener2);
   }
   
   @Test(dependsOnMethods = {"testUnsubscribeEmpty"})
   public void testSubscribeToNull()
   {
      Messenger.addSubscription(null, listener1);
   }
   
   @Test(dependsOnMethods = {"testUnsubscribeEmpty"})
   public void testSubscribeNullContext()
   {
      Messenger.addSubscription(null, "FakeIDentifier", listener1);
   }
   
   @Test(dependsOnMethods = {"testSubscribeNullContext"})
   public void testResubscribe()
   {
      Messenger.addSubscription(null, "FakeIDentifier", listener1);
   }
   
   @Test(dependsOnMethods = {"testUnsubscribeEmpty"})
   public void testSubscribeNull()
   {
      Messenger.addSubscription(TheListener.SPECIFICID, null);
   }
   
   @Test(dependsOnMethods = {"testUnsubscribeEmpty"})
   public void testRegisterNull()
   {
      Messenger.register(null);
   }
   
   @Test(dependsOnMethods = {"testUnsubscribeEmpty"})
   public void testRegisterContextNull()
   {
      Messenger.register(null, null);
   }
   
   @Test(dependsOnMethods = {"testUnsubscribeEmpty"})
   public void testRegister()
   {
      Messenger.register(listener1);
   }
   
   @Test(dependsOnMethods = {"testRegister"})
   public void testUnregisterDefault()
   {
      Messenger.unregister(listener1);
   }
   
   @Test(dependsOnMethods = {"testRegister"})
   public void testUnregisterDefaultNull()
   {
      Messenger.unregister(null);
   }
   
   @Test(dependsOnMethods = {"testUnsubscribeEmpty"})
   public void testRegisterContext()
   {
      Messenger.register("Context1", listener1);
      Messenger.register("Context2", listener1);
   }
   
   @Test(dependsOnMethods = {"testRegisterContext"})
   public void testUnregisterContextIdentifier()
   {
      Messenger.unregister("Context2", listener1);
   }
   
   @Test(dependsOnMethods = {"testRegisterContext"})
   public void testUnregisterNullContext()
   {
      Messenger.unregister(null, listener1);
   }
   
   @Test(dependsOnMethods = {"testRegisterContext"})
   public void testUnregisterNullListenerContext()
   {
      Messenger.unregister("Context2", null);
   }
   
   @Test(dependsOnMethods = {"testRegisterContext"})
   public void testUnregisterBadListenerContext()
   {
      Messenger.unregister("Context", "Anything");
   }
   
   @Test(dependsOnMethods = {"testRegisterContext"})
   public void testUnregisterNotAListenerContext()
   {
      Messenger.register("Context3", listener1);
      Messenger.unregister("Context3", "Anything");
   }
   
   @Test(dependsOnMethods = {"testUnsubscribeEmpty"})
   public void testNullMessage()
   {
      Messenger.sendMessage(null);
   }
   
   @Test(dependsOnMethods = {"testUnsubscribeEmpty"})
   public void testUnregister()
   {
      Messenger.addSubscription(TheListener.SPECIFICID, listener2);
      Messenger.removeSubscription(TheListener.SPECIFICID, listener2);
      Messenger.removeSubscription(null, listener2);
      Messenger.removeSubscription(TheListener.SPECIFICID, null);
      Messenger.removeSubscription(null, TheListener.SPECIFICID, listener2);
   }
   
   @Test(dependsOnMethods = {"testUnregister"})
   public void testAddHorizonListener()
   {
      Messenger.addListener(this, listener2);
      // re-add
      Messenger.addListener(this, listener2);
   }
   
   @Test(dependsOnMethods = {"testAddHorizonListener"})
   public void testRemoveHorizonListener()
   {
      Messenger.removeListener(this, listener2);
      Messenger.removeListener(this, listener1);
      Messenger.removeListener(listener1, this);

   }
   
   @Test(dependsOnMethods = {"testRemoveHorizonListener"})
   public void testSendMessage()
   {
      Message message = new Message(this, listener2, TheListener.SPECIFICID, "Test", false);
      message.setConfidential(true);
      Messenger.sendMessage(message);
      Messenger.sendMessage(new Message(this, listener2, TheListener.SPECIFICID, "Test", false));
      Messenger.sendConfidentialMessage(this, listener2, TheListener.SPECIFICID, "Test");
      Messenger.sendMessage(this, TheListener.SPECIFICID, "Test");
   }
   
   @Test(dependsOnMethods = {"testSendMessage"})
   public void testSendSynchronousMessage()
   {
      Messenger.sendSynchronousMessage(this, listener2, TheListener.SPECIFICID, "Test", false);
      Messenger.sendSynchronousMessage(new Message(this, listener2, TheListener.SPECIFICID, "Test", true));
      Messenger.sendSynchronousMessage(this, TheListener.SPECIFICID, "Test");
   }
   
   @Test(dependsOnMethods = {"testSendSynchronousMessage"})
   public void testRemoveAllHorizonListener()
   {
      Messenger.removeAllListeners(listener2);
      Messenger.removeAllListeners(this);
      Messenger.removeAllListeners(null);
   }
   
   @Test(dependsOnMethods = {"testSendSynchronousMessage"})
   public void testUnregisterAllMessages()
   {
      Messenger.registerAllMessages(listener1);
      Messenger.registerAllMessages(listener1);
      Messenger.unregisterAllMessages(listener1);
      Messenger.unregisterAllMessages(this);
      Messenger.unregisterAllMessages(null);
   }
   
   @Test(dependsOnMethods = {"testUnregisterAllMessages"})
   public void testNullRegistering()
   {
      Messenger.registerAllMessages(null);
   }
   
   @Test(dependsOnMethods = {"testUnregisterAllMessages"})
   public void testRemoveUnregistered()
   {
      Messenger.removeSubscription("DontCare", null);
   }
   
   @Test(dependsOnMethods = {"testRemoveUnregistered"})
   public void testUnregisterSillyMessages()
   {
      Messenger.removeSubscription("NothingHere", "DontCare");
   }
   
}
