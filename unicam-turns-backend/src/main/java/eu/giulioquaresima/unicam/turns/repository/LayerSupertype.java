package eu.giulioquaresima.unicam.turns.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import eu.giulioquaresima.unicam.turns.domain.entities.AbstractEntity;

@NoRepositoryBean
public interface LayerSupertype<E extends AbstractEntity<?>> extends JpaRepository<E, Integer>, JpaSpecificationExecutor<E>
{
}
