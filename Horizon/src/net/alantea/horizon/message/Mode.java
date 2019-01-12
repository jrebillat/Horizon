package net.alantea.horizon.message;

/**
 * The Enum Mode.
 */
public enum Mode
{
   /** A thread is used to deliver messages in order they are generated. */
   THREADED,
   /** A thread is used to thread messages (one thread generating one thread per message). */
   HYPERTHREADED,
   /** Messages are threaded in order (one thread per message) from the main thread. */
   CONCURRENT,
   /** Messages are processed in order from the main thread. */
   SYNCHRONOUS
};