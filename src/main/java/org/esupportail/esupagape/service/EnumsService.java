package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.enums.TypeAideMaterielle;
import org.esupportail.esupagape.entity.enums.TypeContact;
import org.esupportail.esupagape.repository.enums.TypeAideMaterielleRepository;
import org.esupportail.esupagape.repository.enums.TypeContactRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnumsService {

    private final TypeAideMaterielleRepository typeAideMaterielleRepository;
    private final TypeContactRepository typeContactRepository;

    public EnumsService(TypeAideMaterielleRepository typeAideMaterielleRepository, TypeContactRepository typeContactRepository) {
        this.typeAideMaterielleRepository = typeAideMaterielleRepository;
        this.typeContactRepository = typeContactRepository;
    }

    public List<TypeAideMaterielle> getAllTypeAideMaterielle() {
        return typeAideMaterielleRepository.findAll();
    }

    public List<TypeContact> getAllTypeContact() {
        return typeContactRepository.findAll();
    }

}
