package com.edukit.core.student.service;

import com.edukit.core.student.exception.StudentErrorCode;
import com.edukit.core.student.exception.StudentException;
import com.edukit.core.student.service.dto.ExcelParseResult;
import com.edukit.core.student.service.dto.InvalidStudentRow;
import com.edukit.core.student.service.dto.ValidStudentRow;
import com.edukit.core.student.utils.ExcelUtils;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String XLS_CONTENT_TYPE = "application/vnd.ms-excel";
    private static final String XLSX_EXTENSION = ".xlsx";
    private static final String XLS_EXTENSION = ".xls";

    static {
        IOUtils.setByteArrayMaxOverride(100 * 1024 * 1024);
    }

    private static final int HEADER_ROW_INDEX = 0;
    private static final int GRADE_INDEX = 0;
    private static final int CLASS_NUMBER_INDEX = 1;
    private static final int STUDENT_NUMBER_INDEX = 2;
    private static final int STUDENT_NAME_INDEX = 3;

    public void validateExcelFormat(final MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        if (!isExcelFile(contentType, fileName)) {
            throw new StudentException(StudentErrorCode.EXCEL_FILE_FORMAT_ERROR);
        }
    }

    private boolean isExcelFile(final String contentType, final String fileName) {
        boolean validContentType = contentType != null &&
                (contentType.equals(XLSX_CONTENT_TYPE) || contentType.equals(XLS_CONTENT_TYPE));
        boolean validExtension = fileName != null &&
                (fileName.toLowerCase().endsWith(XLSX_EXTENSION) || fileName.toLowerCase().endsWith(XLS_EXTENSION));
        return validContentType || validExtension;
    }

    public ExcelParseResult parseStudentExcel(final MultipartFile file) {
        Set<ValidStudentRow> validStudents = new HashSet<>();
        Set<InvalidStudentRow> invalidRows = new HashSet<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                ExcelParseResult sheetResult = parseSheet(sheet);
                validStudents.addAll(sheetResult.validStudents());
                invalidRows.addAll(sheetResult.invalidRows());
            }

        } catch (IOException e) {
            throw new StudentException(StudentErrorCode.EXCEL_FILE_READ_ERROR, e);
        } catch (Exception e) {
            throw new StudentException(StudentErrorCode.EXCEL_FILE_FORMAT_ERROR, e);
        }
        return ExcelParseResult.of(validStudents, invalidRows);
    }

    private ExcelParseResult parseSheet(final Sheet sheet) {
        Set<ValidStudentRow> validStudents = new HashSet<>();
        Set<InvalidStudentRow> invalidRows = new HashSet<>();

        for (int rowIndex = HEADER_ROW_INDEX + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);

            if (row == null || ExcelUtils.isRowEmpty(row)) {
                continue;
            }

            parseRow(row, rowIndex + 1, validStudents, invalidRows);
        }
        return ExcelParseResult.of(validStudents, invalidRows);
    }

    private void parseRow(final Row row, final int rowNumber, final Set<ValidStudentRow> validStudents,
                          final Set<InvalidStudentRow> invalidRows) {
        String gradeStr = ExcelUtils.getCellValueAsStringOrDefault(row.getCell(GRADE_INDEX));
        String classNumberStr = ExcelUtils.getCellValueAsStringOrDefault(row.getCell(CLASS_NUMBER_INDEX));
        String studentNumberStr = ExcelUtils.getCellValueAsStringOrDefault(row.getCell(STUDENT_NUMBER_INDEX));
        String studentName = ExcelUtils.getCellValueAsStringOrDefault(row.getCell(STUDENT_NAME_INDEX));

        Integer grade = validateAndParseInt(gradeStr);
        Integer classNumber = validateAndParseInt(classNumberStr);
        Integer studentNumber = validateAndParseInt(studentNumberStr);

        if (isInvalidRow(grade, classNumber, studentNumber, studentName)) {
            invalidRows.add(InvalidStudentRow.of(rowNumber, gradeStr, classNumberStr, studentNumberStr, studentName));
        } else {
            validStudents.add(ValidStudentRow.of(grade, classNumber, studentNumber, studentName.trim()));
        }
    }

    private Integer validateAndParseInt(final String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isInvalidRow(final Integer grade, final Integer classNumber, final Integer studentNumber,
                                 final String studentName) {
        return grade == null || classNumber == null || studentNumber == null || studentName == null
                || studentName.isBlank();
    }
}
