package eu.giulioquaresima.unicam.turns.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.giulioquaresima.unicam.turns.repository.SessionRepository;

@RepositoryRestController
public class SessionController
{
	@Autowired
	private SessionRepository sessionRepository;
	
	@GetMapping ("/sbirulino")
	public @ResponseBody ResponseEntity<?> sbirusd()
	{
		return ResponseEntity.ok().build();
	}
}
