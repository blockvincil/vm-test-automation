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

public class ExcelUtil {

    /**
     * Reads an Excel file from an input stream and returns the data as a list of lists.
     * @param inputStream The input stream of the Excel file.
     * @param sheetName The name of the sheet to read.
     * @return A list of lists containing the data from the Excel file.
     */
    public static List<List<String>> readExcel(
            InputStream inputStream,
            String sheetName) {

        List<List<String>> data = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {

            // System.out.println("Reading Excel file...");

            Sheet sheet = workbook.getSheet(sheetName);

            for (Row row : sheet) {
                List<String> rowData = new ArrayList<>();

                for (Cell cell : row) {
                    rowData.add(cell.toString().trim());
                }

                data.add(rowData);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to read Excel from InputStream", e);
        }

        return data;
    }

    public static List<Map<String, String>> readExcelByHeaders(
            InputStream inputStream,
            String sheetName,
            List<String> requiredHeaders) {

        List<Map<String, String>> data = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheet(sheetName);

            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                return data;
            }

            // Read header row
            Row headerRow = rowIterator.next();
            Map<String, Integer> headerIndexMap = new HashMap<>();

            for (Cell cell : headerRow) {
                headerIndexMap.put(cell.toString().trim(), cell.getColumnIndex());
            }

            // Process data rows
            while (rowIterator.hasNext()) {

                Row row = rowIterator.next();
                Map<String, String> rowData = new LinkedHashMap<>();

                for (String header : requiredHeaders) {

                    Integer colIndex = headerIndexMap.get(header);

                    if (colIndex != null) {
                        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        rowData.put(header, cell.toString().trim());
                    }
                }

                data.add(rowData);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to read Excel from InputStream", e);
        }

        return data;
    }

    public static List<Map<String, String>> readExcelAndNormalizeHeaders(
            InputStream inputStream,
            String sheetName,
            Map<String, String> columnKeyMapping) {

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

            // 🔹 Step 1: Create reverse mapping (Value → Key)
            Map<String, String> reverseMapping = columnKeyMapping.entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            e -> e.getValue().toLowerCase().trim(),
                            Map.Entry::getKey
                    ));

            // 🔹 Step 2: Read header row
            Row headerRow = rowIterator.next();
            Map<String, Integer> headerIndexMap = new LinkedHashMap<>();

            for (Cell cell : headerRow) {

                String excelHeader = cell.toString().trim();
                String normalizedHeader = reverseMapping
                        .getOrDefault(excelHeader.toLowerCase(), excelHeader);

                headerIndexMap.put(normalizedHeader, cell.getColumnIndex());
            }

            // 🔹 Step 3: Read data rows
            while (rowIterator.hasNext()) {

                Row row = rowIterator.next();
                Map<String, String> rowData = new LinkedHashMap<>();

                for (Map.Entry<String, Integer> entry : headerIndexMap.entrySet()) {

                    String headerKey = entry.getKey();
                    Integer colIndex = entry.getValue();

                    Cell cell = row.getCell(colIndex,
                            Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    rowData.put(headerKey, cell.toString().trim());
                }

                data.add(rowData);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to read Excel from InputStream", e);
        }

        return data;
    }

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
                                    Map.Entry::getKey
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

                // Only store if required
                if (requiredColIds.contains(normalizedHeader)) {
                    headerIndexMap.put(normalizedHeader, cell.getColumnIndex());
                }
            }

            // 🔹 Step 3: Read data rows
            while (rowIterator.hasNext()) {

                Row row = rowIterator.next();
                Map<String, String> rowData = new LinkedHashMap<>();

                for (Map.Entry<String, Integer> entry : headerIndexMap.entrySet()) {

                    String colId = entry.getKey();
                    Integer colIndex = entry.getValue();

                    Cell cell = row.getCell(
                            colIndex,
                            Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
                    );

                    rowData.put(colId, cell.toString().trim());
                }

                data.add(rowData);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to read Excel", e);
        }

        return data;
    }



}


