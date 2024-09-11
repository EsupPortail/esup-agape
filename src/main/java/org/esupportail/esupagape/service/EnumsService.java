package org.esupportail.esupagape.service;

import org.esupportail.esupagape.entity.enums.EnumEntityAbstract;
import org.esupportail.esupagape.entity.enums.TypeAideMaterielle;
import org.esupportail.esupagape.entity.enums.TypeContact;
import org.esupportail.esupagape.repository.enums.TypeAideMaterielleRepository;
import org.esupportail.esupagape.repository.enums.TypeContactRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class EnumsService {

    Map<String, JpaRepository<?, ?>> enumTypes = new HashMap<>();

    private final TypeAideMaterielleRepository typeAideMaterielleRepository;
    private final TypeContactRepository typeContactRepository;

    public EnumsService(TypeAideMaterielleRepository typeAideMaterielleRepository, TypeContactRepository typeContactRepository) {
        this.typeAideMaterielleRepository = typeAideMaterielleRepository;
        enumTypes.put("TypeAideMaterielle", typeAideMaterielleRepository);
        this.typeContactRepository = typeContactRepository;
        enumTypes.put("TypeContact", typeContactRepository);
    }

    public List<EnumEntityAbstract> getAllEnumEntityAbstract() {
        List<EnumEntityAbstract> enumEntityAbstracts = new ArrayList<>();
        for (JpaRepository<?, ?> enumType : enumTypes.values()) {
            enumEntityAbstracts.addAll((Collection<? extends EnumEntityAbstract>) enumType.findAll());
        }
        return enumEntityAbstracts;
    }

    public List<TypeAideMaterielle> getAllTypeAideMaterielle() {
        return typeAideMaterielleRepository.findAll();
    }

    public List<TypeContact> getAllTypeContact() {
        return typeContactRepository.findAll();
    }

    @Transactional
    public void addEnumType(String enumEntityType, String code, String label) {
        if(enumEntityType.equals("TypeAideMaterielle")) {
            TypeAideMaterielle typeAideMaterielle = new TypeAideMaterielle();
            typeAideMaterielle.setCode(code);
            typeAideMaterielle.setLabel(label);
            typeAideMaterielleRepository.save(typeAideMaterielle);
        } else if(enumEntityType.equals("TypeContact")) {
            TypeContact typeContact = new TypeContact();
            typeContact.setCode(code);
            typeContact.setLabel(label);
            typeContactRepository.save(typeContact);
        }
    }

    public List<String> getEnumTypes() {
        return enumTypes.keySet().stream().toList();
    }

    @Transactional
    public void deleteEnumType(String enumEntityType, Long id) {
        if(enumEntityType.equals("TypeAideMaterielle")) {
            typeAideMaterielleRepository.deleteById(id);
        } else if(enumEntityType.equals("TypeContact")) {
            typeContactRepository.deleteById(id);
        }
    }
}
