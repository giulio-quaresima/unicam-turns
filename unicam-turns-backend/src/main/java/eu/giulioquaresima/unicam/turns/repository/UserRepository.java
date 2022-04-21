package eu.giulioquaresima.unicam.turns.repository;

import java.util.Optional;

import eu.giulioquaresima.unicam.turns.domain.entities.User;

public interface UserRepository extends LayerSupertype<User>
{
	Optional<User> findOneByUsername(String username);
}
