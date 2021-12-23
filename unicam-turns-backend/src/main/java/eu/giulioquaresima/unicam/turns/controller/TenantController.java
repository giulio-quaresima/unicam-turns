package eu.giulioquaresima.unicam.turns.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.giulioquaresima.unicam.turns.domain.entities.Tenant;
import eu.giulioquaresima.unicam.turns.repository.TenantRepository;

@RestController
@RequestMapping ("/tenants")
public class TenantController
{
	@Autowired
	private TenantRepository tenantRepository;
	
	@GetMapping
	public Collection<Tenant> tenants()
	{
		return tenantRepository.findAll();
	}
	
	@GetMapping ("/{tenant}")
	public Tenant tenant(Tenant tenant)
	{
		return tenant;
	}
	
}
