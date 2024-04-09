package com.example.consul.models;

import jakarta.persistence.*;

@Entity
public class ApiKey {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
}
