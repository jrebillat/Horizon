/**
 * This package contains a pretty simple messaging mechanism between objects in Java.
 * Any object (mainly those implementing the <code>HorizonSubscriber</code> interface) may send a
 * message to any other object (preferred is <code>HorizonSubscriber</code> object), using the method :
 * <code>sender.sendMessage(HorizonSubscriber receiver, String id, Object content);</code> if it
 * is an HorizonSubscriber, or one of the HorizonMessageManager.sendMessage(...) methods.
 *  <p>
 *  The receiver will receive an <code>HorizonMessage</code> through one of the three
 *  following mechanism :
 *  <br>
 *  A - Search for a receiver method marked as @Listen with the corresponding Identifier and
 *  call it.
 *  <p>
 *  B - If the receiver class - or one of its ancestors - has declared a method
 *  <code>onXxxxMessage(HorizonMessage message)</code>, with Xxxx being the Id of the sent
 *  message, then this method is called.
 *  <br>
 *  C - If no corresponding method exists, then the default receiver method
 *  <code>onMessage(HorizonMessage message)</code> is searched for and called.
 *  <p>
 *  Messages are sent asynchronously and processed one after one, in a specialized thread.
 *  This thread is a daemon, thus it will stop, regardless of the remaining messages, as soon
 *  as the main thread stops.
 * 
 * @author Jean Rébillat
 *
 */
package net.alantea.horizon.message;