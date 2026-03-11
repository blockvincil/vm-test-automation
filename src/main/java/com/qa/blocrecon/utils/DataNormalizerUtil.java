package com.qa.blocrecon.utils;

import java.math.BigDecimal;
import java.util.Set;

public final class DataNormalizerUtil {

    // 🔹 Prevent instantiation
    private DataNormalizerUtil() {}

    // 🔹 Define column categories
    private static final Set<String> NUMERIC_COLUMNS = Set.of(
            "amount",
            "openingbalance",
            "closingbalance"
    );

    private static final Set<String> DATE_COLUMNS = Set.of(
            "itemdate",
            "openingbalancedate",
            "closingbalancedate"
    );

    private static final Set<String> DBCR_COLUMNS = Set.of(
            "db_cr",
            "openingbalance_dbcr",
            "closingbalance_dbcr"
    );

    /**
     * Main normalization entry point.
     * Call this everywhere before adding value to your Map.
     */
    public static String normalize(String colId, String value) {

        if (value == null) {
            return "";
        }

        value = value.trim();

        String lowerCol = colId.toLowerCase();

        // 🔹 Numeric handling
        if (NUMERIC_COLUMNS.contains(lowerCol)) {
            return normalizeNumeric(value);
        }

        // 🔹 Date handling
        if (DATE_COLUMNS.contains(lowerCol)) {
            return normalizeDate(value);
        }

        // 🔹 DB/CR handling
        if (DBCR_COLUMNS.contains(lowerCol)) {
            return normalizeDbCr(value);
        }

        // 🔹 Default handling (text fields)
        return normalizeText(value);
    }

    // -------------------------
    // 🔹 Numeric Normalization
    // -------------------------
    private static String normalizeNumeric(String value) {

        try {
            // Remove commas if present (1,000.00)
            value = value.replace(" ", "");
            value = value.replace(",", "");

            BigDecimal bd = new BigDecimal(value);

            // Remove trailing zeros
            bd = bd.stripTrailingZeros();

            return bd.toPlainString();

        } catch (Exception e) {
            return value; // fallback if not valid number
        }
    }

    // -------------------------
    // 🔹 Date Normalization
    // -------------------------
    private static String normalizeDate(String value) {

        // Standardize to YYYY-MM-DD format style
        value = value.replace("/", "-");

        return value;
    }

    // -------------------------
    // 🔹 DB/CR Normalization
    // -------------------------
    private static String normalizeDbCr(String value) {

        return value.trim().toUpperCase();
    }

    // -------------------------
    // 🔹 Text Normalization
    // -------------------------
    private static String normalizeText(String value) {

        // Remove multiple spaces but preserve meaningful spacing
        value = value.replaceAll("\\s+", " ");

        return value;
    }
}
