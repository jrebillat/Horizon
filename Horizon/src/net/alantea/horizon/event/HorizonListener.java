package net.alantea.horizon.event;

/**
 * The listener interface for receiving horizon events.
 * It is merely a marker to set the class as usable as listener.
 * The implementing class should have some "onXxxxxEvent(HorizonEvent event)" methods,
 * with Xxxxxx being an event Id.
 * The class that is interested in processing a horizon
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addHorizonListener</code> method. When
 * the horizon event occurs, that object's appropriate
 * method is invoked.
 *
 * @see HorizonEvent
 */
public interface HorizonListener
{

}
