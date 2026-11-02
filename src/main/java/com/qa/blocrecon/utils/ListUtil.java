package com.qa.blocrecon.utils;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.core.util.Assert;

import java.util.*;

public class ListUtil {

    private ListUtil() {
    }

    public static List<String> compare2DMapsWithDetails(
            List<Map<String, String>> expected,
            List<Map<String, String>> actual) {

        List<String> errors = new ArrayList<>();

        // Create frequency maps for exact matches
        Map<Map<String, String>, Integer> expectedCounts = new HashMap<>();
        Map<Map<String, String>, Integer> actualCounts = new HashMap<>();

        for (Map<String, String> row : expected) {
            expectedCounts.merge(new HashMap<>(row), 1, Integer::sum);
        }

        for (Map<String, String> row : actual) {
            actualCounts.merge(new HashMap<>(row), 1, Integer::sum);
        }

        // First pass: handle exact matches
        List<Map<String, String>> remainingExpected = new ArrayList<>();
        List<Map<String, String>> remainingActual = new ArrayList<>();

        for (Map.Entry<Map<String, String>, Integer> entry : expectedCounts.entrySet()) {
            Map<String, String> row = entry.getKey();
            int expectedCount = entry.getValue();
            int actualCount = actualCounts.getOrDefault(row, 0);

            if (actualCount >= expectedCount) {
                // All expected rows have exact matches
                actualCounts.put(row, actualCount - expectedCount);
            } else {
                // Some expected rows missing exact matches
                for (int i = 0; i < expectedCount - actualCount; i++) {
                    remainingExpected.add(new HashMap<>(row));
                }
                if (actualCount > 0) {
                    actualCounts.put(row, 0);
                }
            }
        }

        // Collect remaining actual rows
        for (Map.Entry<Map<String, String>, Integer> entry : actualCounts.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                remainingActual.add(new HashMap<>(entry.getKey()));
            }
        }

        // Second pass: find best matches for remaining rows
        for (Map<String, String> expectedRow : remainingExpected) {
            if (remainingActual.isEmpty()) {
                errors.add("Missing in UI: " + expectedRow);
                continue;
            }

            Map<String, String> bestMatch = null;
            int bestMatchScore = -1;
            int bestMatchIndex = -1;

            for (int i = 0; i < remainingActual.size(); i++) {
                Map<String, String> actualRow = remainingActual.get(i);
                int matchScore = calculateMatchScore(expectedRow, actualRow);

                if (matchScore > bestMatchScore) {
                    bestMatchScore = matchScore;
                    bestMatch = actualRow;
                    bestMatchIndex = i;
                }
            }

            if (bestMatchScore == expectedRow.size()) {
                remainingActual.remove(bestMatchIndex);
            } else {
                List<String> fieldDifferences = getFieldDifferences(expectedRow, bestMatch);
                errors.add("Row mismatch in UI: " + fieldDifferences);
                remainingActual.remove(bestMatchIndex);
            }
        }

        // Report unexpected rows
        for (Map<String, String> unexpectedRow : remainingActual) {
            errors.add("Unexpected in UI: " + unexpectedRow);
        }

        return errors;
    }

    private static int calculateMatchScore(Map<String, String> row1, Map<String, String> row2) {
        int score = 0;
        for (Map.Entry<String, String> entry : row1.entrySet()) {
            if (Objects.equals(entry.getValue(), row2.get(entry.getKey()))) {
                score++;
            }
        }
        return score;
    }

    private static List<String> getFieldDifferences(Map<String, String> expected, Map<String, String> actual) {
        List<String> differences = new ArrayList<>();

        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(expected.keySet());
        allKeys.addAll(actual.keySet());

        for (String key : allKeys) {
            String expectedValue = expected.get(key);
            String actualValue = actual.get(key);

            if (!Objects.equals(expectedValue, actualValue)) {
                differences.add(String.format("Field '%s': expected='%s', actual='%s'",
                        key, expectedValue, actualValue));
            }
        }

        return differences;
    }

    public static boolean compare2DMaps(
            List<Map<String, String>> list1,
            List<Map<String, String>> list2) {

        Allure.step("Compare test data with actual data");

        if (list1.size() != list2.size()) {
            return false;
        }

        Map<Map<String, String>, Integer> frequencyMap = new HashMap<>();

        // Count occurrences from list1
        for (Map<String, String> row : list1) {
            frequencyMap.put(row, frequencyMap.getOrDefault(row, 0) + 1);
        }

        // Reduce using list2
        for (Map<String, String> row : list2) {

            if (!frequencyMap.containsKey(row)) {
                return false; // row not found
            }

            int count = frequencyMap.get(row);

            if (count == 1) {
                frequencyMap.remove(row);
            } else {
                frequencyMap.put(row, count - 1);
            }
        }

        return frequencyMap.isEmpty();
    }
}
