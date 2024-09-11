package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.enums.TypeAideMaterielle;
import org.esupportail.esupagape.repository.enums.TypeAideMaterielleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnumsService {

    private final TypeAideMaterielleRepository typeAideMaterielleRepository;

    public EnumsService(TypeAideMaterielleRepository typeAideMaterielleRepository) {
        this.typeAideMaterielleRepository = typeAideMaterielleRepository;
    }

    public List<TypeAideMaterielle> getAllTypeAideMaterielle() {
        return typeAideMaterielleRepository.findAll();
    }

}
