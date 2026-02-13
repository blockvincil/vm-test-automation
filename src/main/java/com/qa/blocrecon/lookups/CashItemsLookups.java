package com.qa.blocrecon.lookups;

import com.qa.blocrecon.utils.DatabaseUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CashItemsLookups {

    private final DatabaseUtil dbUtil;

    public CashItemsLookups(DatabaseUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    /**
     * Takes source data and returns fully enriched data containing columns in cash items.
     */
    public List<List<String>> enrichRawData(
            List<List<String>> excelData) throws SQLException {

        System.out.println("Enriching source data...");

        List<List<String>> enrichedData = new ArrayList<>();

        for (List<String> excelRow : excelData) {
            enrichedData.add(enrichSingleRow(excelRow));
        }

        System.out.println("Source Data enrich completed");
        return enrichedData;
    }

    /* =========================================================
       CORE ENRICHMENT LOGIC
       ========================================================= */

    private List<String> enrichSingleRow(List<String> excelRow)
            throws SQLException {

        // Column positions (0-based in excelRow)
        String subAccount = excelRow.get(0);      // col 7
        String currency = excelRow.get(1);        // col 8
        BigDecimal amount = parseAmount(excelRow.get(2)); // col 9

        // Lookups
        String account = getAccountFromSubAccount(subAccount);
        String fund = getFund(account);
        String fundGroup = getFundGroup(account);

        boolean currencyMatches =
                isAccountCurrencyMatching(account, currency);

        BigDecimal baseAmount = null;
        String baseCurrency = null;

        if (currencyMatches) {
            baseCurrency = getBaseCurrency(account);
            baseAmount = calculateBaseAmount(amount, currency, baseCurrency);
        }

        // Build final row (columns 1–18)
        List<String> finalRow = new ArrayList<>();

        finalRow.add(account);                              // 1. Account
        finalRow.add(formatAmount(baseAmount));             // 2. Base Amount
        finalRow.add(baseCurrency);                         // 3. Base Currency
        finalRow.add(fund);                                 // 4. Fund
        finalRow.add(fundGroup);                            // 5. Fund Group
        finalRow.add("Validated");                          // 6. Status
        finalRow.addAll(excelRow);                          // 7–18

        return finalRow;
    }

    /* =========================================================
       BASE AMOUNT CALCULATION
       ========================================================= */

    private BigDecimal calculateBaseAmount(
            BigDecimal amount,
            String currency,
            String baseCurrency) throws SQLException {

        // Rule 1 & 2
        if (currency.equalsIgnoreCase(baseCurrency)) {
            return amount;
        }

        BigDecimal fxRateOfCurrency = getFxRate(currency);
        BigDecimal fxRateOfBaseCurrency = getFxRate(baseCurrency);

        // Rule 3
        if ("USD".equalsIgnoreCase(currency)
                && !"USD".equalsIgnoreCase(baseCurrency)) {

            return amount.divide(
                    fxRateOfBaseCurrency, 6, RoundingMode.HALF_UP);
        }

        // Rule 4
        if (!"USD".equalsIgnoreCase(currency)
                && "USD".equalsIgnoreCase(baseCurrency)) {

            return amount.multiply(fxRateOfCurrency);
        }

        // Rule 5
        return amount.multiply(fxRateOfCurrency)
                .divide(fxRateOfBaseCurrency, 6, RoundingMode.HALF_UP);
    }

    /* =========================================================
       DB LOOKUPS
       ========================================================= */

    private String getAccountFromSubAccount(String subAccount)
            throws SQLException {

        String query =
                "SELECT account FROM cr_accounts_map WHERE sub_account = ?";

        try (PreparedStatement ps = dbUtil.conn.prepareStatement(query)) {
            ps.setString(1, subAccount);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException(
                        "No account found for sub_account: " + subAccount);
            }
            return rs.getString("account");
        }
    }

    private String getBaseCurrency(String account)
            throws SQLException {

        String query =
                "SELECT base_currency FROM cr_accounts WHERE account = ?";

        try (PreparedStatement ps = dbUtil.conn.prepareStatement(query)) {
            ps.setString(1, account);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException(
                        "No base currency found for account: " + account);
            }
            return rs.getString("base_currency");
        }
    }

    private String getFund(String account)
            throws SQLException {

        String query =
                "SELECT fund FROM cr_accounts WHERE account = ?";

        try (PreparedStatement ps = dbUtil.conn.prepareStatement(query)) {
            ps.setString(1, account);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException(
                        "No fund found for account: " + account);
            }
            return rs.getString("fund");
        }
    }

    private String getFundGroup(String account)
            throws SQLException {

        String query =
                "SELECT fund_group FROM cr_fund_group_map WHERE account = ?";

        try (PreparedStatement ps = dbUtil.conn.prepareStatement(query)) {
            ps.setString(1, account);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("No fund group found for account: " + account);
                return null;
            }
            return rs.getString("fund_group");
        }
    }

    private BigDecimal getFxRate(String currency)
            throws SQLException {

        String query = """
                    SELECT fx_rate
                    FROM cr_fx_rates
                    WHERE currency = ?
                    ORDER BY date DESC
                    LIMIT 1
                """;

        try (PreparedStatement ps = dbUtil.conn.prepareStatement(query)) {
            ps.setString(1, currency);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException(
                        "FX rate not found for currency: " + currency);
            }
            return rs.getBigDecimal("fx_rate");
        }
    }

    /* =========================================================
       HELPER METHODS
       ========================================================= */

    private BigDecimal parseAmount(String rawAmount) {
        // Handles "1 500.00"
        String normalized = rawAmount.replace(" ", "");
        return new BigDecimal(normalized);
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) return null;
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private boolean isAccountCurrencyMatching(
            String account,
            String currency) throws SQLException {

        String query =
                "SELECT account_currency FROM cr_accounts WHERE account = ?";

        try (PreparedStatement ps = dbUtil.conn.prepareStatement(query)) {
            ps.setString(1, account);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException(
                        "No account currency found for account: " + account);
            }

            return rs.getString("account_currency")
                    .equalsIgnoreCase(currency);
        }
    }
}
