package com.example.springpostgresqlcompose.db.repositories;

import com.example.springpostgresqlcompose.db.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    List<Student> findByClassIdOrderByRollNo(String classId);

    List<Student> findByClassIdOrderBySchoolNameAscRollNoAsc(String classId);

    @Query("select s from Student s where ( :name is null or s.name = :name ) and " +
            "( :schoolName is null or s.schoolName = :schoolName) and " +
            "( :schoolRollNo is null or s.schoolRollNo = :schoolRollNo )")
    Page<Student> filterBySearch(@Param("name") Object name, @Param("schoolName") Object schoolName
            , @Param("schoolRollNo") Object schoolRollNo, Pageable pageable);


    long countByName(Object name);

    List<Student> findByNameAndSchoolName(String name, String schoolName);

    @Query("select s from Student s order by case when (s.verificationNo is null) then 0 else 1 end asc, s.id asc")
    List<Student> findAllByOrderById();

    List<Student> findBySchoolNameAndClassIdAndSchoolRollNo(String schoolName, String classId, int schoolRollNo);

    @Query("select distinct s.schoolName from Student s where s.schoolName is not null")
    List<String> getDistinctSchoolName();

}
