package org.example.entity;

import jakarta.persistence.*;

@Entity
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "ref_key")
    private String refKey;
    @Column(name = "name")
    private String name;


}
