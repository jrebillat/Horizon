package net.alantea.horizon.testng.model;

import net.alantea.horizon.message.Message;
import net.alantea.horizon.message.MessageControler;
import net.alantea.horizon.message.Receive;

@MessageControler
public class TheControler
{
   private static boolean called;
   private static boolean me;
   private static double what = 0.0;
   
   @Receive(message="TheControler::call")
   public static void call(Message message)
   {
      called = ("TheControler::call".equals(message.getIdentifier()));
      me = ("Me".equals(message.getContent()));
      what = 0.0;
   }
   
   @Receive(message="TheControler::call")
   public static void call(Double value)
   {
      called = false;
      me = false;
      what = value;
   }

   public static boolean isCalled()
   {
      return called;
   }

   public static boolean isMe()
   {
      return me;
   }

   public static double getWhat()
   {
      return what;
   }
}