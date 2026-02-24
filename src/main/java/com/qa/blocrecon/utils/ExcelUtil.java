package com.qa.blocrecon.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import utils.DataNormalizerUtil;

public class ExcelUtil {

    public static List<Map<String, String>> readExcelNormalizedWithRequiredHeaders(
            InputStream inputStream,
            String sheetName,
            Map<String, String> columnKeyMapping,
            List<String> requiredColIds) {

        List<Map<String, String>> data = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                return data; // empty sheet
            }

            // 🔹 Step 1: Reverse mapping (Value → Key)
            Map<String, String> reverseMapping =
                    columnKeyMapping.entrySet()
                            .stream()
                            .collect(Collectors.toMap(
                                    e -> e.getValue().toLowerCase().trim(),
                                    Map.Entry::getKey,
                                    (existing, replacement) -> existing // handle duplicates safely
                            ));

            // 🔹 Step 2: Read header row
            Row headerRow = rowIterator.next();

            Map<String, Integer> headerIndexMap = new LinkedHashMap<>();

            for (Cell cell : headerRow) {

                String excelHeader = cell.toString().trim();

                String normalizedHeader =
                        reverseMapping.getOrDefault(
                                excelHeader.toLowerCase(),
                                excelHeader
                        );

                if (requiredColIds.contains(normalizedHeader)) {
                    headerIndexMap.put(normalizedHeader, cell.getColumnIndex());
                }
            }

            // 🔹 Step 3: Read data rows
            while (rowIterator.hasNext()) {

                Row row = rowIterator.next();

                Map<String, String> rowData = new LinkedHashMap<>();
                boolean isEmptyRow = true;

                for (Map.Entry<String, Integer> entry : headerIndexMap.entrySet()) {

                    String colId = entry.getKey();
                    Integer colIndex = entry.getValue();

                    Cell cell = row.getCell(
                            colIndex,
                            Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                    );

                    String value = cell.toString().trim();

                    // 🔥 Centralized normalization
                    value = DataNormalizerUtil.normalize(colId, value);

                    if (!value.isEmpty()) {
                        isEmptyRow = false;
                    }

                    rowData.put(colId, value);
                }

                // Skip fully empty rows
                if (!isEmptyRow) {
                    data.add(rowData);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to read Excel", e);
        }

        return data;
    }


}


