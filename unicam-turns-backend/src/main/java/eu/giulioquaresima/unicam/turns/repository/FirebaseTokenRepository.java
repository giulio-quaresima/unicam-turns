package eu.giulioquaresima.unicam.turns.repository;

import java.util.Optional;

import eu.giulioquaresima.unicam.turns.domain.entities.FirebaseToken;

public interface FirebaseTokenRepository extends LayerSupertype<FirebaseToken>
{
	Optional<FirebaseToken> findOneByToken(String token);
}
