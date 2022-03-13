package eu.giulioquaresima.unicam.turns.domain.service;

import java.time.Clock;

public interface ClockService
{
	/**
	 * Get the clock user for all the operations.
	 * 
	 * @return
	 */
	Clock getClock();
}
