/**
 * This package contains a pretty simple messaging mechanism between objects in Java.
 * Any object implementing the <code>HorizonSubscriber</code> interface may send a
 * message to any other  <code>HorizonSubscriber</code> object, using the method :
 * <code>sender.sendMessage(HorizonSubscriber receiver, String id, Object content);</code>.
 *  <p>
 *  The receiver will receive an <code>HorizonMessage</code> through one of the two
 *  following mechanism :
 *  <br>
 *  A - If the receiver class - or one of its ancestors - has declared a method
 *  <code>onXxxxMessage(HorizonMessage message)</code>, with Xxxx being the Id of the sent
 *  message, then this method is called.
 *  <br>
 *  B - If no corresponding method exists, then the default receiver method
 *  <code>onMessage(HorizonMessage message)</code> is called.
 *  <p>
 *  Messages are sent asynchronously and processed one after one, in a specialized thread.
 *  This thread is a daemon, thus it will stop, regardless of the remaining messages, as soon
 *  as the main thread stops.
 * 
 * @author Jean Rébillat
 *
 */
package net.alantea.horizon.message;