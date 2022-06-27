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

    public Student(String name, String schoolName, Long schoolRollNo, String classId, Long rollNo, Long regNo, Double marks) {
        this.name = name;
        this.schoolName = schoolName;
        this.schoolRollNo = schoolRollNo;
        this.classId = classId;
        this.rollNo = rollNo;
        this.regNo = regNo;
        this.marks = marks;
    }
}
