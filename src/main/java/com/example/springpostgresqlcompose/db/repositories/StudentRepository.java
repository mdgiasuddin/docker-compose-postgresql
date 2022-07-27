package com.example.springpostgresqlcompose.db.repositories;

import com.example.springpostgresqlcompose.db.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    Student findOneById(Long id);

    List<Student> findAllByNameIsNotNull();

    List<Student> findAllByNameIsNull();

    List<Student> findAllByClassIdAndMarksIsNotNullOrderByMarksDesc(String classId);

    List<Student> findByClassIdAndNameIsNotNullOrderByRollNo(String classId);

    List<Student> findAllByClassIdAndMeritPositionLessThanEqualOrderByMeritPosition(String classId, int meritPosition);

    List<Student> findByClassIdAndNameIsNullOrderByRollNo(String classId);

    List<Student> findByClassIdAndNameIsNotNullOrderBySchoolNameAscRollNoAsc(String classId);

    List<Student> findAllByNameIsNullOrderByClassIdAscRollNoAsc();

    List<Student> findByRollNoIsBetweenOrderByRollNoAsc(long startRoll, long endRoll);

    @Query("select s from Student s where upper(concat(s.rollNo, '')) like :rollNo")
    List<Student> getAllStudentsRollMatch(String rollNo);

    @Query("select s from Student s where ( :name is null or s.name = :name ) and " +
            "( :schoolName is null or s.schoolName = :schoolName) and " +
            "( :schoolRollNo is null or s.schoolRollNo = :schoolRollNo )")
    Page<Student> filterBySearch(@Param("name") Object name, @Param("schoolName") Object schoolName
            , @Param("schoolRollNo") Object schoolRollNo, Pageable pageable);


    long countByName(Object name);

    @Query("select s from Student s order by case when (s.verificationNo is null) then 0 else 1 end asc, s.id asc")
    List<Student> findAllByOrderById();

    @Query("select distinct s.schoolName from Student s where s.schoolName is not null")
    List<String> getDistinctSchoolName();

    @Query("select s from Student s where s.addressDetails.address = :address")
    List<Student> getStudentByAddress(@Param("address") String address);

}
