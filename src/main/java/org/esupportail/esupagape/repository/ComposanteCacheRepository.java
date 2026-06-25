package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.ComposanteCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComposanteCacheRepository extends JpaRepository<ComposanteCache, Long> {
    List<ComposanteCache> findAllByOrderByCodeAsc();
    Optional<ComposanteCache> findByCode(String code);
}
