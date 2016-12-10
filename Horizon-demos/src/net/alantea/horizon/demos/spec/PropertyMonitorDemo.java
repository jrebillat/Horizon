package net.alantea.horizon.demos.spec;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.alantea.horizon.message.Listen;
import net.alantea.horizon.message.Messenger;

public class PropertyMonitorDemo
{

   public static final String DOUBLEMODIFIED = "DoubleModified";
   public static final String MODIFYSTRING = "ModifyString";
   public static final String STRINGMODIFIED = "StringModified";

   public static void main(String[] args) throws InterruptedException
   {
      DoubleProperty property1 = new SimpleDoubleProperty(123);
      Messenger.monitorProperty(property1, DOUBLEMODIFIED);
      StringProperty property2 = new SimpleStringProperty("Test");
      Messenger.monitorProperty(property2, STRINGMODIFIED, MODIFYSTRING);
      DoubleWatchingElement element1 = new DoubleWatchingElement();
      Messenger.register(element1);
      StringWatchingElement element2 = new StringWatchingElement();
      Messenger.register(element2);
      
      property1.set(666);
      Thread.sleep(1000);
   }

   private static class DoubleWatchingElement
   {
      @Listen(message=DOUBLEMODIFIED)
      private void manageDouble(Double value)
      {
         System.out.println("Double property modified to : " + value);
         Messenger.sendMessage(value, MODIFYSTRING, "Modified to " + value);
      }
   }

   private static class StringWatchingElement
   {
      @Listen(message=STRINGMODIFIED)
      private void manageDouble(String value)
      {
         System.out.println("String property modified to : '" + value + "'");
      }
   }
}
