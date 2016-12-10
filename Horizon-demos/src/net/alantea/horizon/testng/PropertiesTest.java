package net.alantea.horizon.testng;

import org.testng.Assert;
import org.testng.annotations.Test;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.alantea.horizon.message.Messenger;
import net.alantea.horizon.testng.model.TheListener;

public class PropertiesTest
{
   private static final String CONTEXT2 = "Context2";
   
   private TheListener listener1 = new TheListener("One");
   private TheListener listener2 = new TheListener("Two");
   
   private StringProperty property1 = new SimpleStringProperty();
   private StringProperty property2 = new SimpleStringProperty();
   
   @Test
   public void testSetInitial()
   {
      Messenger.setMode(Messenger.Mode.SYNCHRONOUS);
      Messenger.register(listener1);
      Messenger.register(CONTEXT2, listener2);
   }
   
   @Test(dependsOnMethods = {"testSetInitial"})
   public void testSetMonitoring()
   {
      Messenger.monitorProperty(property1, TheListener.SPECIALID);
   }
   
   @Test(dependsOnMethods = {"testSetMonitoring"})
   public void testSetContextValue()
   {
      property2.set("Hello World");
      // verify listener2
      Assert.assertFalse(listener2.isSpecific());
      Assert.assertFalse(listener2.isSpecial());
      Assert.assertFalse(listener2.isBackup());
      Assert.assertEquals(listener2.getId(), TheListener.ANOTHERID);
      Assert.assertEquals(listener2.getContent(), property2.get());
   }
   
   @Test(dependsOnMethods = {"testSetContextValue"})
   public void testSetValue()
   {
      property1.set("Bingo");
      // verify listener1
      Assert.assertFalse(listener1.isSpecific());
      Assert.assertTrue(listener1.isSpecial());
      Assert.assertFalse(listener1.isBackup());
      Assert.assertEquals(listener1.getId(), TheListener.SPECIALID);
      Assert.assertEquals(listener1.getContent(), property1.get());

      // verify listener2
      Assert.assertFalse(listener2.isSpecific());
      Assert.assertFalse(listener2.isSpecial());
      Assert.assertFalse(listener2.isBackup());
      Assert.assertEquals(listener2.getId(), TheListener.ANOTHERID);
      Assert.assertEquals(listener2.getContent(), property2.get());
   }
}
