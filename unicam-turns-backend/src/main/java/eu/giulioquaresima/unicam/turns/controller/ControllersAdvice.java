package eu.giulioquaresima.unicam.turns.controller;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllersAdvice
{
	@InitBinder
	public void initBinder(WebDataBinder webDataBinder)
	{
		webDataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
}
