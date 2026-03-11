package com.qa.blocrecon.utils;

import io.qameta.allure.Allure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListUtil {

    private ListUtil() {
    }

    public static boolean compare2DMaps(
            List<Map<String, String>> list1,
            List<Map<String, String>> list2) {

        Allure.step("Compare data");

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
