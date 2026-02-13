package com.qa.blocrecon.utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CSVUtil {

    // Convert CSV to list of normalized maps
    public static List<Map<String, String>> readCSVWithNormalization(String filePath, Map<String, String> columnMapping)
            throws IOException, CsvValidationException {

        List<Map<String, String>> rowsList = new ArrayList<>();
        CSVReader reader = new CSVReader(new FileReader(filePath));

        // Read headers
        String[] headers = reader.readNext();
        if (headers == null) {
            reader.close();
            throw new IOException("CSV file is empty");
        }

        // Trim headers
        for (int i = 0; i < headers.length; i++) {
            headers[i] = headers[i].trim();
        }

        // Date formatter: Excel/CSV format (dd/MM/yyyy) -> DB format (yyyy-MM-dd)
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String[] row;
        int rowIndex = 1;

        while ((row = reader.readNext()) != null) {
            Map<String, String> rowData = new LinkedHashMap<>();

            for (int j = 0; j < headers.length; j++) {
                String excelKey = headers[j].replaceAll("^\\p{C}+", "");
                String dbKey = columnMapping.getOrDefault(excelKey, excelKey);

                String value = (j < row.length && row[j] != null) ? row[j].trim() : "";

                // Handle dates (dd/MM/yyyy → yyyy-MM-dd)
                if (value.matches("\\d{2}-\\d{2}-\\d{4}")) {
                    try {
                        Date date = inputDateFormat.parse(value);
                        value = dbDateFormat.format(date);
                    } catch (ParseException e) {
                        // Keep original if parsing fails
                    }
                }

                // Handle numeric: remove .0 if integer
                if (value.matches("-?\\d+\\.0")) {
                    value = value.substring(0, value.length() - 2);
                }

                rowData.put(dbKey, value);
            }

            // Debug print
            System.out.println("CSV Row " + rowIndex + ": " + rowData);
            rowIndex++;

            rowsList.add(rowData);
        }

        reader.close();
        return rowsList;
    }
}
