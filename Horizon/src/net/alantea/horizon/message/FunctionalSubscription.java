package net.alantea.horizon.message;

/**
 * The functional interface FunctionalSubscription.
 */
@FunctionalInterface
public interface FunctionalSubscription
{
   /**
    * On message.
    *
    * @param message the message
    */
   public void onMessage(Message message);
}
