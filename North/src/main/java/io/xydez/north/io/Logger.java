package io.xydez.north.io;

import io.xydez.north.utility.Utility;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;

public class Logger implements AutoCloseable
{
	private static int loggers = 0;
	private final Level level;

	public Logger(Level level)
	{
		this.level = level;

		if (loggers == 0)
		{
			try
			{
				AnsiConsole.out().install();
			} catch (IOException ignored) {}
		}

		loggers += 1;
	}

	public void trace(String text, Object... args)
	{
		log(Level.Trace, text, args);
	}

	public void info(String text, Object... args)
	{
		log(Level.Info, text, args);
	}

	public void warn(String text, Object... args)
	{
		log(Level.Warning, text, args);
	}

	public void error(String text, Object... args)
	{
		log(Level.Error, text, args);
	}

	public void log(Level level, String text, Object... args)
	{
		if (level.level < this.level.level)
			return;

		Calendar calendar = Calendar.getInstance();
		String timeString = String.format(
			"%s:%s:%s.%s",
			Utility.padString(Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)), 2, Utility.Align.Right, "0"),
			Utility.padString(Integer.toString(calendar.get(Calendar.MINUTE)), 2, Utility.Align.Right, "0"),
			Utility.padString(Integer.toString(calendar.get(Calendar.SECOND)), 2, Utility.Align.Right, "0"),
			Utility.padString(Integer.toString(calendar.get(Calendar.MILLISECOND)), 3, Utility.Align.Right, "0")
		);

		AnsiConsole.out().println(level.format(String.format("%s [%s] [%s] %s", timeString, level.name, Thread.currentThread().getName(), String.format(text, args))));
	}

	@Override
	public void close()
	{
		loggers -= 1;

		if (loggers == 0)
		{
			try
			{
				AnsiConsole.out().uninstall();
			} catch (IOException ignored) {}
		}
	}

	public enum Level
	{
		Trace("TRACE", "%s", 0), Info("INFO", "\u001b[32m%s\u001b[0m", 1), Warning("WARN", "\u001b[33m%s\u001b[0m", 2), Error("ERROR", "\u001b[31m%s\u001b[0m", 3);

		private final String name;
		private final String template;
		private final int level;

		Level(String name, String template, int level)
		{
			this.name = name;
			this.template = template;
			this.level = level;
		}

		public String format(String text)
		{
			return String.format(this.template, text);
		}

		@Override
		public String toString()
		{
			return this.name;
		}
	}
}
