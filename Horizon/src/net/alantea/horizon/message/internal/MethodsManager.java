package net.alantea.horizon.message.internal;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.alantea.horizon.message.Receive;
import net.alantea.horizon.message.Receives;
import net.alantea.horizon.message.Message;

/**
 * The Class MethodsManager. This class contains the elements to manage the methods, their parameters and their access, in an
 * Horizon point of view.
 */
public class MethodsManager
{

   /** The Constant method header. */
   private static final String METHODHEADER = "on";

   /** The Constant method footer. */
   private static final String METHODFOOTER = "Message";

   /** The "catch all" message constant. */
   protected static final String DEFAULTID = "";

   /** The listener classes map. A map < listening class class, map <Message identifier, map <waited content, target method>>>*/
   private static Map<Class<? extends Object>, Map<String, Map<Class<?>, Method>>> instanceClassesmap = new ConcurrentHashMap<>();
   
   /** The listener classes map. A map < listening class class, map <Message identifier, map <waited content, target method>>>*/
   private static Map<Class<? extends Object>, Map<String, Map<Class<?>, Method>>> staticClassesmap = new ConcurrentHashMap<>();

   /**
    * Gets the method listening to identifier with the correct parameter.
    *
    * @param managedClass the managed class to parse
    * @param identifier the identifier searched for
    * @param parameter the parameter to search for
    * @return the method found or null
    */
   public static final Method getStaticMethod(Class<?> managedClass, String identifier, Class<?> parameter)
   {
      if ((managedClass == null) || (identifier == null))
      {
         return null;
      }
      return getMethodRecursively(managedClass, identifier, parameter, true);
   }

   /**
    * Gets the method listening to identifier with the correct parameter.
    *
    * @param managedClass the managed class to parse
    * @param identifier the identifier searched for
    * @param parameter the parameter to search for
    * @return the method found or null
    */
   public static final Method getMethod(Class<?> managedClass, String identifier, Class<?> parameter)
   {
      if ((managedClass == null) || (identifier == null))
      {
         return null;
      }
      return getMethodRecursively(managedClass, identifier, parameter, false);
   }

   /**
    * Parses all methods for a class, to organize them and add them in the global class map.
    *
    * @param managedClass the class
    * @return the methods
    */
   public static final Map<String, Map<Class<?>, Method>> getMethods(Class<?> managedClass)
   {
      // get methods map for class
      Map<String, Map<Class<?>, Method>> methodmap = getSubMethodMap(managedClass, false);
      return methodmap;
   }

   /**
    * Parses all methods for a class, to organize them and add them in the global class map.
    *
    * @param managedClass the class
    * @return the methods
    */
   public static final Map<String, Map<Class<?>, Method>> getStaticMethods(Class<?> managedClass)
   {
      // get methods map for class
      Map<String, Map<Class<?>, Method>> methodmap = getSubMethodMap(managedClass, true);
      return methodmap;
   }
   
   //-----------------------------------------------------------------------------------------------------------
   /**
    * Gets the method recursively.
    *
    * @param managedClass the managed class
    * @param identifier the identifier
    * @param parameter the parameter
    * @return the method recursively
    */
   private static Method getMethodRecursively(Class<?> managedClass, String identifier, Class<?> parameter, boolean staticFlag)
   {
      // get the map for methods in class.
      Map<String, Map<Class<?>, Method>> map = getMethods(managedClass);
      
      // search for methods managing the identifier (may have several depending on parameter type)
      Map<Class<?>, Method> meths = map.get(identifier);
      
      // If nothing is found, analyse more the class.
      if ((meths == null) && (!DEFAULTID.equals(identifier)))
      {
         return getMethodRecursively(managedClass, DEFAULTID, parameter, staticFlag);
      }

      // Analyze parameter type for each method, to get the best match.
      Method ret = null;
      if (meths != null)
      {
         // Get method with exactly the given parameter type.
         ret = meths.get(parameter);

         // Set current class as superclass.
         Class<?> superparam = parameter.getSuperclass();
         // If not already found, we have to search for parameter superclass of current class
         while ((ret == null) && (superparam != null))
         {
            // Get method with superclass parameter type.
            ret = meths.get(superparam);
            // prepare superclass for superclass, for next loop, except for Object.
            if (!superparam.equals(Object.class))
            {
               superparam = superparam.getSuperclass();
            }
            else
            {
               superparam = null;
            }
         }

         // search for parameter interfaces for parameter class and all super classes
         // TODO (but just one level deep, don't get interfaces extended by interfaces)
         if ((ret == null))
         {
            // rewind to parameter
            superparam = parameter;
            while ((ret == null) && (superparam != null) && ((!superparam.equals(Object.class))))
            {
               // get interfaces implemented by the curent class
               Class<?>[] itfs = superparam.getInterfaces();
               int i = 0;
               while ((ret == null) && (i < itfs.length) && ((!itfs[i].equals(Object.class))))
               {
                  // TODO why not use 'ret = meths.get(itfs[i]);' ???
                  ret = getMethodRecursively(managedClass, identifier, itfs[i], staticFlag);
                  i++;
               }
               superparam = superparam.getSuperclass();
            }
         }
      }
      
      // If nothing has been found so far, search for 'default' method for identifier, the one with a Message as parameter.
      if ((ret == null) && (!parameter.equals(Message.class)))
      {
         ret = getMethodRecursively(managedClass, identifier, Message.class, staticFlag);
      }
      return ret;
   }

   /**
    * Gets the method map for a class.
    *
    * @param managedClass the class to map
    * @return the sub method map
    */
   private static Map<String, Map<Class<?>, Method>> getSubMethodMap(Class<?> managedClass, boolean staticFlag)
   {
      // Test silly call
      if (managedClass == null)
      {
         return new ConcurrentHashMap<>();
      }

      // Test if we already have done the work.
      Map<String, Map<Class<?>, Method>> methodmap = (staticFlag) ? staticClassesmap.get(managedClass) : instanceClassesmap.get(managedClass);
      if (methodmap != null)
      {
         return methodmap;
      }

      // Search for compatible methods in interfaces (for default methods) and superclass.
      methodmap = new ConcurrentHashMap<>();
      if (staticFlag)
      {
         staticClassesmap.put(managedClass, methodmap);
      }
      else
      {
         instanceClassesmap.put(managedClass, methodmap);
      }
      
      if (!managedClass.equals(Object.class))
      {
         for (Class<?> class1 : managedClass.getInterfaces())
         {
            methodmap.putAll(copyMethodsMap(class1, staticFlag));
         }
         methodmap.putAll(copyMethodsMap(managedClass.getSuperclass(), staticFlag));
      }

      // Get methods declared in class itself
      Method[] methods = managedClass.getDeclaredMethods();
      for (Method method : methods)
      {
         // Usable listening methods have only one parameter
         if (method.getParameterCount() == 1)
         {
            String name = method.getName();
            // If the method is annoted with @Receive and analyse Receive annotation
            if ((method.isAnnotationPresent(Receive.class)) || (method.isAnnotationPresent(Receives.class)))
            {
               Receive[] annotations = method.getAnnotationsByType(Receive.class);
               for (Receive annotation : annotations)
               {
                  // TODO ?????
                  // Yep, that's one ! Register in map.
                  registerListeningMethod(annotation.message(), method, methodmap);
                  // Search for all identifiers listed in messages
                  for( String id : annotation.messages())
                  {
                     registerListeningMethod(id, method, methodmap);
                  }
               }
            }
            else if ((name.startsWith(METHODHEADER)) && (name.endsWith(METHODFOOTER)))
            {
               // Analyse method name to guess the identifier name.
               String id = name.substring(METHODHEADER.length(), name.length() - METHODFOOTER.length());
               registerListeningMethod(id, method, methodmap);
            }
         }
      }
      return methodmap;
   }
   
   /**
    * Deep copy for methods map to avoid messing with already created maps (for subclasses, interfaces... ).
    *
    * @param subclass the sub class to copy method map from
    * @return the map
    */
   private static Map<String, Map<Class<?>, Method>> copyMethodsMap(Class<?> subclass, boolean staticFlag)
   {
      Map<String, Map<Class<?>, Method>> methodmap = new HashMap<>();
      
      // Warning : copy content, do not just link to it.
      Map<String, Map<Class<?>, Method>> supermap = getSubMethodMap(subclass, staticFlag);
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
