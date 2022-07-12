package com.example.springpostgresqlcompose.services;

import com.example.springpostgresqlcompose.constants.AppConstants;
import com.example.springpostgresqlcompose.db.model.Student;
import com.example.springpostgresqlcompose.db.repositories.StudentRepository;
import com.example.springpostgresqlcompose.dtos.AttendanceSheetData;
import com.example.springpostgresqlcompose.dtos.ExcelData;
import com.example.springpostgresqlcompose.dtos.StudentDTO;
import com.example.springpostgresqlcompose.dtos.UnregisteredStudents;
import com.example.springpostgresqlcompose.enums.Gender;
import com.example.springpostgresqlcompose.utils.ClassOptionUtils;
import com.example.springpostgresqlcompose.utils.StringFormattingUtils;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StudentService {
    private final StudentRepository studentRepository;
    private final ExcelGenerationService excelGenerationService;
    private final StringFormattingUtils stringFormattingUtils;
    private final ClassOptionUtils classOptionUtils;
    private final PdfGenerationService pdfGenerationService;
    private final WatermarkPdfGenerationService watermarkPdfGenerationService;

    @org.springframework.transaction.annotation.Transactional
    public String saveStudent(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return "File is empty!";
        }


        InputStream inputStream = multipartFile.getInputStream();
        XSSFRow row;

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet spreadsheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = spreadsheet.iterator();

        int colNumber;

        if (rowIterator.hasNext()) {
            row = (XSSFRow) rowIterator.next();
            colNumber = row.getPhysicalNumberOfCells();
            if (colNumber != 6) {
                return "Excel must have 6 column!";
            }

            List<StudentDTO> maleStudentDTOList = new ArrayList<>();
            List<StudentDTO> femaleStudentDTOList = new ArrayList<>();

            while (rowIterator.hasNext()) {
                row = (XSSFRow) rowIterator.next();

                String studentName = excelGenerationService.getStringFromAllCellType(row.getCell(0));
                String schoolName = excelGenerationService.getStringFromAllCellType(row.getCell(1));
                String classId = excelGenerationService.getStringFromAllCellType(row.getCell(2));
                String classIdActual = excelGenerationService.getStringFromAllCellType(row.getCell(3));
                Long schoolRollNo = excelGenerationService.getIntegerFromAllCellType(row.getCell(4)).longValue();
                String gender = excelGenerationService.getStringFromAllCellType(row.getCell(5)).toUpperCase();

                StudentDTO studentDTO = new StudentDTO();
                studentDTO.setName(stringFormattingUtils.formatString(studentName));
                studentDTO.setSchoolName(stringFormattingUtils.formatString(schoolName));
                studentDTO.setClassId(classId);
                studentDTO.setClassIdActual(classIdActual);
                studentDTO.setSchoolRollNo(schoolRollNo);

                if (gender.equals("MALE")) {
                    studentDTO.setGender(Gender.M);
                    maleStudentDTOList.add(studentDTO);
                } else {
                    studentDTO.setGender(Gender.F);
                    femaleStudentDTOList.add(studentDTO);
                }
            }

            List<StudentDTO> sortedStudent = sortStudent(femaleStudentDTOList);
            sortedStudent.addAll(sortStudent(maleStudentDTOList));

            saveStudentToDatabase(sortedStudent);

            return "Successfully saved to database!";

        }


        return "Ops! could not save to database!";
    }

    public List<StudentDTO> sortStudent(List<StudentDTO> studentDTOList) {
        List<StudentDTO> resultList = new ArrayList<>();
        Random random = new Random();

        int listLength = studentDTOList.size();
        while (listLength > 0) {
            int count = 0;
            while (true) {
                int randIndex = random.nextInt(listLength);
                if (resultList.isEmpty() || !resultList.get(resultList.size() - 1).getSchoolName().equals(studentDTOList.get(randIndex).getSchoolName())) {
                    resultList.add(studentDTOList.get(randIndex));

                    StudentDTO tempStudent = studentDTOList.get(randIndex);
                    studentDTOList.set(randIndex, studentDTOList.get(listLength - 1));
                    studentDTOList.set(listLength - 1, tempStudent);
                    listLength--;

                    count = 0;
                    break;
                }
                count++;

                if (count > 200)
                    break;
            }

            if (count > 200)
                break;

        }
        for (int i = 0; i < listLength; i++) {
            StudentDTO student = studentDTOList.get(i);

            for (int j = 1; j < resultList.size(); j++) {
                if (!resultList.get(j - 1).getSchoolName().equals(student.getSchoolName()) && !resultList.get(j).getSchoolName().equals(student.getSchoolName())) {
                    resultList.add(j, student);
                    break;
                }
            }
        }
        return resultList;
    }

    public void saveStudentToDatabase(List<StudentDTO> studentDTOList) {
        if (studentDTOList.isEmpty())
            return;

        StudentDTO firstStudent = studentDTOList.get(0);

        Map<String, String> map = classOptionUtils.getOptionsOfClass(firstStudent.getClassId());

        long startingRollNo = Long.parseLong(map.get("startingRollNo"));
        long startingRegNo = Long.parseLong(map.get("startingRegNo"));
        long increasingRegNo = Long.parseLong(map.get("increasingRegNo"));

        Random random = new Random();

        List<Student> studentList = new ArrayList<>();
        int i = 0;
        for (StudentDTO studentDTO : studentDTOList) {
            Student student = new Student();
            student.setName(studentDTO.getName());
            student.setSchoolName(studentDTO.getSchoolName());
            student.setClassId(studentDTO.getClassId());
            student.setClassIdActual(studentDTO.getClassIdActual());
            student.setSchoolRollNo(studentDTO.getSchoolRollNo());
            Long rollNo = startingRollNo + i;
            Long regNo = (startingRegNo * 10000) + ((1 + random.nextInt(9)) * 1000) + increasingRegNo + i;
            student.setRollNo(rollNo);
            student.setRegNo(regNo);
            student.setGender(studentDTO.getGender());
            i++;

            studentList.add(student);

        }

        for (int j = 0; j < 30; j++) {
            Student student = new Student();
            Long rollNo = startingRollNo + i;
            Long regNo = (startingRegNo * 10000) + ((1 + random.nextInt(9)) * 1000) + increasingRegNo + i;
            student.setClassId(firstStudent.getClassId());
            student.setRollNo(rollNo);
            student.setRegNo(regNo);
            i++;

            studentList.add(student);
        }

        studentRepository.saveAll(studentList);
    }

    public String generateAdmitCard(String classId) throws Exception {
        List<Student> studentList = studentRepository.findByClassIdAndNameIsNotNullOrderBySchoolNameAscRollNoAsc(classId);
        Map<String, String> map = classOptionUtils.getOptionsOfClass(classId);

        String admitCardFileName = AppConstants.INPUT_OUTPUT_FILE_DIRECTORY + map.get("admitCards");
        String watermarkAdmitCard = AppConstants.INPUT_OUTPUT_FILE_DIRECTORY + map.get("watermarkAdmitCards");
        pdfGenerationService.generateAdmitCard(studentList, admitCardFileName);

        Thread.sleep(2000);

        Image logoImage = Image.getInstance(AppConstants.AMAR_AMI_LOGO);
        watermarkPdfGenerationService.addWaterMarkToPdf(admitCardFileName, watermarkAdmitCard, logoImage, 300, 300, 0.1f);

        return "Admit card generated successfully!";
    }

    public String generateBlankAdmitCard() throws Exception {
        List<Student> studentList = studentRepository.findAllByNameIsNullOrderByClassIdAscRollNoAsc();

        String admitCardFileName = AppConstants.INPUT_OUTPUT_FILE_DIRECTORY + "BlankAdmitCards.pdf";
        String watermarkAdmitCard = AppConstants.INPUT_OUTPUT_FILE_DIRECTORY + "WaterMarkedBlankAdmitCards.pdf";
        pdfGenerationService.generateBlankAdmitCard(studentList, admitCardFileName);

        Thread.sleep(2000);

        Image logoImage = Image.getInstance(AppConstants.AMAR_AMI_WHITE_LOGO);
        watermarkPdfGenerationService.addWaterMarkToPdf(admitCardFileName, watermarkAdmitCard, logoImage, 300, 300, 0.1f);

        return "Admit card generated successfully!";
    }

    public String generateSeatPlan(String classId) throws Exception {
        List<Student> studentList = studentRepository.findByClassIdAndNameIsNotNullOrderByRollNo(classId);

        String fileName = AppConstants.INPUT_OUTPUT_FILE_DIRECTORY + classId + "_Seat_Plan.pdf";
        pdfGenerationService.generateSeatPlan(studentList, fileName);

        return "Seat plan generated successfully!";
    }

    public String generateFinalResult() throws Exception {
        List<Student> tenStudent = studentRepository.findAllByClassIdAndMeritPositionLessThanEqualOrderByMeritPosition("Ten", AppConstants.TEN_PRIZE);
        List<Student> eightStudent = studentRepository.findAllByClassIdAndMeritPositionLessThanEqualOrderByMeritPosition("Eight", AppConstants.EIGHT_PRIZE);
        List<Student> fiveStudent = studentRepository.findAllByClassIdAndMeritPositionLessThanEqualOrderByMeritPosition("Five", AppConstants.FIVE_PRIZE);

        String fileName = AppConstants.INPUT_OUTPUT_FILE_DIRECTORY + "Final_Result.pdf";
        pdfGenerationService.generateFinalResult(tenStudent, eightStudent, fiveStudent, fileName);

        return "Result generated successfully!";
    }

    public String generateAttendance(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return "The File is empty!";
        }

        try {
            InputStream inputStream = multipartFile.getInputStream();
            XSSFRow row;

            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = spreadsheet.iterator();

            if (rowIterator.hasNext()) {
                row = (XSSFRow) rowIterator.next();
                if (row.getPhysicalNumberOfCells() != 5) {
                    return "Excel must have 5 columns!";
                }

                List<AttendanceSheetData> dataList = new ArrayList<>();
                while (rowIterator.hasNext()) {
                    row = (XSSFRow) rowIterator.next();

                    String classId = excelGenerationService.getStringFromAllCellType(row.getCell(0));
                    String centre = excelGenerationService.getStringFromAllCellType(row.getCell(1));
                    String roomNo = excelGenerationService.getStringFromAllCellType(row.getCell(2));
                    long startRoll = excelGenerationService.getIntegerFromAllCellType(row.getCell(3)).longValue();
                    long endRoll = excelGenerationService.getIntegerFromAllCellType(row.getCell(4)).longValue();

                    dataList.add(new AttendanceSheetData(classId, roomNo, centre, startRoll, endRoll));

                }

                pdfGenerationService.generateAttendanceSheet(dataList);
                return "Successfully generated attendance sheet!";

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Ops! Could not generate attendance sheet!";
    }

    public String registerByTokenAdmit(MultipartFile multipartFile) {

        if (multipartFile.isEmpty()) {
            return "The File is empty!";
        }

        List<Student> studentList = studentRepository.findAllByNameIsNull();
        Map<Long, Student> studentMap = new HashMap<>();

        for (Student student : studentList) {
            studentMap.put(student.getRollNo(), student);
        }

        try {
            InputStream inputStream = multipartFile.getInputStream();
            XSSFRow row;

            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = spreadsheet.iterator();

            if (rowIterator.hasNext()) {
                row = (XSSFRow) rowIterator.next();
                if (row.getPhysicalNumberOfCells() != 6) {
                    return "Excel must have 6 columns!";
                }

                while (rowIterator.hasNext()) {
                    row = (XSSFRow) rowIterator.next();

                    long rollNo = excelGenerationService.getIntegerFromAllCellType(row.getCell(3)).longValue();
                    String name = excelGenerationService.getStringFromAllCellType(row.getCell(4));
                    String schoolName = excelGenerationService.getStringFromAllCellType(row.getCell(5));

                    Student student = studentMap.get(rollNo);
                    student.setName(name);
                    student.setSchoolName(schoolName);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
            return "Ops! Could not update name & school name!";
        }

        studentRepository.saveAll(studentList);
        return "Successfully updated name & school name!";
    }

    public String instantRegisterBySlip(MultipartFile multipartFile) {

        if (multipartFile.isEmpty()) {
            return "The File is empty!";
        }

        List<Student> studentList = new ArrayList<>();
        try {
            InputStream inputStream = multipartFile.getInputStream();
            XSSFRow row;

            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = spreadsheet.iterator();

            if (rowIterator.hasNext()) {
                row = (XSSFRow) rowIterator.next();
                if (row.getPhysicalNumberOfCells() != 6) {
                    return "Excel must have 6 columns!";
                }

                while (rowIterator.hasNext()) {
                    row = (XSSFRow) rowIterator.next();

                    String classId = excelGenerationService.getStringFromAllCellType(row.getCell(0));
                    String name = excelGenerationService.getStringFromAllCellType(row.getCell(1));
                    String schoolName = excelGenerationService.getStringFromAllCellType(row.getCell(2));
                    long rollNo = excelGenerationService.getIntegerFromAllCellType(row.getCell(3)).longValue();
                    long regNo = excelGenerationService.getIntegerFromAllCellType(row.getCell(4)).longValue();
                    String verificationNo = excelGenerationService.getStringFromAllCellType(row.getCell(5));

                    Student student = new Student();
                    student.setClassId(classId);
                    student.setName(name);
                    student.setSchoolName(schoolName);
                    student.setRollNo(rollNo);
                    student.setRegNo(regNo);
                    student.setVerificationNo(verificationNo);

                    studentList.add(student);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
            return "Ops! Could not add new students!";
        }

        studentRepository.saveAll(studentList);
        return "Successfully added new students!";
    }

    public String generateUnregisteredStudentList() throws DocumentException, IOException {

        List<Student> tenStudents = studentRepository.findByClassIdAndNameIsNullOrderByRollNo("Ten");
        List<Student> eightStudents = studentRepository.findByClassIdAndNameIsNullOrderByRollNo("Eight");
        List<Student> fiveStudents = studentRepository.findByClassIdAndNameIsNullOrderByRollNo("Five");

        pdfGenerationService.generateUnregisteredStudentList(new UnregisteredStudents(tenStudents, eightStudents, fiveStudents));

        return "Unregistered student list generated successfully!";
    }

    public String addVerificationNo() {
        List<Integer> list = new ArrayList<>();

        for (int i = 101; i <= 999; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        List<Student> studentList = studentRepository.findAll();

        Random random = new Random();
        int i = 0;
        for (Student student : studentList) {
            char ch = (char) ('A' + random.nextInt(26));
            student.setVerificationNo(ch + "" + list.get(i++));
        }

        studentRepository.saveAll(studentList);

        return "Verification No added successfully!";
    }

    public String generateExcelOfStudentList(String classId) throws IOException {
        List<Student> studentList = studentRepository.findByClassIdAndNameIsNotNullOrderByRollNo(classId);

        String[] headers = new String[]{
                "Id", "Name", "School Name", "Roll No.", "Reg No.", "Verify No"
        };

        List<Object[]> otherRowList = new ArrayList<>();
        for (Student student : studentList) {
            Object[] otherRow = new Object[]{
                    student.getId(), student.getName(), student.getSchoolName(), student.getRollNo(), student.getRegNo(), student.getVerificationNo()
            };
            otherRowList.add(otherRow);
        }

        excelGenerationService.createExcelFile(new ExcelData("Test", headers, otherRowList), "Class_" + classId + "_Student_List.xlsx");

        return "Excel Generated Successfully!";
    }

    public String updateMark(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return "File is empty!";
        }

        List<Student> studentList = studentRepository.findAll();
        Map<Long, Student> studentMap = new HashMap<>();
        for (Student student : studentList) {
            studentMap.put(student.getRollNo(), student);
        }

        try {
            InputStream inputStream = multipartFile.getInputStream();
            XSSFRow row;

            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = spreadsheet.iterator();

            if (rowIterator.hasNext()) {
                row = (XSSFRow) rowIterator.next();
                if (row.getPhysicalNumberOfCells() != 4) {
                    return "Excel must have 4 column!";
                }

                while (rowIterator.hasNext()) {
                    row = (XSSFRow) rowIterator.next();

                    long rollNo = excelGenerationService.getIntegerFromAllCellType(row.getCell(0)).longValue();
                    double mark = excelGenerationService.getDoubleFromAllCellType(row.getCell(1));

                    studentMap.get(rollNo).setMarks(mark);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Ops! could not save to database!";
        }

        studentRepository.saveAll(studentList);

        return "Successfully saved to database!";
    }

    public Page<Student> filterStudentBySearch(Map map, Pageable pageable) {
        return studentRepository.filterBySearch(map.get("name"), map.get("schoolName"), map.get("schoolRollNo"), pageable);
    }

    public long countStudentBySearch(Map map) {
        return studentRepository.countByName(map.get("name"));
    }

    public List<Student> getAllStudent() {
        return studentRepository.findAllByOrderById();
    }

    public List<Student> testStudent() {
        return studentRepository.getAllStudentsRollMatch("%315%");
    }

    public String updateMarkForTest() {
        List<Student> studentList = studentRepository.findAllByNameIsNotNull();

        Random random = new Random();

        for (Student student : studentList) {
            double marks = 40 + random.nextInt(95);

            student.setMarks(marks);
        }

        studentRepository.saveAll(studentList);

        return "Successfully updated mark for test!";
    }

    public String updateGradeAndMeritPosition() {
        List<Student> tenStudents = studentRepository.findAllByClassIdAndMarksIsNotNullOrderByMarksDesc("Ten");
        List<Student> eightStudents = studentRepository.findAllByClassIdAndMarksIsNotNullOrderByMarksDesc("Eight");
        List<Student> fivesStudents = studentRepository.findAllByClassIdAndMarksIsNotNullOrderByMarksDesc("Five");

        int i = 1;
        for (Student student : tenStudents) {
            student.setMeritPosition(i++);
            int grade = (int) (student.getMarks() / 20);
            student.setGrade(grade);
        }

        i = 1;
        for (Student student : eightStudents) {
            student.setMeritPosition(i++);
            int grade = (int) (student.getMarks() / 20);
            student.setGrade(grade);
        }

        i = 1;
        for (Student student : fivesStudents) {
            student.setMeritPosition(i++);
            int grade = (int) (student.getMarks() / 20);
            student.setGrade(grade);
        }

        studentRepository.saveAll(tenStudents);
        studentRepository.saveAll(eightStudents);
        studentRepository.saveAll(fivesStudents);

        return "Successfully updated grade & merit position!";
    }
}
