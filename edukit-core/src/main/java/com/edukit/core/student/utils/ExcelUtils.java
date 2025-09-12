package com.edukit.core.student.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

public class ExcelUtils {

    private static final int MAX_ROW_LENGTH = 4;
    private static final String DEFAULT_VALUE = "";

    public static boolean isRowEmpty(final Row row) {
        for (int cellIndex = 0; cellIndex < MAX_ROW_LENGTH; cellIndex++) {
            if (!isCellEmpty(row.getCell(cellIndex))) {
                return false;
            }
        }
        return true;
    }

    public static String getCellValueAsStringOrDefault(final Cell cell) {
        if (cell == null) {
            return DEFAULT_VALUE;
        }
        return getCellValueAsString(cell);
    }

    private static boolean isCellEmpty(final Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return true;
        }
        String value = getCellValueAsString(cell);
        return value == null || value.trim().isEmpty();
    }

    private static String getCellValueAsString(final Cell cell) {
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
                return DEFAULT_VALUE;
        }
    }
}
