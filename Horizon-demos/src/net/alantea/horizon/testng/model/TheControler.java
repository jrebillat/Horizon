package net.alantea.horizon.testng.model;

import net.alantea.horizon.message.Message;
import net.alantea.horizon.message.MessageAction;
import net.alantea.horizon.message.MessageControler;

@MessageControler
public class TheControler
{
   private static boolean called;
   private static boolean me;
   
   @MessageAction
   public static void call(Message message)
   {
      called = ("TheControler::call".equals(message.getIdentifier()));
      me = ("Me".equals(message.getContent()));
   }

   public static boolean isCalled()
   {
      return called;
   }

   public static boolean isMe()
   {
      return me;
   }
}