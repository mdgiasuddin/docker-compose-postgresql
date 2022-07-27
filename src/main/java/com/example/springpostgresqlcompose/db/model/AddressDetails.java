package com.example.springpostgresqlcompose.db.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "address_details")
@NoArgsConstructor
public class AddressDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private Student student;

    @Column(name = "address")
    private String address;

    @Column(name = "upazila")
    private String upazila;

    @Column(name = "district")
    private String district;
}
