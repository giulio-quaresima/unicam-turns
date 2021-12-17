package eu.giulioquaresima.unicam.turns.test.integration.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import eu.giulioquaresima.unicam.turns.domain.entities.Location;
import eu.giulioquaresima.unicam.turns.domain.entities.Service;
import eu.giulioquaresima.unicam.turns.domain.entities.Session;
import eu.giulioquaresima.unicam.turns.domain.entities.SessionConfiguration;
import eu.giulioquaresima.unicam.turns.domain.entities.Tenant;
import eu.giulioquaresima.unicam.turns.domain.entities.TicketSourceConfiguration;
import eu.giulioquaresima.unicam.turns.domain.entities.User;
import eu.giulioquaresima.unicam.turns.repository.LocationRepository;
import eu.giulioquaresima.unicam.turns.repository.ServiceRepository;
import eu.giulioquaresima.unicam.turns.repository.SessionConfigurationRepository;
import eu.giulioquaresima.unicam.turns.repository.SessionRepository;
import eu.giulioquaresima.unicam.turns.repository.TenantRepository;
import eu.giulioquaresima.unicam.turns.repository.TicketRepository;
import eu.giulioquaresima.unicam.turns.repository.UserRepository;

public class AbstractTest
{
	@Autowired
	protected TenantRepository tenantRepository;
	@Autowired
	protected LocationRepository locationRepository;
	@Autowired
	protected ServiceRepository serviceRepository;
	@Autowired
	protected SessionRepository sessionRepository;
	@Autowired
	protected SessionConfigurationRepository sessionConfigurationRepository;
	@Autowired
	protected TicketRepository ticketRepository;
	@Autowired
	protected UserRepository userRepository;
	
	@BeforeEach
	public void commodityData()
	{
		Service service = createService();
		assertThat(service).isNotNull().matches(s -> "Accettazione".equals(s.getName()));
		assertThat(service.getLocation()).isNotNull();
		assertThat(service.getLocation().getTenant()).isNotNull();
	}
	
	protected Tenant createTenant()
	{
		tenantRepository.save(new Tenant("Superconti Camerino"));
		tenantRepository.save(new Tenant("Studio commerciale \"l'elusionista\""));
		return tenantRepository.save(new Tenant("Studio Medico \"Lo spergiuro di Ippocrate"));
	}
	
	protected Location createLocation()
	{
		Location location = new Location();
		location.setName("Studio centro");
		location.setTenant(createTenant());
		location.setZoneId("Europe/Rome");
		return locationRepository.save(location);
	}
	
	protected Service createService()
	{
		Service service = new Service();
		service.setName("Accettazione");
		service.setLocation(createLocation());
		return serviceRepository.save(service);
	}
	
	protected User createUser(String givenName, String familyName)
	{
		User user = new User(givenName, familyName);
		return userRepository.save(user);
	}
	
	protected Service findTestService()
	{
		List<Service> services = serviceRepository.findAll();
		assertThat(services).size().isEqualTo(1);
		return services.get(0);
	}
	
	protected Session initSessionCommons()
	{
		Service service = findTestService();
		Session session = new Session();
		session.setService(service);
		session.setLabel("Lunedì mattina");
		session.setStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)));
		session.setEndTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0)));
		return session;
	}
	
	protected Session createSequentialDecimalSession()
	{
		SessionConfiguration sessionConfiguration = new SessionConfiguration();
		TicketSourceConfiguration ticketSourceConfiguration = new TicketSourceConfiguration();
		ticketSourceConfiguration.setUseBijectiveNumeration(false);
		sessionConfiguration.setTicketSourceConfiguration(ticketSourceConfiguration);
		
		Session session = initSessionCommons();
		session.setSessionConfiguration(sessionConfigurationRepository.save(sessionConfiguration));
		
		return sessionRepository.save(session);
	}
	
}
