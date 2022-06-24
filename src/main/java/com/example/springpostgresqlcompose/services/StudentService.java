package com.example.springpostgresqlcompose.services;

import com.example.springpostgresqlcompose.constants.AppConstants;
import com.example.springpostgresqlcompose.db.model.Student;
import com.example.springpostgresqlcompose.db.repositories.StudentRepository;
import com.example.springpostgresqlcompose.dtos.ExcelData;
import com.example.springpostgresqlcompose.dtos.StudentDTO;
import com.example.springpostgresqlcompose.utils.ClassOptionUtils;
import com.example.springpostgresqlcompose.utils.StringFormattingUtils;
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

    public String saveStudent(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return "Wrong Excel Format!";
        }

        try {
            InputStream inputStream = multipartFile.getInputStream();
            XSSFRow row;

            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = spreadsheet.iterator();

            int colNumber;

            if (rowIterator.hasNext()) {
                row = (XSSFRow) rowIterator.next();
                colNumber = row.getPhysicalNumberOfCells();
                if (colNumber != 5) {
                    return "Wrong Excel Format!";
                }

                List<StudentDTO> maleStudentDTOList = new ArrayList<>();
                List<StudentDTO> femaleStudentDTOList = new ArrayList<>();

                while (rowIterator.hasNext()) {
                    row = (XSSFRow) rowIterator.next();

                    String studentName = excelGenerationService.getStringFromAllCellType(row.getCell(0));
                    String schoolName = excelGenerationService.getStringFromAllCellType(row.getCell(1));
                    String classId = excelGenerationService.getStringFromAllCellType(row.getCell(2));
                    Long schoolRollNo = excelGenerationService.getIntegerFromAllCellType(row.getCell(3)).longValue();
                    String gender = excelGenerationService.getStringFromAllCellType(row.getCell(4)).toUpperCase();

                    StudentDTO studentDTO = new StudentDTO();
                    studentDTO.setName(stringFormattingUtils.formatString(studentName));
                    studentDTO.setSchoolName(stringFormattingUtils.formatString(schoolName));
                    studentDTO.setClassId(classId);
                    studentDTO.setSchoolRollNo(schoolRollNo);

                    if (gender.equals("MALE"))
                        maleStudentDTOList.add(studentDTO);
                    else
                        femaleStudentDTOList.add(studentDTO);
                }

                List<StudentDTO> sortedStudent = sortStudent(maleStudentDTOList);
                sortedStudent.addAll(sortStudent(femaleStudentDTOList));

                saveStudentToDatabase(sortedStudent);

                return "Right Excel Format!";

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Wrong Excel Format!";
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

        int i = 0;
        for (StudentDTO studentDTO : studentDTOList) {
            Student student = new Student();
            student.setName(studentDTO.getName());
            student.setSchoolName(studentDTO.getSchoolName());
            student.setClassId(studentDTO.getClassId());
            student.setSchoolRollNo(studentDTO.getSchoolRollNo());
            Long rollNo = startingRollNo + i;
            Long regNo = (startingRegNo * 10000) + ((1 + random.nextInt(9)) * 1000) + increasingRegNo + i;
            student.setRollNo(rollNo);
            student.setRegNo(regNo);
            i++;

            studentRepository.save(student);

        }
    }

    public String generateAdmitCard(String classId) throws Exception {
        List<Student> studentList = studentRepository.findByClassIdOrderByRollNo(classId);
        Map<String, String> map = classOptionUtils.getOptionsOfClass(classId);

        String admitCardFileName = AppConstants.INPUT_OUTPUT_FILE_DIRECTORY + map.get("admitCards");
        String watermarkAdmitCard = AppConstants.INPUT_OUTPUT_FILE_DIRECTORY + map.get("watermarkAdmitCards");
        pdfGenerationService.generateAdmitCard(studentList, admitCardFileName);

        Thread.sleep(2000);

        Image logoImage = Image.getInstance(AppConstants.AMAR_AMI_LOGO);
        watermarkPdfGenerationService.addWaterMarkToPdf(admitCardFileName, watermarkAdmitCard, logoImage, 300, 300, 0.1f);

        return "Admit card generated successfully!";
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
        List<Student> studentList = studentRepository.findByClassIdOrderByRollNo(classId);

        String[] headers = new String[]{
                "Id", "Name", "School Name", "Roll No.", "Reg No."
        };

        List<Object[]> otherRowList = new ArrayList<>();
        for (Student student : studentList) {
            Object[] otherRow = new Object[]{
                    student.getId(), student.getName(), student.getSchoolName(), student.getRollNo(), student.getRegNo()
            };
            otherRowList.add(otherRow);
        }

        excelGenerationService.createExcelFile(new ExcelData("Test", headers, otherRowList), "Class_" + classId + "_Student_List.xlsx");

        return "Excel Generated Successfully!";
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

    public Map<String, Object> testStudent() {
        List<String> schoolNames = studentRepository.getDistinctSchoolName();

        Set<String> set = new HashSet<>(schoolNames);
        List<String> list = Arrays.asList("HB Secondary School", "Betbaria Secondary School", "Giash Uddin");
        set.addAll(list);

        Map<String, Object> map = new HashMap<>();
        map.put("SchoolNames", set);

        return map;
    }
}
