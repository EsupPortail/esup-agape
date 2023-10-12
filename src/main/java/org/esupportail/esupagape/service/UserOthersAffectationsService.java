package org.esupportail.esupagape.service;


import org.esupportail.esupagape.entity.UserOthersAffectations;
import org.esupportail.esupagape.repository.UserOthersAffectationsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserOthersAffectationsService {

    private final UserOthersAffectationsRepository userOthersAffectationsRepository;


    public UserOthersAffectationsService(UserOthersAffectationsRepository userOthersAffectationsRepository) {
        this.userOthersAffectationsRepository = userOthersAffectationsRepository;
    }


    public UserOthersAffectations save(UserOthersAffectations userOthersAffectations) {
        return userOthersAffectationsRepository.save(userOthersAffectations);
    }

    public Optional<UserOthersAffectations> findById(Long id) {
        return userOthersAffectationsRepository.findById(id);
    }

    @Transactional
    public void deleteUserOthersAffectations(Long id) {
        UserOthersAffectations userOthersAffectations = userOthersAffectationsRepository.findById(id).orElseThrow();
        userOthersAffectationsRepository.deleteById(userOthersAffectations.getId());
    }

    public void addUserOthersAffectations(String uid, String codComposante) {
        UserOthersAffectations userOthersAffectations = new UserOthersAffectations();
        userOthersAffectations.setUid(uid);
        userOthersAffectations.setCodComposante(codComposante.toUpperCase());
        userOthersAffectationsRepository.save(userOthersAffectations);

    }

}
