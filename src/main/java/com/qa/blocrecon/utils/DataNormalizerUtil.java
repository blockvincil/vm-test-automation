
package com.qa.blocrecon.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Set;

public final class DataNormalizerUtil {

    private DataNormalizerUtil() {}

    private static final Set<String> NUMERIC_COLUMNS = Set.of(
            "amount",
            "openingbalance",
            "closingbalance"
    );

    private static final Set<String> DATE_COLUMNS = Set.of(
            "itemdate",
            "openingbalancedate",
            "closingbalancedate",
            "datefield"
    );

    private static final Set<String> DATETIME_COLUMNS = Set.of(
            "datetimefield"
    );

    private static final Set<String> DBCR_COLUMNS = Set.of(
            "db_cr",
            "openingbalance_dbcr",
            "closingbalance_dbcr"
    );

    public static String normalize(String colId, String value) {

        if (value == null) {
            return "";
        }

        value = value.trim();
        String lowerCol = colId.toLowerCase();

        if (NUMERIC_COLUMNS.contains(lowerCol)) {
            return normalizeNumeric(value);
        }

        if (DATE_COLUMNS.contains(lowerCol)) {
            return normalizeDate(value);
        }

        if (DATETIME_COLUMNS.contains(lowerCol)) {
            return normalizeDateTime(value);
        }

        if (DBCR_COLUMNS.contains(lowerCol)) {
            return normalizeDbCr(value);
        }

        return normalizeText(value);
    }

    private static String normalizeNumeric(String value) {
        try {
            value = value.replace(" ", "").replace(",", "");
            BigDecimal bd = new BigDecimal(value);
            return bd.stripTrailingZeros().toPlainString();
        } catch (Exception e) {
            return value;
        }
    }

    private static String normalizeDate(String value) {
        return value.replace("/", "-");
    }

    private static String normalizeDateTime(String value) {

        if (value.isEmpty()) {
            return "";
        }

        String[] formats = {
                "yyyy/MM/dd HH:mm:ss.SSS",
                "yyyy-MM-dd HH:mm:ss.SSS",
                "yyyy/MM/dd HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss"
        };

        for (String format : formats) {
            SimpleDateFormat inputFormat = new SimpleDateFormat(format);
            inputFormat.setLenient(false);

            try {
                java.util.Date date = inputFormat.parse(value);

                SimpleDateFormat outputFormat =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

                return outputFormat.format(date);

            } catch (ParseException ignored) {
                // Try the next supported format.
            }
        }

        return value;
    }

    private static String normalizeDbCr(String value) {
        return value.toUpperCase();
    }

    private static String normalizeText(String value) {
        return value.replaceAll("\\s+", " ");
    }
}
