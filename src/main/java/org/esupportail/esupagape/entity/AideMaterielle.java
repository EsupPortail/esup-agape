package org.esupportail.esupagape.entity;

import org.esupportail.esupagape.entity.enums.TypeAideMaterielle;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class AideMaterielle {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeAideMaterielle typeAideMaterielle;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate;

    @NotNull
    private Double cost;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @ManyToOne(optional = false)
    private Dossier dossier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeAideMaterielle getTypeAideMaterielle() {
        return typeAideMaterielle;
    }

    public void setTypeAideMaterielle(TypeAideMaterielle type) {
        this.typeAideMaterielle = type;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String commentaire) {
        this.comment = commentaire;
    }

    public Dossier getDossier() {
        return dossier;
    }

    public void setDossier(Dossier dossier) {
        this.dossier = dossier;
    }
}
