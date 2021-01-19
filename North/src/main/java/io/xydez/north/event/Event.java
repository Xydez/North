package io.xydez.north.event;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class Event<T extends Listener>
{
	public abstract void fire(@Nullable ArrayList<T> listeners);
}
