package com.qa.blocrecon.pages;

import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.qa.blocrecon.utils.DataNormalizerUtil;
import io.qameta.allure.Step;

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

    // For fetching source data AND cash items data as it is
    public List<Map<String, String>> getGridRawData(List<String> columns) {
        Allure.step("Fetch required data from the Grid for validation");

        // 🔹 Adjust zoom level for balances table
        adjustZoom(15);

        List<Map<String, String>> tableData = new ArrayList<>();
        List<WebElement> rows = driver.findElements(agGridRows);

        for (WebElement row : rows) {

            Map<String, String> rowData = new LinkedHashMap<>();

            for (String colId : columns) {

                String value = row.findElement(getCellByColId(colId))
                        .getText()
                        .trim();

                // 🔥 Centralized normalization
                value = DataNormalizerUtil.normalize(colId, value);

                rowData.put(colId, value);
            }

            tableData.add(rowData);
        }

        adjustZoom(100);
        return tableData;
    }


    public List<Map<String, String>> getGridRawData(List<String> columns, String dataFetchPurpose) {

        adjustZoom(15);

        List<Map<String, String>> tableData = new ArrayList<>();
        List<WebElement> rows = driver.findElements(agGridRows);

        Set<String> uniqueSubAccountSourceBatch = new HashSet<>();

        for (WebElement row : rows) {

            Map<String, String> rowData = new LinkedHashMap<>();

            // 🔹 Special filtering logic for balances validation
            if ("cashItems_balancesValidation".equalsIgnoreCase(dataFetchPurpose)) {

                String statusValue = row.findElement(getCellByColId("status"))
                        .getText()
                        .trim();

                statusValue = DataNormalizerUtil.normalize("status", statusValue);

                if (!"Validated".equalsIgnoreCase(statusValue)) {
                    continue;
                }

                String subAccountValue = row.findElement(getCellByColId("subaccount"))
                        .getText()
                        .trim();

                subAccountValue = DataNormalizerUtil.normalize("subaccount", subAccountValue);

                String sourceBatchValue = row.findElement(getCellByColId("source_batch_id"))
                        .getText()
                        .trim();

                sourceBatchValue = DataNormalizerUtil.normalize("source_batch_id", sourceBatchValue);

                String uniquenessKey = subAccountValue + "|" + sourceBatchValue;

                if (!uniqueSubAccountSourceBatch.add(uniquenessKey)) {
                    continue;
                }
            }

            // 🔹 Normal column extraction
            for (String colId : columns) {

                String value = row.findElement(getCellByColId(colId))
                        .getText()
                        .trim();

                value = DataNormalizerUtil.normalize(colId, value);

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

                        rowData.put("source_batch_id",
                                DataNormalizerUtil.normalize("source_batch_id", sourceBatchId));

                        rowData.put("batch_id",
                                DataNormalizerUtil.normalize("batch_id", batchId));
                    }

                } else {
                    rowData.put(colId, value);
                }
            }

            tableData.add(rowData);
        }

        adjustZoom(100);
        return tableData;
    }



}
