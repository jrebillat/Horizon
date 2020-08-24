package net.alantea.horizon.message;

/**
 * The Interface MessageTarget.
 */
@FunctionalInterface
public interface MessageTarget
{
   
   /**
    * On message.
    *
    * @param message the message
    */
   public void onMessage(Message message);}
