package com.example.springpostgresqlcompose.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ClassOptionUtils {

    public Map<String, String> getOptionsOfClass(String classId) {
        Map<String, String> map = new HashMap<>();
        String admitCards = "Class"+classId+"AdmitCards.pdf";
        String watermarkAdmitCards = "Class"+classId+"WatermarkAdmitCards.pdf";
        String attendanceSheet = "Class"+classId+"AttendanceSheet.pdf";
        String resultSheet = "Class"+classId+"ResultSheet.pdf";

        int startingRollNo = 0;
        int startingRegNo = 0;
        int increasingRegNo = 0;

        if (classId.equalsIgnoreCase("Ten")) {
            startingRollNo = 971221;
            startingRegNo = 57;
            increasingRegNo = 331;
        }else if (classId.equalsIgnoreCase("Eight")) {
            startingRollNo = 651411;
            startingRegNo = 37;
            increasingRegNo = 271;
        }else if (classId.equalsIgnoreCase("Five")) {
            startingRollNo = 831341;
            startingRegNo = 23;
            increasingRegNo = 541;
        }

        map.put("admitCards", admitCards);
        map.put("watermarkAdmitCards", watermarkAdmitCards);
        map.put("attendanceSheet", attendanceSheet);
        map.put("resultSheet", resultSheet);
        map.put("startingRollNo", String.valueOf(startingRollNo));
        map.put("startingRegNo", String.valueOf(startingRegNo));
        map.put("increasingRegNo", String.valueOf(increasingRegNo));

        return map;
    }
}
