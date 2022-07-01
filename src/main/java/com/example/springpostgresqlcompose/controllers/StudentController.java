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

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/excel")
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

    @GetMapping("/seat-plan/{classId}")
    public String generateSeatPlan(@PathVariable("classId") String classId) throws Exception {
        return studentService.generateSeatPlan(classId);
    }

    @PostMapping("/attendance-sheet")
    public String generateAttendance(@RequestParam("file") final MultipartFile multipartFile) {
        return studentService.generateAttendance(multipartFile);
    }

    @GetMapping("/list/excel/{classId}")
    public String generateExcelOfStudentList(@PathVariable("classId") String classId) throws Exception {
        return studentService.generateExcelOfStudentList(classId);
    }

    @GetMapping("/list/pdf/unregistered")
    public String generateUnregisteredStudentList() throws Exception {
        return studentService.generateUnregisteredStudentList();
    }

    @PutMapping("/excel/mark-input")
    public String updateMark(@RequestParam("file") final MultipartFile multipartFile) {
        return studentService.updateMark(multipartFile);
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
    public Map<String, Object> testStudent() {
        return studentService.testStudent();
    }

}
