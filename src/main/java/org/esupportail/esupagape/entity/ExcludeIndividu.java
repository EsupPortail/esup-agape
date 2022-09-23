package org.esupportail.esupagape.entity;

import javax.persistence.*;

@Entity
public class ExcludeIndividu {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
