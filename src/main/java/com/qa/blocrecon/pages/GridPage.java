package com.qa.blocrecon.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class GridPage {
    WebDriver driver;
    JavascriptExecutor js;

    public GridPage(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
    }

    private By getCellByColId(String colId) {
        return By.xpath(".//div[@col-id='" + colId + "']");
    }

    private By agGridRows = By.xpath("//div[contains(@class,'ag-center-cols-container')]//div[contains(@class,'ag-row') and @row-index]");

    /**
     * Adjusts the zoom level of the page to the specified percentage.
     *
     * @param zoomPercentage The percentage to which the page should be zoomed.
     */
    private void adjustZoom(int zoomPercentage) {
        js.executeScript("document.body.style.zoom='" + zoomPercentage + "%'");
    }

    private String normalizeValue(String colId, String value) {

        if (value == null) return "";

        value = value.trim();
        value = value.replace("-", "/");

        // Only numeric columns remove internal spaces
        if (colId.equalsIgnoreCase("amount") ||
                colId.equalsIgnoreCase("openingbalance") ||
                colId.equalsIgnoreCase("closingbalance")) {

            value = value.replaceAll("\\s+", "");
        }

        return value;
    }

    // For fetching source data AND cash items data as it is
    public List<Map<String, String>> getGridRawData(List<String> columns) {

        // 🔹 Adjust zoom level for balances table
        adjustZoom(15);

        System.out.println("Data reading started");

        List<Map<String, String>> tableData = new ArrayList<>();
        List<WebElement> rows = driver.findElements(agGridRows);

        for (WebElement row : rows) {

            Map<String, String> rowData = new LinkedHashMap<>();

            for (String colId : columns) {

                String value = row.findElement(getCellByColId(colId))
                        .getText()
                        .trim();

                // 🔹 Normalization
                value = normalizeValue(colId, value);

                rowData.put(colId, value);
            }

            tableData.add(rowData);
        }

        System.out.println("Data reading ended");
        adjustZoom(100);
        return tableData;
    }

    // For fetching cash items data as per balances table AND cash balances data
    public List<Map<String, String>> getGridRawData(List<String> columns, String dataFetchPurpose) {

        // 🔹 Adjust zoom level for balances table
        adjustZoom(15);

        List<Map<String, String>> tableData = new ArrayList<>();
        List<WebElement> rows = driver.findElements(agGridRows);

        // 🔹 Used only for balancesValidation logic
        Set<String> uniqueSubAccounts = new HashSet<>();

        for (WebElement row : rows) {

            Map<String, String> rowData = new LinkedHashMap<>();

            String statusValue = "";
            String subAccountValue = "";

            // 🔹 First pass: read required control fields (status & subaccount)
            if ("cashItems_balancesValidation".equalsIgnoreCase(dataFetchPurpose)) {

                statusValue = row.findElement(getCellByColId("status"))
                        .getText()
                        .trim();

                if (!"Validated".equalsIgnoreCase(statusValue)) {
                    continue; // Skip row immediately
                }

                subAccountValue = row.findElement(getCellByColId("subaccount"))
                        .getText()
                        .trim();

                // If subaccount already processed → skip
                if (!uniqueSubAccounts.add(subAccountValue)) {
                    continue;
                }
            }

            // 🔹 Normal column extraction
            for (String colId : columns) {

                String value = row.findElement(getCellByColId(colId))
                        .getText()
                        .trim();

                value = normalizeValue(colId, value);

                // 🔹 Special handling for cashBalances → balance_id split
                if ("cashBalances".equalsIgnoreCase(dataFetchPurpose)
                        && "balance_id".equalsIgnoreCase(colId)
                        && value.contains("BAT")) {

                    int batIndex = value.lastIndexOf("BAT");

                    if (batIndex > 0) {

                        String batchId = value.substring(0, batIndex);
                        if (batchId.endsWith("_")) {
                            batchId = batchId.substring(0, batchId.length() - 1);
                        }

                        String sourceBatchId = value.substring(batIndex);

                        rowData.put("source_batch_id", sourceBatchId);
                        rowData.put("batch_id", batchId);
                    }

                }
                // 🔹 NEW: Reduce precision for closingbalance ONLY for cashBalances
                else if ("cashBalances".equalsIgnoreCase(dataFetchPurpose)
                        && "closingbalance".equalsIgnoreCase(colId)
                        && !value.isEmpty()) {

                    try {
                        BigDecimal bd = new BigDecimal(value);
                        bd = bd.setScale(1, RoundingMode.HALF_UP);
                        value = bd.toPlainString();
                    } catch (NumberFormatException e) {
                        // Optional: log if needed
                    }

                    rowData.put(colId, value);
                }
                else {
                    rowData.put(colId, value);
                }
            }

            tableData.add(rowData);
        }

        adjustZoom(100);
        return tableData;
    }


}
