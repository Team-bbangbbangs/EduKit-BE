package com.edukit.core.student.service;

import com.edukit.core.student.exception.StudentErrorCode;
import com.edukit.core.student.exception.StudentException;
import com.edukit.core.student.service.dto.StudentExcelRow;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String EXCEL_FILE_EXTENSION = ".xlsx";
    private static final int HEADER_ROW_INDEX = 0;
    private static final int GRADE_INDEX = 0;
    private static final int CLASS_NUMBER_INDEX = 1;
    private static final int STUDENT_NUMBER_INDEX = 2;
    private static final int STUDENT_NAME_INDEX = 3;
    private static final int MAX_ROW_LENGTH = 4;

    public void validateExcelFormat(final MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        if (!isExcelFile(contentType, fileName)) {
            throw new StudentException(StudentErrorCode.EXCEL_FILE_FORMAT_ERROR);
        }
    }

    private boolean isExcelFile(final String contentType, final String fileName) {
        return (contentType != null && contentType.equals(EXCEL_CONTENT_TYPE))
                || (fileName != null && fileName.toLowerCase().endsWith(EXCEL_FILE_EXTENSION));
    }

    public Set<StudentExcelRow> parseStudentExcel(final MultipartFile file) {
        Set<StudentExcelRow> students = new HashSet<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                students.addAll(parseSheet(sheet));
            }

        } catch (IOException e) {
            throw new StudentException(StudentErrorCode.EXCEL_FILE_READ_ERROR, e);
        }
        return students;
    }

    private Set<StudentExcelRow> parseSheet(final Sheet sheet) {
        Set<StudentExcelRow> students = new HashSet<>();

        for (int rowIndex = HEADER_ROW_INDEX + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);

            if (row == null || isRowEmpty(row)) {
                continue;
            }

            Objects.requireNonNull(parseRow(row))
                    .ifPresent(students::add);

        }
        return students;
    }

    private Optional<StudentExcelRow> parseRow(final Row row) {
        try {
            String grade = getCellValueAsString(row.getCell(GRADE_INDEX));
            String classNumber = getCellValueAsString(row.getCell(CLASS_NUMBER_INDEX));
            String studentNumber = getCellValueAsString(row.getCell(STUDENT_NUMBER_INDEX));
            String studentName = getCellValueAsString(row.getCell(STUDENT_NAME_INDEX));

            return Optional.of(StudentExcelRow.of(grade, classNumber, studentNumber, studentName));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String getCellValueAsString(final Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case CellType.STRING:
                return cell.getStringCellValue().trim();
            case CellType.NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case CellType.BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case CellType.FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private boolean isRowEmpty(final Row row) {
        for (int cellIndex = 0; cellIndex < MAX_ROW_LENGTH; cellIndex++) {
            Cell cell = row.getCell(cellIndex);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}
