package io.xydez.north.event;

import java.util.ArrayList;
import java.util.HashMap;

public class EventManager
{
	private final HashMap<Class<? extends Listener>, ArrayList<? extends Listener>> listenerMap = new HashMap<>();

	public <L extends Listener> void addListener(Class<L> listenerClass, L listener)
	{
		if (listenerMap.containsKey(listenerClass))
		{
			@SuppressWarnings("unchecked")
			ArrayList<L> listeners = (ArrayList<L>)listenerMap.get(listenerClass);

			listeners.add(listener);
		}
		else
		{
			ArrayList<L> listeners = new ArrayList<>();
			listeners.add(listener);
			listenerMap.put(listenerClass, listeners);
		}
	}

	public <L extends Listener> void removeListener(Class<L> listenerClass, L listener)
	{
		@SuppressWarnings("unchecked")
		ArrayList<L> listeners = (ArrayList<L>)listenerMap.get(listenerClass);

		listeners.remove(listener);
	}

	public <L extends Listener, E extends Event<L>> void fire(Class<L> listenerClass, E event)
	{
		@SuppressWarnings("unchecked")
		ArrayList<L> listeners = (ArrayList<L>)listenerMap.get(listenerClass);

		event.fire(listeners);
	}
}
