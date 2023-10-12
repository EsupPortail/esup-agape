package org.esupportail.esupagape.repository;

import org.esupportail.esupagape.entity.UserOthersAffectations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserOthersAffectationsRepository extends JpaRepository <UserOthersAffectations, Long> {
    List<UserOthersAffectations> findByUid(String uid);
    List<UserOthersAffectations> findByUidAndCodComposante(String uid, String codComposante);
    List<UserOthersAffectations> findByCodComposante(String codComposante);
}
