package eu.giulioquaresima.unicam.turns.domain.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import eu.giulioquaresima.unicam.turns.domain.entities.Owner;
import eu.giulioquaresima.unicam.turns.domain.entities.TicketDispenser;
import eu.giulioquaresima.unicam.turns.domain.entities.User;
import eu.giulioquaresima.unicam.turns.repository.OwnerRepository;
import eu.giulioquaresima.unicam.turns.repository.TicketDispenserRepository;

@Service
@Transactional (readOnly = true, propagation = Propagation.SUPPORTS)
public class TicketDispenserServicesImpl implements TicketDispenserServices
{
	@Autowired
	private TicketDispenserRepository ticketDispenserRepository;
	
	@Autowired
	private OwnerRepository ownerRepository;
	
	@Autowired
	private UserServices userServices;

	@Override
	public List<TicketDispenser> listOwnDispensers()
	{
		User user = userServices.getCurrentUser(true);
		
		if (user != null)
		{
			return user
					.getOwners()
					.stream()
					.flatMap(owner -> owner.getTicketDispensers().stream())
					.sorted()
					.collect(Collectors.toList())
					;
		}
		
		return Collections.emptyList();
	}

	@Override
	@Transactional (readOnly = false, propagation = Propagation.REQUIRED)
	public TicketDispenser create(TicketDispenser template) throws IllegalArgumentException, IllegalStateException
	{
		Assert.notNull(template, "template required");
		Assert.hasText(template.getLabel(), "template.label required");
		
		if (template.getOwner() == null)
		{
			template.setOwner(null);
			User user = userServices.getCurrentUser(true);
			if (user != null)
			{
				/* 
				 * Per ora semplifico creando automaticamente uno ed un solo Owner per ogni User,
				 * o utilizzando un Owner a caso se già presente.
				 */
				Owner owner;
				if (user.getOwners().isEmpty())
				{
					owner = ownerRepository.save(new Owner(template.getLabel()));
				}
				else
				{
					owner = user.getOwners().stream().findAny().orElseThrow(AssertionError::new);
				}
				template.setOwner(owner);
				return ticketDispenserRepository.save(template);
			}
		}
		
		return null;
	}

}
