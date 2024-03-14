package org.esupportail.esupagape.entity;

import jakarta.persistence.*;

@Entity
public class Year {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true)
    private Integer number;

    public Year() {
    }

    public Year(Integer number) {
        this.number = number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
