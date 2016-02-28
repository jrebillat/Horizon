package net.alantea.horizon.event;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class HorizonEventManager.
 */
public final class HorizonEventManager
{
   /** Default regexp pattern. */
   private static final String DEFAULTPATTERN = "^on([\\S]+)Event$";
   
   private static final int DEFAULTINTERVAL = 250;
   
   /** The Regexp list */
   private static List<Pattern> regexplist = new CopyOnWriteArrayList<>();
   
   /** The source map. */
   private static Map<HorizonSource, List<HorizonListener>> sourcemap = new ConcurrentHashMap<>();
   
   /** The listener map. */
   private static Map<Class<? extends HorizonListener>, Map<String, Method>> listenermap = new ConcurrentHashMap<>();
   
   /** The static listener list. */
   private static List<HorizonListener> staticlistenerlist = new CopyOnWriteArrayList<>();
   
   /** The queued messages list. */
   private static ConcurrentLinkedQueue<HorizonEvent> queuelist = new ConcurrentLinkedQueue<>();
   
   /** Tells if the manager is queued. */
   private static boolean queued = false;
   
   /** Tells the queue to wait for recomputing. */
   private static boolean recomputing = false;
   
   /** The queue thread. */
   private static Thread queueThread;
   
   /** The interval between polling. */
   private static int interval = DEFAULTINTERVAL;
   
   /**
    * Sets the manager queued or not. Better do it before starting to send events...
    * By default, the manager is not queued.
    *
    * @param flag the new queued
    */
   public static void setQueued(boolean flag)
   {
      queued = flag;
   }
   
   public static void setInterval(int value)
   {
      if (value > 0)
      {
         interval = value;
      }
   }
   
   /**
    * Adds a regexp to the list of expected event listening methods. This regexp should just
    * follow one single rule : have a group defined, that will contain the event id. For
    * example, "^on([\\S]+)Event$" allows to catch the "Test" event in the
    * onTestEvent(HorizonEvent event) method.
    *
    * @param regexp the regexp
    */
   static final boolean addRegexp(String regexp)
   {
      if ((regexp == null) || (!regexp.matches("^.*\\(.+\\).*$")))
      {
         return false;
      }

      regexplist.add(Pattern.compile(regexp));
      return true;
   }
   
   /**
    * Sets a regexp and wipe the list of expected event listening methods. This regexp should just
    * follow one single rule : have a group defined, that will contain the event id. For
    * example, "^on([\\S]+)Event$" allows to catch the "Test" event in the
    * onTestEvent(HorizonEvent event) method.
    *
    * @param regexp the regexp
    */
   static final boolean setRegexp(String regexp)
   {
      if ((regexp == null) || (!regexp.matches("^.*\\(.+\\).*$")))
      {
         return false;
      }

      recomputing = true;
      regexplist.clear();
      regexplist.add(Pattern.compile(regexp));
      recomputeMappings();
      recomputing = false;
      return true;
   }
   
   /**
    * Adds a horizon listener to a specific source.
    *
    * @param horizonSource the horizon source
    * @param listener the listener
    */
   static final boolean addHorizonListener(HorizonSource horizonSource, HorizonListener listener)
   {
      if ((horizonSource == null) || (listener == null))
      {
         return false;
      }
      
      synchronized(sourcemap)
      {
         List<HorizonListener> listeners = sourcemap.get(horizonSource);
         
         if (listeners == null)
         {
            listeners = new CopyOnWriteArrayList<>();
            sourcemap.put(horizonSource, listeners);
         }
         if (!listeners.contains(listener))
         {
            listeners.add(listener);
            parseMethods(listener);
         }
      }
      return true;
   }

   /**
    * Removes a horizon listener from a specific source.
    *
    * @param horizonSource the horizon source
    * @param listener the listener
    */
   static final boolean removeHorizonListener(HorizonSource horizonSource, HorizonListener listener)
   {
      synchronized(sourcemap)
      {
         List<HorizonListener> listeners = sourcemap.get(horizonSource);
         
         if (listeners != null)
         {
            if (listeners.contains(listener))
            {
               listeners.remove(listener);
               return true;
            }
         }
      }
      return false;
   }

   /**
    * Removes all horizon listeners from a specific source.
    *
    * @param horizonSource the horizon source
    */
   static final boolean removeAllHorizonListeners(HorizonSource horizonSource)
   {
      synchronized(sourcemap)
      {
         List<HorizonListener> listeners = sourcemap.get(horizonSource);
         
         if (listeners != null)
         {
            listeners.clear();
            return true;
         }
      }
      return false;
   }

   /**
    * Adds a static horizon listener to all managed events.
    *
    * @param listener the listener
    * @return true, if successful
    */
   public static final boolean addStaticHorizonListener(HorizonListener listener)
   {
      if (listener == null)
      {
         return false;
      }
      
      synchronized(staticlistenerlist)
      {
         if (!staticlistenerlist.contains(listener))
         {
            staticlistenerlist.add(listener);
            parseMethods(listener);
         }
      }
      return true;
   }

   /**
    * Removes a horizon listener.
    *
    * @param listener the listener
    * @return true, if successful
    */
   public static final boolean removeStaticHorizonListener(HorizonListener listener)
   {
      if (listener == null)
      {
         return false;
      }
      
      synchronized(staticlistenerlist)
      {         
         if (staticlistenerlist.contains(listener))
         {
            staticlistenerlist.remove(listener);
            return true;
         }
      }
      return false;
   }

   /**
    * Removes all static horizon listeners.
    *
    * @return true, if successful
    */
   public static final boolean removeAllStaticHorizonListeners()
   {
      synchronized(staticlistenerlist)
      {         
         staticlistenerlist.clear();
      }
      return true;
   }
   
   /**
    * Recompute all event management mappings.
    */
   public static final void recomputeMappings()
   {
      synchronized(listenermap)
      {
         listenermap.clear();
         sourcemap.values().forEach((list) ->
         {
            list.forEach((listener) -> parseMethods(listener));
         });
         
         staticlistenerlist.forEach((listener) -> parseMethods(listener));
      }
   }

   /**
    * Send event.
    *
    * @param event the event
    */
   static final void sendEvent(HorizonEvent event)
   {
      if (!queued)
      {
         new Thread(() -> reallySendEvent(event)).start();
      }
      else
      {
         synchronized(queuelist)
         {
            queuelist.add(event);
            if (queueThread == null)
            {
               startQueueThread();
            }
         }
      }
   }

   /**
    * Send event.
    *
    * @param event the event
    */
   private static final void reallySendEvent(HorizonEvent event)
   {
      // execute static listeners
      staticlistenerlist.forEach((listener)->
      {
         new Thread(() -> execute(listener, event)).start();
      });

      // search specific listeners
      HorizonSource source = event.getSource();
      List<HorizonListener> listeners = sourcemap.get(source);
      if (listeners != null)
      {
         // execute specific listeners
         listeners.forEach((listener)->
         {
            new Thread(() -> execute(listener, event)).start();
         });
      }
   }

   /**
    * Send queued event.
    *
    * @param event the event
    */
   private static final void reallySendQueuedEvent(HorizonEvent event)
   {
      // execute static listeners
      staticlistenerlist.forEach((listener)->
      {
         execute(listener, event);
      });

      // search specific listeners
      HorizonSource source = event.getSource();
      List<HorizonListener> listeners = sourcemap.get(source);
      if (listeners != null)
      {
         // execute specific listeners
         listeners.forEach((listener)->
         {
            execute(listener, event);
         });
      }
   }
   
   /**
    * Start queue thread.
    */
   private static final void startQueueThread()
   {
      queueThread = new Thread(() ->
      {
         while(true)
         {
            HorizonEvent evt;
            if (!recomputing)
            {
               do
               {
                  evt = queuelist.poll();
                  if (evt != null)
                  {
                     reallySendQueuedEvent(evt);
                  }
               }
               while (evt != null);
            }
            try
            {
               Thread.sleep(interval);
            }
            catch (Exception e)
            {
               // really : don't care !
            }
         }
      });
      queueThread.setDaemon(true);
      queueThread.start();
   }
   
   /**
    * Execute the event method on the listener.
    *
    * @param listener the listener
    * @param event the event
    */
   private static void execute(HorizonListener listener, HorizonEvent event)
   {
      Map<String, Method> methods = listenermap.get(listener.getClass());
      Method method = methods.get(event.getId());
      if (method != null)
      {
         try
         {
            method.invoke(listener, event);
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }

   /**
    * Parses the methods.
    *
    * @param listener the listener
    */
   private static final void parseMethods(HorizonListener listener)
   {
      // First call : add default regexp
      if (regexplist.isEmpty())
      {
         addRegexp(DEFAULTPATTERN);
      }
      
      Class<? extends HorizonListener> theClass = listener.getClass();
      if (!listenermap.containsKey(theClass))
      {
         // Search for compatible methods
         Map<String, Method> methodmap = new ConcurrentHashMap<>();
         Method[] methods = theClass.getMethods();
         for (Method method : methods)
         {
            if ((method.getParameterCount() == 1)
                  && (method.getParameterTypes()[0].isAssignableFrom(HorizonEvent.class)))
            {
               String name = method.getName();
               for (Pattern pat : regexplist)
               {
                  Matcher match = pat.matcher(name);
                  if (match.matches())
                  {
                     String key = match.group(1);
                     method.setAccessible(true);
                     methodmap.put(key, method);
                  }
               }
            }
         }
         listenermap.put(theClass,  methodmap);
      }
   }
}
