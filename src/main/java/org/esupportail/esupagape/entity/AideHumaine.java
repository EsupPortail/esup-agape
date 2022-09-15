package org.esupportail.esupagape.entity;

import com.sun.istack.NotNull;
import org.esupportail.esupagape.entity.enums.Status;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "AideHumaine", uniqueConstraints = {
        @UniqueConstraint(name = "uc_aidehumaine_numetuaide", columnNames = {"numEtuAide"})
})
public class AideHumaine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated
    private Status status;

    @Column(unique = true, nullable = false)
    private int numEtuAide;

    @NotNull
    private String name;

    @NotNull
    private String firstName;


    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateOfBirth;

    private String email;

    private int phone;


    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate startDate;

    private String function;

    @OneToMany(cascade = CascadeType.ALL)
    private List<FeuilleHeures> feuilleHeures = new ArrayList<>();

    public AideHumaine() {

    }

    public AideHumaine(Long id, Status status, int numEtuAide, String name, String firstName, LocalDate dateOfBirth, String email, int phone, LocalDate startDate, String function) {
        this.id = id;
        this.status = status;
        this.numEtuAide = numEtuAide;
        this.name = name;
        this.firstName = firstName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phone = phone;
        this.startDate = startDate;
        this.function = function;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getNumEtuAide() {
        return numEtuAide;
    }

    public void setNumEtuAide(int numEtuAide) {
        this.numEtuAide = numEtuAide;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}
