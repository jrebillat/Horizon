/**
 * 
 */
package net.alantea.horizon.message.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import net.alantea.horizon.message.Message;
import net.alantea.horizon.message.MessageControler;
import net.alantea.horizon.message.MessageSubscriber;
import net.alantea.horizon.message.Receive;
import net.alantea.tools.scan.Scanner;

/**
 * @author Manager
 *
 */
public class MessageControlerManager extends PropertyMonitor
{
   static
   {
      List<String> controlers = Scanner.getNamesOfClassesWithAnnotation(MessageControler.class);
      for (String className : controlers)
      {
         try
         {
            Class<?> cl = ClassLoader.getSystemClassLoader().loadClass(className);
            
            // Search in methods
            List<String> events = new LinkedList<>();
            for (Method method : cl.getDeclaredMethods())
            {
               if ((method.getAnnotation(Receive.class) != null) 
                     && (Modifier.isStatic(method.getModifiers()))
                     && (method.getParameterCount() == 1))
               {
                  String injectionName = method.getAnnotation(Receive.class).message();
                  if (!events.contains(injectionName))
                  {
                     events.add(injectionName);
                  }
               }
            }
            
            for (String event : events)
            {
               SubscriptionManager.addSubscription(event, cl);
            }
         }
         catch (ClassNotFoundException e)
         {
         }
      }
   }

   public static void initialize()
   {
      // work is done in static part
   }

}
