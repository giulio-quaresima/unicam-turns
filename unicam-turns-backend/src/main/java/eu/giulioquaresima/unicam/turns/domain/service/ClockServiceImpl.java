package eu.giulioquaresima.unicam.turns.domain.service;

import java.time.Clock;

import org.springframework.stereotype.Service;

@Service
public class ClockServiceImpl implements ClockService
{
	private Clock clock = Clock.systemUTC();

	@Override
	public Clock getClock()
	{
		return clock;
	}

}
