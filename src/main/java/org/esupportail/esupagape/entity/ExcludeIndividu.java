package org.esupportail.esupagape.entity;

import jakarta.persistence.*;

@Entity
public class ExcludeIndividu {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @Column(unique = true)
    String numEtuHash;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumEtuHash() {
        return numEtuHash;
    }

    public void setNumEtuHash(String numEtuHash) {
        this.numEtuHash = numEtuHash;
    }
}
