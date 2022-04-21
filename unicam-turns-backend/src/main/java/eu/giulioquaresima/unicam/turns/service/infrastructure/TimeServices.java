package eu.giulioquaresima.unicam.turns.service.infrastructure;

import java.time.Clock;

public interface TimeServices
{
	default Clock getSystemClock()
	{
		return Clock.systemUTC();
	}
}
