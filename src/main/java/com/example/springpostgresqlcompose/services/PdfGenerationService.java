package com.example.springpostgresqlcompose.services;


import com.example.springpostgresqlcompose.constants.AppConstants;
import com.example.springpostgresqlcompose.db.model.Student;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PdfGenerationService {
    private final int SPACING = 20;
    private final int NARROW_SPACING = 5;

    public void generateAdmitCard(List<Student> studentList, String filename) throws IOException, DocumentException {

        Rectangle pageSize = new Rectangle(594, 423);
        pageSize.setBackgroundColor(new BaseColor(192, 192, 192));
        final float marginTopBottom = 25;
        final float marginLeftRight = 35;
        Document document = new Document(pageSize, marginLeftRight, marginLeftRight, marginTopBottom, marginTopBottom);

        BaseFont scriptMTBold = BaseFont.createFont(AppConstants.SCRIPT_MT_BOLD, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        BaseFont oldEnglish = BaseFont.createFont(AppConstants.OLD_ENGLISH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        BaseFont winding = BaseFont.createFont(AppConstants.WINDING, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.ITALIC, BaseColor.BLACK);
        Font controllerSmallFont = new Font(Font.FontFamily.TIMES_ROMAN, 8f, Font.NORMAL, new BaseColor(128, 0, 0));

        Font oldEnglish22 = new Font(oldEnglish, 22, Font.NORMAL, BaseColor.BLACK);
        Font oldEnglishIT18 = new Font(oldEnglish, 18, Font.ITALIC, BaseColor.BLACK);
        Font scriptMTBold11 = new Font(scriptMTBold, 11, Font.NORMAL, BaseColor.BLACK);
        Font controllerFont = new Font(scriptMTBold, 11f, Font.NORMAL, new BaseColor(128, 0, 0));
        Font windingFont = new Font(winding, 11f, Font.NORMAL);

        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));

        document.open();

        Image logoImage = Image.getInstance(AppConstants.AMAR_AMI_LOGO);
        Image signImage = Image.getInstance(AppConstants.SIGNATURE_IMAGE);

        String centreMale = "Betbaria Secondary School";
        String centreFemale = "Mojibur Rahman Memorial Pre-Cadet School, Betbaria";
        for (Student student : studentList) {

            logoImage.setAlignment(Element.ALIGN_LEFT);
            logoImage.setBorderWidth(SPACING);

            PdfPTable imageTable = new PdfPTable(2);
            imageTable.setWidthPercentage(100);
            imageTable.setWidths(new int[]{1, 5});

            PdfPCell imageCell = new PdfPCell();
            imageCell.addElement(logoImage);
            imageCell.setBorder(PdfPCell.NO_BORDER);
            imageTable.addCell(imageCell);

            Font font2 = new Font(oldEnglish, 12, Font.NORMAL, BaseColor.BLACK);
            PdfPCell textCell = new PdfPCell();

            Paragraph paragraph = new Paragraph("Amar Ami\n", oldEnglish22);
            paragraph.add(new Chunk("Talent Evaluation Exam - 2022\n", font2));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            textCell.addElement(paragraph);
            textCell.setBorder(Rectangle.NO_BORDER);

            imageTable.addCell(textCell);
            imageTable.setSpacingAfter(0);

            Paragraph paragraph1 = new Paragraph("Admit Card", oldEnglishIT18);
            paragraph1.setSpacingAfter(20);
            paragraph1.setAlignment(Element.ALIGN_CENTER);

            // Rectangle around 'Admit card'.
            PdfContentByte cb = writer.getDirectContent();
            cb.roundRectangle(250f, 285f, 95f, 20f, 5f);
            cb.setColorStroke(new BaseColor(209, 0, 0));
            cb.stroke();

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(95);
            table.setWidths(new int[]{8, 5});

            PdfPCell cell;

            cell = new PdfPCell(new Phrase("Name: " + student.getName(), scriptMTBold11));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Roll No: " + student.getRollNo(), scriptMTBold11));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("School: " + student.getSchoolName(), scriptMTBold11));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Registration No: " + student.getRegNo(), scriptMTBold11));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Class: " + student.getClassId(), scriptMTBold11));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Class Roll: " + student.getSchoolRollNo(), scriptMTBold11));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Centre: " + centreFemale, scriptMTBold11));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Exam Date: Thursday, 07 July, 9.00 am", scriptMTBold11));
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            table.setSpacingAfter(30);

            signImage.setAlignment(Element.ALIGN_LEFT);

            PdfPTable table2 = new PdfPTable(3);
            table2.setWidths(new int[]{24, 2, 3});
            table2.setWidthPercentage(100);

            cell = new PdfPCell(new Phrase("", scriptMTBold11));
            cell.setBorder(Rectangle.NO_BORDER);
            table2.addCell(cell);

            PdfPCell signImageCell = new PdfPCell();
            signImageCell.addElement(signImage);
            signImageCell.setBorder(Rectangle.NO_BORDER);
            table2.addCell(signImageCell);

            cell = new PdfPCell(new Phrase("", scriptMTBold11));
            cell.setBorder(Rectangle.NO_BORDER);
            table2.addCell(cell);

            PdfPTable table3 = new PdfPTable(2);
            table3.setWidths(new int[]{8, 3});
            table3.setWidthPercentage(100);

            cell = new PdfPCell(new Phrase("", scriptMTBold11));
            cell.setBorder(Rectangle.NO_BORDER);
            table3.addCell(cell);

            Paragraph controllerExam = new Paragraph(new Chunk("Gias Uddin Ahmed\n", controllerSmallFont));
            controllerExam.add(new Chunk("Controller of Exam", controllerFont));
            cell = new PdfPCell(controllerExam);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table3.addCell(cell);


            Paragraph line = new Paragraph(new Chunk(new LineSeparator(0.0F, 100.0F, BaseColor.BLACK, Element.ALIGN_LEFT, 1)));

            PdfPTable directions = new PdfPTable(2);
            directions.setWidthPercentage(100);
            directions.setWidths(new int[]{3, 97});

            char checked = '\u0076';
            PdfPCell markCell = new PdfPCell(new Phrase(String.valueOf(checked), windingFont));
            markCell.setBorder(Rectangle.NO_BORDER);

            directions.addCell(markCell);

            cell = new PdfPCell(new Phrase("Examinee must bring this card to the examination hall.", font));
            cell.setBorder(Rectangle.NO_BORDER);
            directions.addCell(cell);

            directions.addCell(markCell);

            cell = new PdfPCell(new Phrase("Examinee must appear in the exam hall & take his/her seat at least 20 minutes before the exam start.", font));
            cell.setBorder(Rectangle.NO_BORDER);
            directions.addCell(cell);

            directions.addCell(markCell);

            cell = new PdfPCell(new Phrase("Examinee must carry his/her own writing materials like pen, pencil, geometry instruments & calculator. But he/she cannot keep anything with him/her except these materials.", font));
            cell.setBorder(Rectangle.NO_BORDER);
            directions.addCell(cell);

            directions.addCell(markCell);

            cell = new PdfPCell(new Phrase("In case of violating one or more rules by any examinee during the exam time, that examinee's exam will be canceled and legal actions against that examinee will be taken.", font));
            cell.setBorder(Rectangle.NO_BORDER);
            directions.addCell(cell);

            document.add(imageTable);
            document.add(paragraph1);
            document.add(table);
            document.add(table2);
            document.add(table3);
            document.add(line);
            document.add(directions);

            document.newPage();
        }

        document.close();
    }


}
