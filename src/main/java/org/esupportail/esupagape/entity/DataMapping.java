package org.esupportail.esupagape.entity;

import jakarta.persistence.*;
import org.esupportail.esupagape.entity.enums.DataType;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"entityName", "attributName", "sourceType", "destinationType", "sourceValue"}
        )
)
public class DataMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", allocationSize = 1)
    private Long id;

    private String entityName;

    private String attributName;

    @Enumerated(EnumType.STRING)
    private DataType sourceType;

    @Enumerated(EnumType.STRING)
    private DataType destinationType;

    @Column(nullable = false)
    private String sourceValue;

    @Column(nullable = false)
    private String destinationValue;

    private String libelle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getAttributName() {
        return attributName;
    }

    public void setAttributName(String attributName) {
        this.attributName = attributName;
    }

    public DataType getSourceType() {
        return sourceType;
    }

    public void setSourceType(DataType sourceType) {
        this.sourceType = sourceType;
    }

    public DataType getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(DataType destinationType) {
        this.destinationType = destinationType;
    }

    public String getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(String sourceValue) {
        this.sourceValue = sourceValue;
    }

    public String getDestinationValue() {
        return destinationValue;
    }

    public void setDestinationValue(String destinationValue) {
        this.destinationValue = destinationValue;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}