/**
 * This package contains a pretty tricky event mechanism between objects in Java.
 * Any object implementing the <code>HorizonSource</code> interface may generate
 * events taht will be sent to any <code>HorizonListener</code> object that will have
 * been registered either to this specific source events or to this specific type of
 * event.
 * <p>
 * Basically, listeners are informed using a specific method :
 *  <br>
 *  The receiver class - or one of its ancestors - must have declared a method
 *  <code>onXxxxEvent(HorizonEvent event)</code>, with Xxxx being the Id of the sent
 *  event. This method is called for every listened event.
 *  <p>
 *  You may change the pattern of the searched method or even specify several patterns.
 *  <br>
 *  The event manager has two different modes. It may work using threads, throwing events
 *  to listeners each in a different thread. This allows to build fast parallel applications
 *  but you will never know what will be processed first.
 *  <br>
 *  The second mode is to queue event sending. This way, the timing is predictable but the
 *  manager will wait for a listener to finish processing the event before sending it to the
 *  next listener, or to process next event.
 *  In this mode events are sent synchronously and processed one after one, in a specialized thread.
 *  This thread is a daemon, thus it will stop, regardless of the remaining messages, as soon
 *  as the main thread stops.
 * 
 * @author Jean Rébillat
 *
 */
package net.alantea.horizon.event;