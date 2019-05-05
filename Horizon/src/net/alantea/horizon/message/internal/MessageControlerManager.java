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
import net.alantea.horizon.message.MessageAction;
import net.alantea.horizon.message.MessageControler;
import net.alantea.horizon.message.MessageSubscriber;
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
            String classInjectionName = cl.getSimpleName();
            if (cl.getAnnotation(MessageControler.class) != null)
            {
               classInjectionName = (!cl.getAnnotation(MessageControler.class).value().equals(""))
                     ? cl.getAnnotation(MessageControler.class).value() : cl.getSimpleName();
            }
            
            // Search in methods
            for (Method method : cl.getDeclaredMethods())
            {
               if ((method.getAnnotation(MessageAction.class) != null) 
                     && (Modifier.isStatic(method.getModifiers()))
                     && (method.getParameterCount() == 1)
                     && (method.getParameterTypes()[0].isAssignableFrom(Message.class)))
               {
                  method.setAccessible(true);
                  MessageSubscriber subscriber = new MessageSubscriber() 
                  {
                     @Override
                     public void onMessage(Message message)
                     {
                        try
                        {
                           method.invoke(null, message);
                        }
                        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
                        {
                           // silently ignore it
                        }
                     }
                  };
                  String methodInjectionName = method.getName();
                  if (method.getAnnotation(MessageAction.class) != null)
                  {
                     methodInjectionName = (!method.getAnnotation(MessageAction.class).value().equals(""))
                           ? method.getAnnotation(MessageAction.class).value() : method.getName();
                  }
                  String injectionName = classInjectionName + "::" + methodInjectionName;
                  subscriber.subscribe(injectionName);
               }
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
