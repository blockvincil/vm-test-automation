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

        Map<Map<String, String>, Integer> expectedCounts = new HashMap<>();
        Map<Map<String, String>, Integer> actualCounts = new HashMap<>();

        for (Map<String, String> row : expected) {
            expectedCounts.merge(new HashMap<>(row), 1, Integer::sum);
        }

        for (Map<String, String> row : actual) {
            actualCounts.merge(new HashMap<>(row), 1, Integer::sum);
        }

        Set<Map<String, String>> allRows = new HashSet<>();
        allRows.addAll(expectedCounts.keySet());
        allRows.addAll(actualCounts.keySet());

        for (Map<String, String> row : allRows) {

            int expectedCount = expectedCounts.getOrDefault(row, 0);
            int actualCount = actualCounts.getOrDefault(row, 0);

            if (expectedCount > actualCount) {
                errors.add("Missing in UI: " + row
                        + " | Expected occurrences: " + expectedCount
                        + " | Actual occurrences: " + actualCount);
            }

            if (actualCount > expectedCount) {
                errors.add("Unexpected in UI: " + row
                        + " | Expected occurrences: " + expectedCount
                        + " | Actual occurrences: " + actualCount);
            }
        }

        return errors;
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
