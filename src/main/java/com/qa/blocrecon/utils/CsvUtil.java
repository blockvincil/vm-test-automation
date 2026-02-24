package com.qa.blocrecon.utils;

import java.io.*;
import java.util.*;
import utils.DataNormalizerUtil;
import java.util.stream.Collectors;

public class CsvUtil {

    private static final Set<String> DATE_COLUMNS = Set.of(
            "itemdate",
            "openingbalancedate",
            "closingbalancedate"
    );

    private static String normalizeCsvValue(String colId, String value) {

        if (value == null) return "";

        value = value.trim();

        if (DATE_COLUMNS.contains(colId.toLowerCase())) {
            value = value.replace("-", "/");
        }

        return value;
    }

    public static List<Map<String, String>> readCsvNormalizedWithRequiredHeaders(
            InputStream inputStream,
            Map<String, String> columnKeyMapping,
            List<String> requiredColIds) {

        List<Map<String, String>> data = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream))) {

            // 🔹 Step 1: Reverse mapping (Value → Key)
            Map<String, String> reverseMapping =
                    columnKeyMapping.entrySet()
                            .stream()
                            .collect(Collectors.toMap(
                                    e -> e.getValue().toLowerCase().trim(),
                                    Map.Entry::getKey,
                                    (existing, replacement) -> existing // handle duplicates safely
                            ));

            // 🔹 Step 2: Read header line
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return data;
            }

            String[] headers = headerLine.split(",");

            Map<String, Integer> headerIndexMap = new LinkedHashMap<>();

            for (int i = 0; i < headers.length; i++) {

                String csvHeader = headers[i].trim();

                String normalizedHeader =
                        reverseMapping.getOrDefault(
                                csvHeader.toLowerCase(),
                                csvHeader
                        );

                if (requiredColIds.contains(normalizedHeader)) {
                    headerIndexMap.put(normalizedHeader, i);
                }
            }

            // 🔹 Step 3: Read data rows
            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue; // skip empty rows
                }

                String[] values = line.split(",");

                Map<String, String> rowData = new LinkedHashMap<>();

                for (Map.Entry<String, Integer> entry : headerIndexMap.entrySet()) {

                    String colId = entry.getKey();
                    int colIndex = entry.getValue();

                    String value = "";

                    if (colIndex < values.length) {
                        value = values[colIndex].trim();
                    }

                    // 🔥 Centralized normalization
                    value = DataNormalizerUtil.normalize(colId, value);

                    rowData.put(colId, value);
                }

                data.add(rowData);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV", e);
        }

        return data;
    }


}
