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

    @PostMapping("/excel")
    public String saveStudent(@RequestParam("file") final MultipartFile multipartFile) {
        return studentService.saveStudent(multipartFile);
    }

    @GetMapping("/admit-card/{classId}")
    public void generateAdmitCard(@PathVariable("classId") String classId) throws Exception {
        studentService.generateAdmitCard(classId);
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
