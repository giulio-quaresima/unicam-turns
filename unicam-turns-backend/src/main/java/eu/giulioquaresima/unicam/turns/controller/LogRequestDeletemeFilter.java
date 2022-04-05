package eu.giulioquaresima.unicam.turns.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated Delete me as soon as you no longer need me!
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@WebFilter
@Deprecated
public class LogRequestDeletemeFilter implements Filter
{
	private static final Logger LOGGER = LoggerFactory.getLogger(LogRequestDeletemeFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		LOGGER.info("REMOTE ADDRESS:  " + request.getRemoteAddr());
		LOGGER.info("REMOTE HOST:     " + request.getRemoteHost());
		if (request instanceof HttpServletRequest)
		{
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			LOGGER.info("X-FORWARDED-FOR: " + httpServletRequest.getHeader("X-FORWARDED-FOR"));	
		}
		chain.doFilter(request, response);
	}

}
