package com.example.springpostgresqlcompose.db.model;

import com.example.springpostgresqlcompose.enums.Gender;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "student_table")
@NoArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "school_roll_no")
    private Long schoolRollNo;

    @Column(name = "class_id")
    private String classId;

    @Column(name = "class_id_actual")
    private String classIdActual;

    @Column(name = "roll_no")
    private Long rollNo;

    @Column(name = "reg_no")
    private Long regNo;

    @Column(name = "verification_no")
    private String verificationNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "marks")
    private Double marks;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "merit_position")
    private Integer meritPosition;

    @OneToOne(mappedBy = "student")
    private AddressDetails addressDetails;
}
