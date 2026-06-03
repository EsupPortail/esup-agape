package org.esupportail.esupagape.dtos.forms;

import jakarta.validation.constraints.NotNull;
import org.esupportail.esupagape.entity.enums.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AmenagementUpdateDto {

    private TypeAmenagement typeAmenagement;
    private LocalDateTime endDate;
    private Set<TypeEpreuve> typeEpreuves = new HashSet<>();
    private String autresTypeEpreuve;
    @NotNull
    private TempsMajore tempsMajore;
    private String autresTempsMajores;
    private Autorisation autorisation;
    private Set<Classification> classification = new HashSet<>();
    private List<LigneAmenagementDto> lignesAmenagement = new ArrayList<>();

    public TypeAmenagement getTypeAmenagement() {
        return typeAmenagement;
    }

    public void setTypeAmenagement(TypeAmenagement typeAmenagement) {
        this.typeAmenagement = typeAmenagement;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Set<TypeEpreuve> getTypeEpreuves() {
        return typeEpreuves;
    }

    public void setTypeEpreuves(Set<TypeEpreuve> typeEpreuves) {
        this.typeEpreuves = typeEpreuves;
    }

    public String getAutresTypeEpreuve() {
        return autresTypeEpreuve;
    }

    public void setAutresTypeEpreuve(String autresTypeEpreuve) {
        this.autresTypeEpreuve = autresTypeEpreuve;
    }

    public TempsMajore getTempsMajore() {
        return tempsMajore;
    }

    public void setTempsMajore(TempsMajore tempsMajore) {
        this.tempsMajore = tempsMajore;
    }

    public String getAutresTempsMajores() {
        return autresTempsMajores;
    }

    public void setAutresTempsMajores(String autresTempsMajores) {
        this.autresTempsMajores = autresTempsMajores;
    }

    public Autorisation getAutorisation() {
        return autorisation;
    }

    public void setAutorisation(Autorisation autorisation) {
        this.autorisation = autorisation;
    }

    public Set<Classification> getClassification() {
        return classification;
    }

    public void setClassification(Set<Classification> classification) {
        this.classification = classification;
    }

    public List<LigneAmenagementDto> getLignesAmenagement() {
        return lignesAmenagement;
    }

    public void setLignesAmenagement(List<LigneAmenagementDto> lignesAmenagement) {
        this.lignesAmenagement = lignesAmenagement;
    }
}