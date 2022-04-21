package eu.giulioquaresima.unicam.turns.security;

/**
 * 
 * 
 * @author Giulio Quaresima (giulio.quaresima--at--gmail.com)
 */
@FunctionalInterface
public interface Permission
{
	boolean satisfies(Object requiredPermission);
}
