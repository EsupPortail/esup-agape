package org.esupportail.esupagape.entity;

import org.esupportail.esupagape.entity.enums.TypeContact;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Entretien
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "La date doit être renseignée")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime date;

    @NotEmpty(message = "Ce champ doit être renseigné")
    private String interlocuteur;

    @NotEmpty(message = "Ce champ doit être renseigné")
    @Column(columnDefinition = "TEXT")
    private String compteRendu;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TypeContact typeContact;

    @ManyToOne
    private Dossier dossier;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> attachments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getInterlocuteur() {
        return interlocuteur;
    }

    public void setInterlocuteur(String interlocuteur) {
        this.interlocuteur = interlocuteur;
    }

    public String getCompteRendu() {
        return compteRendu;
    }

    public void setCompteRendu(String compteRendu) {
        this.compteRendu = compteRendu;
    }

    public TypeContact getTypeContact() {
        return typeContact;
    }

    public void setTypeContact(TypeContact typeContact) {
        this.typeContact = typeContact;
    }

    public Dossier getDossier() {
        return dossier;
    }

    public void setDossier(Dossier dossier) {
        this.dossier = dossier;
    }

    public List<Document> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Document> attachments) {
        this.attachments = attachments;
    }
}
