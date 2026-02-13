package com.qa.blocrecon.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListUtil {

    private ListUtil() {
    }

    /**
     * Converts a 2D list into a multiset-like map where
     * key   = joined row values
     * value = occurrence count
     */
    public static Map<String, Integer> toMultiSet(List<List<String>> data) {

        Map<String, Integer> map = new HashMap<>();

        for (List<String> row : data) {
            String key = String.join("|", row);
            map.put(key, map.getOrDefault(key, 0) + 1);
        }

        return map;
    }

    public static boolean compare2DMaps(
            List<Map<String, String>> list1,
            List<Map<String, String>> list2) {

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
