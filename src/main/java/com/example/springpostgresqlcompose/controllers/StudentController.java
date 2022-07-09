package com.example.springpostgresqlcompose.controllers;

import com.example.springpostgresqlcompose.db.model.Student;
import com.example.springpostgresqlcompose.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;

    /*@PostMapping("/excel")
    public String saveStudent(@RequestParam("file") final MultipartFile multipartFile) throws IOException {
        return studentService.saveStudent(multipartFile);
    }

    @PutMapping("/verification-no")
    public String addVerificationNo() {
        return studentService.addVerificationNo();
    }

    @GetMapping("/admit-card/{classId}")
    public String generateAdmitCard(@PathVariable("classId") String classId) throws Exception {
        return studentService.generateAdmitCard(classId);
    }

    @GetMapping("/admit-card/blank/instant-register")
    public String generateBlankAdmitCard() throws Exception {
        return studentService.generateBlankAdmitCard();
    }

    @GetMapping("/list/pdf/unregistered")
    public String generateUnregisteredStudentList() throws Exception {
        return studentService.generateUnregisteredStudentList();
    }

    @GetMapping("/seat-plan/{classId}")
    public String generateSeatPlan(@PathVariable("classId") String classId) throws Exception {
        return studentService.generateSeatPlan(classId);
    }

    @PostMapping("/attendance-sheet")
    public String generateAttendance(@RequestParam("file") final MultipartFile multipartFile) {
        return studentService.generateAttendance(multipartFile);
    }

    @PostMapping("/token-register/excel")
    public String generateAttendance(@RequestParam("file") final MultipartFile multipartFile) {
        return studentService.registerByTokenAdmit(multipartFile);
    }

    @PostMapping("/instant-register-slip/excel")
    public String generateAttendance(@RequestParam("file") final MultipartFile multipartFile) {
        return studentService.instantRegisterBySlip(multipartFile);
    }

    @GetMapping("/list/excel/{classId}")
    public String generateExcelOfStudentList(@PathVariable("classId") String classId) throws Exception {
        return studentService.generateExcelOfStudentList(classId);
    } */

    @PutMapping("/excel/mark-input")
    public String updateMark(@RequestParam("file") final MultipartFile multipartFile) {
        return studentService.updateMark(multipartFile);
    }

    /*@PutMapping("/mock/test/mark-input")
    public String updateMarkForTest() {
        return studentService.updateMarkForTest();
    }*/

    @PutMapping("/grade/merit-position")
    public String updateGradeAndMeritPosition() {
        return studentService.updateGradeAndMeritPosition();
    }

    @GetMapping("/all")
    public List<Student> getAllStudent() {
        return studentService.getAllStudent();
    }

    @PostMapping("/filter/{pageNum}")
    public Page<Student> filterStudentBySearch(@PathVariable("pageNum") int pageNum, @RequestBody Map map) {
        Pageable pageable = PageRequest.of(pageNum, 10);
        return studentService.filterStudentBySearch(map, pageable);
    }

    @PostMapping("/count")
    public long countStudentBySearch(@RequestBody Map map) {
        return studentService.countStudentBySearch(map);
    }

    @GetMapping("/test")
    public List<Student> testStudent() {
        return studentService.testStudent();
    }

}
