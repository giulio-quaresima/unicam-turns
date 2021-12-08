package eu.giulioquaresima.unicam.turns.domain.entities;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.SortNatural;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@Entity
public class SessionScheduling extends AbstractEntity<SessionScheduling>
{
	@ElementCollection
	@SortNatural
	private SortedSet<Shift> defaultShifts = new TreeSet<>();
	
	public SortedSet<Shift> getDefaultShifts()
	{
		return defaultShifts;
	}
	public void setDefaultShifts(SortedSet<Shift> defaultShifts)
	{
		this.defaultShifts = defaultShifts;
	}

	@Embeddable
	public static class Shift implements Comparable<Shift>
	{
		public static final Comparator<Shift> COMPARATOR = Comparator
				.comparing(Shift::getDayOfWeek)
				.thenComparing(Shift::getStartTime);
		
		@NotNull
		@Column (nullable = false)
		private String name;
		
		@NotNull
		private DayOfWeek dayOfWeek;
		
		@NotNull
		private LocalTime startTime;
		
		@NotNull
		private Duration duration;
		
		private boolean resetTicketSource;
		
		@Override
		public int compareTo(Shift o)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public DayOfWeek getDayOfWeek()
		{
			return dayOfWeek;
		}

		public void setDayOfWeek(DayOfWeek dayOfWeek)
		{
			this.dayOfWeek = dayOfWeek;
		}

		public LocalTime getStartTime()
		{
			return startTime;
		}

		public void setStartTime(LocalTime startTime)
		{
			this.startTime = startTime;
		}
		
		public Duration getDuration()
		{
			return duration;
		}

		public void setDuration(Duration duration)
		{
			this.duration = duration;
		}

		/**
		 * If <code>true</code>, at the start of this shift
		 * the associated {@link TicketSource} will be reset
		 * and therefore all the previously emitted but not 
		 * consumed tickets will be cancelled.
		 * 
		 * @return
		 */
		public boolean isResetTicketSource()
		{
			return resetTicketSource;
		}

		public void setResetTicketSource(boolean resetTicketSource)
		{
			this.resetTicketSource = resetTicketSource;
		}
	}
}
