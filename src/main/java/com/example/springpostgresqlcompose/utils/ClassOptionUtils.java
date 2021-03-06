package com.example.springpostgresqlcompose.utils;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ClassOptionUtils {

    public Map<String, String> getOptionsOfClass(String classId) {
        final String CLASS_WORD = "Class";
        Map<String, String> map = new HashMap<>();
        String admitCards = CLASS_WORD + classId + "AdmitCards.pdf";
        String watermarkAdmitCards = CLASS_WORD + classId + "WatermarkAdmitCards.pdf";
        String attendanceSheet = CLASS_WORD + classId + "AttendanceSheet.pdf";
        String resultSheet = CLASS_WORD + classId + "ResultSheet.pdf";

        int startingRollNo = 0;
        int startingRegNo = 0;
        int increasingRegNo = 0;

        if (classId.equalsIgnoreCase("Ten")) {
            startingRollNo = 973101;
            startingRegNo = 56;
            increasingRegNo = 331;
        } else if (classId.equalsIgnoreCase("Eight")) {
            startingRollNo = 645101;
            startingRegNo = 38;
            increasingRegNo = 271;
        } else if (classId.equalsIgnoreCase("Five")) {
            startingRollNo = 827101;
            startingRegNo = 29;
            increasingRegNo = 421;
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
