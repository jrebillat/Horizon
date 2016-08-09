package net.alantea.horizon.message.internal;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.alantea.horizon.message.Listen;
import net.alantea.horizon.message.Message;

public class MethodsManager
{

   /** The Constant method header. */
   private static final String METHODHEADER = "on";

   /** The Constant method footer. */
   private static final String METHODFOOTER = "Message";

   /** The "catch all" message constant. */
   protected static final String DEFAULTID = "__Default 1d__";

   /** The listener classes map. A map < listening class class, map <Message identifier, map <waited content, target method>>>*/
   private static Map<Class<? extends Object>, Map<String, Map<Class<?>, Method>>> classesmap = new ConcurrentHashMap<>();

   /**
    * Gets the method recursively.
    *
    * @param cl the cl
    * @param id the id
    * @param param the param
    * @return the method recursively
    */
   private static Method getMethodRecursively(Class<?> cl, String id, Class<?> param)
   {
      Map<String, Map<Class<?>, Method>> map = getMethods(cl);
      Map<Class<?>, Method> meths = map.get(id);
      if ((meths == null) && (!DEFAULTID.equals(id)))
      {
         return getMethodRecursively(cl, DEFAULTID, param);
      }

      Method ret = null;
      if (meths != null)
      {

         ret = meths.get(param);

         // search for parameter superclasses
         Class<?> superparam = param.getSuperclass();
         while ((ret == null) && (superparam != null))
         {
            ret = meths.get(superparam);
            if (!superparam.equals(Object.class))
            {
               superparam = superparam.getSuperclass();
            }
            else
            {
               superparam = null;
            }
         }

         // search for parameter interfaces (but just one level deep,
         // don't get interfaces extended by interfaces)
         if ((ret == null))
         {
            superparam = param;
            while ((ret == null) && (superparam != null) && ((!superparam.equals(Object.class))))
            {
               Class<?>[] itfs = superparam.getInterfaces();
               int i = 0;
               while ((ret == null) && (i < itfs.length) && ((!itfs[i].equals(Object.class))))
               {
                  ret = getMethodRecursively(cl, id, itfs[i]);
                  i++;
               }
               superparam = superparam.getSuperclass();
            }
         }
      }
      
      if ((ret == null) && (!param.equals(Message.class)))
      {
         ret = getMethodRecursively(cl, id, Message.class);
      }
      return ret;
   }

   /**
    * Gets the method.
    *
    * @param cl the cl class to parse
    * @param id the id identifier searched for
    * @param param the parameter to search for
    * @return the method found or null
    */
   protected static final Method getMethod(Class<?> cl, String id, Class<?> param)
   {
      return getMethodRecursively(cl, id, param);
   }

   /**
    * Parses the methods.
    *
    * @param theClass the the class
    * @return the methods
    */
   protected static final Map<String, Map<Class<?>, Method>> getMethods(Class<?> theClass)
   {
      Map<String, Map<Class<?>, Method>> methodmap = getSubMethodMap(theClass);
      classesmap.put(theClass, methodmap);
      return methodmap;
   }

   /**
    * Gets the sub method map.
    *
    * @param cl the cl
    * @return the sub method map
    */
   private static Map<String, Map<Class<?>, Method>> getSubMethodMap(Class<?> cl)
   {
      // Test silly call
      if (cl == null)
      {
         return new ConcurrentHashMap<>();
      }

      // Test if we already have done the work.
      Map<String, Map<Class<?>, Method>> methodmap = classesmap.get(cl);
      if (methodmap != null)
      {
         return methodmap;
      }

      // Search for compatible methods in interfaces (for default methods) and superclass.
      methodmap = new ConcurrentHashMap<>();
      if (!cl.equals(Object.class))
      {
         for (Class<?> cl1 : cl.getInterfaces())
         {
            methodmap.putAll(copyMethodsMap(cl1));
         }
         methodmap.putAll(copyMethodsMap(cl.getSuperclass()));
      }

      // Get methods declared in class itself
      Method[] methods = cl.getDeclaredMethods();
      for (Method method : methods)
      {
         if (method.getParameterCount() == 1)
         {
            String name = method.getName();
            // Method with @Listen
            if (method.isAnnotationPresent(Listen.class))
            {
               Listen annotation = method.getAnnotation(Listen.class);
               if (!DEFAULTID.equals(annotation.message()))
               {
                  registerListeningMethod(annotation.message(), method, methodmap);
               }
               for( String id : annotation.messages())
               {
                  registerListeningMethod(id, method, methodmap);
               }
            }
            else if ((name.startsWith(METHODHEADER)) && (name.endsWith(METHODFOOTER)))
            {
                  String id = name.substring(METHODHEADER.length(), name.length() - METHODFOOTER.length());
                  registerListeningMethod(id, method, methodmap);
            }
         }
      }
      return methodmap;
   }
   
   private static Map<String, Map<Class<?>, Method>> copyMethodsMap(Class<?> subclass)
   {
      Map<String, Map<Class<?>, Method>> methodmap = new HashMap<>();
      
      // Warning : copy content, do not just link to it.
      Map<String, Map<Class<?>, Method>> supermap = getSubMethodMap(subclass);
      for(String key : supermap.keySet())
      {
         Map<Class<?>, Method> submap = supermap.get(key);
         Map<Class<?>, Method> meths = methodmap.get(key);
         if (meths == null)
         {
            meths = new HashMap<>();
         }
         for (Class<?> parmClass : submap.keySet())
         {
            meths.put(parmClass, submap.get(parmClass));
         }
         methodmap.put(key, meths);
      }
      return methodmap;
   }
   
   /**
    * Register listening method.
    *
    * @param key the key
    * @param method the method
    * @param methodmap the methodmap
    */
   private static void registerListeningMethod(String key, Method method, Map<String, Map<Class<?>, Method>> methodmap)
   {
      method.setAccessible(true);
      Map<Class<?>, Method> meths = methodmap.get(key);
      if (meths == null)
      {
         meths = new HashMap<>();
      }
      meths.put(method.getParameterTypes()[0], method);
      methodmap.put(key, meths);
   }
}
