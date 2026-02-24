package com.qa.blocrecon.testcases;

import com.qa.blocrecon.base.BaseTest;
import com.qa.blocrecon.pages.*;
import com.qa.blocrecon.records.EventRuleHierarchiesPageDTO;
import com.qa.blocrecon.utils.*;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.*;

public class CashReconTest extends BaseTest {

    private LoginPage loginPage;
    private GridPage gridPage;
    private HomePage homePage;
    private EventRuleHierarchiesPage eventRuleHierarchiesPage;
    private SourceExplorerPage sourceExplorerPage;
    private CashItemsPage cashItemsPage;
    private CashBalancesPage cashBalancesPage;
    private EventRuleHierarchiesPageDTO eventRuleHierarchiesPageDTO;

    @BeforeClass
    public void loadTestData() {
        eventRuleHierarchiesPageDTO = JsonDataReader.getEventRuleHierarchiesPageData();
    }

    @BeforeMethod
    public void loginToApp() {
        loginPage = new LoginPage(driver);
        homePage = loginPage.enterCredentialsAndClickLoginButton(prop.getProperty("username"), prop.getProperty("password"));
        homePage.disableResponsiveSidebar();
    }

    private final Map<String, String> cashDashboardsColumnKeyMapping = Map.ofEntries(
            Map.entry("account", "Account"),
            Map.entry("status", "Status"),
            Map.entry("subaccount", "Sub Account"),
            Map.entry("currency", "Currency"),
            Map.entry("baseamount", "Base Amount"),
            Map.entry("basecurrency", "Base Currency"),
            Map.entry("db_cr", "DB/CR"),
            Map.entry("amount", "Amount"),
            Map.entry("fund", "Fund"),
            Map.entry("fundgroup", "Fund Group"),
            Map.entry("openingbalance", "Opening Balance"),
            Map.entry("openingbalance_dbcr", "Opening Balance DB/CR"),
            Map.entry("closingbalance", "Closing Balance"),
            Map.entry("closingbalance_dbcr", "Closing Balance DB/CR"),
            Map.entry("itemdate", "Item Date"),
            Map.entry("openingbalancedate", "Opening Balance Date"),
            Map.entry("closingbalancedate", "Closing Balance Date"),
            Map.entry("description", "Description"),
            Map.entry("status_details", "Status Details"),
            Map.entry("batch_id", "Batch ID"),
            Map.entry("source_batch_id", "Source Batch ID")
    );

    private final Map<String, String> sourceColumnKeyMapping = Map.ofEntries(
            Map.entry("account", "Account"),
            Map.entry("status", "Status"),
            Map.entry("subaccount", "Sub Account"),
            Map.entry("currency", "Currency"),
            Map.entry("baseamount", "Base Amount"),
            Map.entry("basecurrency", "Base Currency"),
            Map.entry("dbcr_4", "DB/CR"),
            Map.entry("amount", "Amount"),
            Map.entry("fund", "Fund"),
            Map.entry("fundgroup", "Fund Group"),
            Map.entry("openingbalance", "Opening Balance"),
            Map.entry("openingbalancedbcr_5", "Opening Balance DB/CR"),
            Map.entry("closingbalance", "Closing Balance"),
            Map.entry("closingbalancedbcr_7", "Closing Balance DB/CR"),
            Map.entry("itemdate", "Item Date"),
            Map.entry("openingbalancedate", "Opening Balance Date"),
            Map.entry("closingbalancedate", "Closing Balance Date"),
            Map.entry("description", "Description"),
            Map.entry("status_details", "Status Details"),
            Map.entry("batch_id", "Batch ID"),
            Map.entry("source_batch_id", "Source Batch ID")
    );

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing valid data")
    @Test(priority = 1, groups = "Cash Items", description = "Importing valid data - Check if data is present in source")
    public void importingValidData_1a() throws Exception {

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "dbcr_4", "openingbalance",
                "openingbalancedbcr_5", "closingbalance", "closingbalancedbcr_7", "itemdate", "openingbalancedate",
                "closingbalancedate", "description");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.searchReconAndTriggerEvent(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getImportData()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Navigate to source explorer and select source
        sourceExplorerPage = homePage.goToSourceExplorer();
        sourceExplorerPage.selectSource("auto1");

        // 4. Check if source table is not empty
        Assert.assertTrue(sourceExplorerPage.isSourcesDataPresent(), "Source table is empty but event is completed");

        // 5. Fetch data from Excel
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/excelFiles/correctAutomationWithStatus.xlsx");

        List<Map<String, String>> excelData =
                ExcelUtil.readExcelNormalizedWithRequiredHeaders(is, "Sheet1", sourceColumnKeyMapping, requiredColumns);

//      Debug print
//        for (Map<String, String> row : excelData)
//            System.out.println(row);

        // 6. Fetch required data from source through UI
        gridPage = new GridPage(driver);
        List<Map<String, String>> sourceGridData = gridPage.getGridRawData(requiredColumns);

//      Debug print
//        System.out.println("\n");
//        for (Map<String, String> row : sourceGridData)
//            System.out.println(row);

        // 7. Compare Excel data with source data
        Assert.assertTrue(ListUtil.compare2DMaps(excelData, sourceGridData));
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing valid data")
    @Test(priority = 2, groups = "Cash Items", description = "Importing valid data - Check if data is present with Validated status in cash items")
    public void importingValidData_1b() throws Exception {

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "db_cr", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "description", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.searchReconAndTriggerEvent(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getImportData()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Fetch data with status and status details from Excel
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/excelFiles/correctAutomationWithStatus.xlsx");

        List<Map<String, String>> excelData =
                ExcelUtil.readExcelNormalizedWithRequiredHeaders(is, "Sheet1", cashDashboardsColumnKeyMapping, requiredColumns);

//      Debug print
//        for (Map<String, String> row : excelData)
//            System.out.println(row);

        // 4. Navigate to cash items and select recon & view
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        // 5. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        // 6. Fetch required data from Cash Items through UI
        gridPage = new GridPage(driver);
        List<Map<String, String>> cashItemsGridData = gridPage.getGridRawData(requiredColumns);
//      Debug print
//        System.out.println("\n");
//        for (Map<String, String> row : cashItemsGridData)
//            System.out.println(row);

        // 7. Compare Excel data with cash items data
        Assert.assertTrue(ListUtil.compare2DMaps(excelData, cashItemsGridData));
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing valid data")
    @Test(priority = 3, groups = "Cash Items", description = "Importing valid data - Check if entries are recorded properly in cash balances")
    public void importingValidData_1c() throws Exception {

        /*  Assumptions:
            1. Test data is properly imported to the source
               (This test case does not perform source vs test data validation)

            2. Test data has flown into cash items after validations
               (This test case does not perform cash items vs enriched source data validation)
         */

        // 0. Define the list of required columns required for validation
        List<String> requiredColumnsFromCashItems = Arrays.asList("account", "fund", "subaccount", "currency", "closingbalance",
                "closingbalance_dbcr", "batch_id", "source_batch_id");

        List<String> requiredColumnsFromCashBalances = Arrays.asList("account", "fund", "subaccount", "currency", "closingbalance",
                "closingbalance_dbcr", "balance_id");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.searchReconAndTriggerEvent(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getImportData()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Navigate to cash items and select recon
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        // 4. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        // 5. Fetch required data from Cash Items through UI
        gridPage = new GridPage(driver);
        List<Map<String, String>> cashItemsGridData = gridPage.getGridRawData(requiredColumnsFromCashItems, "cashItems_balancesValidation");
//      Debug print
//        for (Map<String, String> row : cashItemsGridData)
//            System.out.println(row);

        // 6. Navigate to cash balances and select recon
        cashBalancesPage = homePage.goToCashBalances();
        cashBalancesPage.selectRecon(prop.getProperty("recon_name"));

        Assert.assertTrue(cashBalancesPage.isCashBalancesDataPresent(), "Cash balances table is empty but event is completed");

        // 7. Fetch required data from Cash Balances through UI
        List<Map<String, String>> cashBalancesData = gridPage.getGridRawData(requiredColumnsFromCashBalances, "cashBalances");
//      Debug print
//        System.out.println("\n");
//        for (Map<String, String> row : cashBalancesData)
//            System.out.println(row);

        // 8. Compare Cash Items data with Cash Balances data
        Assert.assertTrue(ListUtil.compare2DMaps(cashItemsGridData, cashBalancesData));
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data")
    @Test(priority = 4, groups = "Cash Items", description = "Importing invalid data - Check if data is present in source")
    public void importingInvalidData_2a() throws Exception {

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "dbcr_4", "openingbalance",
                "openingbalancedbcr_5", "closingbalance", "closingbalancedbcr_7", "itemdate", "openingbalancedate",
                "closingbalancedate", "description");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.searchReconAndTriggerEvent(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB1_openingClosingInconsistent()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Navigate to source explorer and select source
        sourceExplorerPage = homePage.goToSourceExplorer();
        sourceExplorerPage.selectSource("auto1");

        // 4. Check if source table is not empty
        Assert.assertTrue(sourceExplorerPage.isSourcesDataPresent(), "Source table is empty but event is completed");

        // 5. Fetch data from Excel
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/excelFiles/openingClosingInconsistentWithStatus.xlsx");

        List<Map<String, String>> excelData =
                ExcelUtil.readExcelNormalizedWithRequiredHeaders(is, "Sheet1", sourceColumnKeyMapping, requiredColumns);

//      Debug print
//        for (Map<String, String> row : excelData)
//            System.out.println(row);

        // 6. Fetch required data from source through UI
        gridPage = new GridPage(driver);
        List<Map<String, String>> sourceGridData = gridPage.getGridRawData(requiredColumns);

//      Debug print
//        System.out.println("\n");
//        for (Map<String, String> row : sourceGridData)
//            System.out.println(row);

        // 7. Compare Excel data with source data
        Assert.assertTrue(ListUtil.compare2DMaps(excelData, sourceGridData));
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data")
    @Test(priority = 5, groups = "Cash Items", description = "Importing invalid data - Verify that no entries are recorded in cash balances for the failed subaccounts")
    public void importingInvalidData_2b() throws Exception {

        /*  Assumptions:
            1. Test data is properly imported to the source
               (This test case does not perform source vs test data validation)

            2. Test data has flown into cash items after validations
               (This test case does not perform cash items vs enriched source data validation)
         */

        // 0. Define the list of required columns required for validation
        List<String> requiredColumnsFromCashItems = Arrays.asList("account", "fund", "subaccount", "currency", "closingbalance",
                "closingbalance_dbcr", "batch_id", "source_batch_id");

        List<String> requiredColumnsFromCashBalances = Arrays.asList("account", "fund", "subaccount", "currency", "closingbalance",
                "closingbalance_dbcr", "balance_id");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.searchReconAndTriggerEvent(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB1_openingClosingInconsistent()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Navigate to cash items and select recon
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        // 4. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        // 5. Fetch required data from Cash Items through UI
        gridPage = new GridPage(driver);
        List<Map<String, String>> cashItemsGridData = gridPage.getGridRawData(requiredColumnsFromCashItems, "cashItems_balancesValidation");
//      Debug print
//        for (Map<String, String> row : cashItemsGridData)
//            System.out.println(row);

        // 6. Navigate to cash balances and select recon
        cashBalancesPage = homePage.goToCashBalances();
        cashBalancesPage.selectRecon(prop.getProperty("recon_name"));

        Assert.assertTrue(cashBalancesPage.isCashBalancesDataPresent(), "Cash balances table is empty but event is completed");

        // 7. Fetch required data from Cash Balances through UI
        List<Map<String, String>> cashBalancesData = gridPage.getGridRawData(requiredColumnsFromCashBalances, "cashBalances");
//      Debug print
//        System.out.println("\n");
//        for (Map<String, String> row : cashBalancesData)
//            System.out.println(row);

        // 8. Compare Cash Items data with Cash Balances data
        Assert.assertTrue(ListUtil.compare2DMaps(cashItemsGridData, cashBalancesData));
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data")
    @Test(priority = 6, groups = "Cash Items", description = "Importing invalid data - Opening/Closing balance inconsistent")
    public void importingInvalidData_2c() throws Exception {

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "description", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.searchReconAndTriggerEvent(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB1_openingClosingInconsistent()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Read the required columns from the excel file
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/excelFiles/openingClosingInconsistentWithStatus.xlsx");

        List<Map<String, String>> excelData =
                ExcelUtil.readExcelNormalizedWithRequiredHeaders(is, "Sheet1", cashDashboardsColumnKeyMapping, requiredColumns);

//      Debug print
//        for (Map<String, String> excelDatum : excelData)
//            System.out.println(excelDatum);

        // 4. Navigate to cash items and select recon & view
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        // 5. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        // 6. Get required columns from Cash Items dashboard
        gridPage = new GridPage(driver);
        List<Map<String, String>> rawData = gridPage.getGridRawData(requiredColumns);

//      Debug print
//        System.out.println("\n");
//        for (Map<String, String> row : rawData)
//            System.out.println(row);

        // 8. Compare Cash Items data with expected data
        Assert.assertTrue(ListUtil.compare2DMaps(excelData, rawData));

    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data")
    @Test(priority = 7, groups = "Cash Items", description = "Importing invalid data - Balance missing or inconsistent")
    public void importingInvalidData_2d() throws Exception {

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "description", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.searchReconAndTriggerEvent(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB1_balanceMissingOrInconsistent()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Read the required columns from the excel file
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/excelFiles/balanceMissingOrInconsistentWithStatus.xlsx");

        List<Map<String, String>> excelData =
                ExcelUtil.readExcelNormalizedWithRequiredHeaders(is, "Sheet1", cashDashboardsColumnKeyMapping, requiredColumns);

//      Debug print
//        for (Map<String, String> excelDatum : excelData)
//            System.out.println(excelDatum);

        // 4. Navigate to cash items and select recon & view
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        // 5. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        // 6. Get required columns from Cash Items dashboard
        gridPage = new GridPage(driver);
        List<Map<String, String>> rawData = gridPage.getGridRawData(requiredColumns);

//      Debug print
//        System.out.println("\n");
//        for (Map<String, String> row : rawData)
//            System.out.println(row);

        // 8. Compare Cash Items data with expected data
        Assert.assertTrue(ListUtil.compare2DMaps(excelData, rawData));

    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data")
    @Test(priority = 8, groups = "Cash Items", description = "Importing invalid data - Missing Mandatory Fields")
    public void importingInvalidData_2e() throws Exception {

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "description", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.searchReconAndTriggerEvent(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB1_missingMandatoryFields()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Read the required columns from the excel file
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/excelFiles/missingMandatoryFieldsWithStatus.xlsx");

        List<Map<String, String>> excelData =
                ExcelUtil.readExcelNormalizedWithRequiredHeaders(is, "Sheet1", cashDashboardsColumnKeyMapping, requiredColumns);

//      Debug print
//        for (Map<String, String> excelDatum : excelData)
//            System.out.println(excelDatum);

        // 4. Navigate to cash items and select recon & view
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        // 5. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        // 6. Get required columns from Cash Items dashboard
        gridPage = new GridPage(driver);
        List<Map<String, String>> rawData = gridPage.getGridRawData(requiredColumns);

//      Debug print
//        System.out.println("\n");
//        for (Map<String, String> row : rawData)
//            System.out.println(row);

        // 8. Compare Cash Items data with expected data
        Assert.assertTrue(ListUtil.compare2DMaps(excelData, rawData));

    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data")
    @Test(priority = 9, groups = "Cash Items", description = "Importing invalid data - Account Mapping Not Found")
    public void importingInvalidData_2f() throws Exception {

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "description", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.searchReconAndTriggerEvent(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB1_accountMappingNotFound()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Read the required columns from the excel file
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/excelFiles/accountMappingNotFoundWithStatus.xlsx");

        List<Map<String, String>> excelData =
                ExcelUtil.readExcelNormalizedWithRequiredHeaders(is, "Sheet1", cashDashboardsColumnKeyMapping, requiredColumns);

//      Debug print
//        for (Map<String, String> excelDatum : excelData)
//            System.out.println(excelDatum);

        // 4. Navigate to cash items and select recon & view
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        // 5. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        // 6. Get required columns from Cash Items dashboard
        gridPage = new GridPage(driver);
        List<Map<String, String>> rawData = gridPage.getGridRawData(requiredColumns);

//      Debug print
//        System.out.println("\n");
//        for (Map<String, String> row : rawData)
//            System.out.println(row);

        // 8. Compare Cash Items data with expected data
        Assert.assertTrue(ListUtil.compare2DMaps(excelData, rawData));

    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data")
    @Test(priority = 10, groups = "Cash Items", description = "Importing invalid data - Failed In Transformations")
    public void importingInvalidData_2g() throws Exception {

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "description", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.searchReconAndTriggerEvent(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB1_failedInTransformation()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventFailedOrCompletedWithError(
                prop.getProperty("recon_id")
        );

        // 3. Read the required columns from the excel file
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/excelFiles/failedInTransformationWithStatus.xlsx");

        List<Map<String, String>> excelData =
                ExcelUtil.readExcelNormalizedWithRequiredHeaders(is, "Sheet1", cashDashboardsColumnKeyMapping, requiredColumns);

//      Debug print
//        for (Map<String, String> excelDatum : excelData)
//            System.out.println(excelDatum);

        // 4. Navigate to cash items and select recon & view
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        // 5. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        // 6. Get required columns from Cash Items dashboard
        gridPage = new GridPage(driver);
        List<Map<String, String>> rawData = gridPage.getGridRawData(requiredColumns);

//      Debug print
//        System.out.println("\n");
//        for (Map<String, String> row : rawData)
//            System.out.println(row);

        // 8. Compare Cash Items data with expected data
        Assert.assertTrue(ListUtil.compare2DMaps(excelData, rawData));
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data from 2 batches")
    @Test(priority = 11, groups = "Cash Items", description = "Importing invalid data from two batches - Check if data is present in source")
    public void importingInvalidDataFromTwoBatches_3a() throws Exception {

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "dbcr_4", "openingbalance",
                "openingbalancedbcr_5", "closingbalance", "closingbalancedbcr_7", "itemdate", "openingbalancedate",
                "closingbalancedate", "description");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.searchReconAndTriggerEvent(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB2_openingClosingInconsistent()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Navigate to source explorer and select source
        sourceExplorerPage = homePage.goToSourceExplorer();
        sourceExplorerPage.selectSource("auto1");

        // 4. Check if source table is not empty
        Assert.assertTrue(sourceExplorerPage.isSourcesDataPresent(), "Source table is empty but event is completed");

        // 5. Fetch data from CSV
        InputStream is1 = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/csvFiles/openingClosingInconsistentWithStatus1.csv");

        List<Map<String, String>> twoBatchesCombinedCsvData =
                CsvUtil.readCsvNormalizedWithRequiredHeaders(
                        is1,
                        sourceColumnKeyMapping,
                        requiredColumns
                );

        InputStream is2 = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/csvFiles/openingClosingInconsistentWithStatus2.csv");

        List<Map<String, String>> secondBatchCsvData =
                CsvUtil.readCsvNormalizedWithRequiredHeaders(
                        is2,
                        sourceColumnKeyMapping,
                        requiredColumns
                );

        // Append
        twoBatchesCombinedCsvData.addAll(secondBatchCsvData);

//      Debug print
        for (Map<String, String> row : twoBatchesCombinedCsvData)
            System.out.println(row);

        // 6. Fetch required data from source through UI
        gridPage = new GridPage(driver);
        List<Map<String, String>> sourceGridData = gridPage.getGridRawData(requiredColumns);

//      Debug print
        System.out.println("\n");
        for (Map<String, String> row : sourceGridData)
            System.out.println(row);

        // 7. Compare Excel data with source data
        Assert.assertTrue(ListUtil.compare2DMaps(twoBatchesCombinedCsvData, sourceGridData));
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data from 2 batches")
    @Description("Known issue: Incorrect entries in cash balances")
    @Issue("JIRA yet to be created")
    @Test(priority = 12, groups = "Cash Items", description = "Importing invalid data from two batches - Verify that no entries are recorded in cash balances for the failed subaccounts")
    public void importingInvalidDataFromTwoBatches_3b() throws Exception {

        /*  Assumptions:
            1. Test data is properly imported to the source
               (This test case does not perform source vs test data validation)

            2. Test data has flown into cash items after validations
               (This test case does not perform cash items vs enriched source data validation)
         */

        boolean isBugStillPresent=true;

        if (isBugStillPresent) {
            throw new SkipException("Skipping due to known bug");
        }

        // 0. Define the list of required columns required for validation
        List<String> requiredColumnsFromCashItems = Arrays.asList("account", "fund", "subaccount", "currency", "closingbalance",
                "closingbalance_dbcr", "batch_id", "source_batch_id");

        List<String> requiredColumnsFromCashBalances = Arrays.asList("account", "fund", "subaccount", "currency", "closingbalance",
                "closingbalance_dbcr", "balance_id");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.searchReconAndTriggerEvent(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB2_openingClosingInconsistent()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Navigate to cash items and select recon
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        // 4. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        // 5. Fetch required data from Cash Items through UI
        gridPage = new GridPage(driver);
        List<Map<String, String>> cashItemsGridData = gridPage.getGridRawData(requiredColumnsFromCashItems, "cashItems_balancesValidation");

//      Debug print
//        for (Map<String, String> row : cashItemsGridData)
//            System.out.println(row);

        // 6. Navigate to cash balances and select recon
        cashBalancesPage = homePage.goToCashBalances();
        cashBalancesPage.selectRecon(prop.getProperty("recon_name"));

        Assert.assertTrue(cashBalancesPage.isCashBalancesDataPresent(), "Cash balances table is empty but event is completed");

        // 7. Fetch required data from Cash Balances through UI
        List<Map<String, String>> cashBalancesData = gridPage.getGridRawData(requiredColumnsFromCashBalances, "cashBalances");
//      Debug print
//        System.out.println("\n");
//        for (Map<String, String> row : cashBalancesData)
//            System.out.println(row);

        // 8. Compare Cash Items data with Cash Balances data
        Assert.assertTrue(ListUtil.compare2DMaps(cashItemsGridData, cashBalancesData));
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data from 2 batches")
    @Test(priority = 13, groups = "Cash Items", description = "Importing invalid data - Opening/Closing balance inconsistent")
    public void importingInvalidDataFromTwoBatches_3c() throws Exception {

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "description", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.searchReconAndTriggerEvent(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB2_openingClosingInconsistent()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Read the required columns from the CSV file
        InputStream is1 = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/csvFiles/openingClosingInconsistentWithStatus1.csv");

        List<Map<String, String>> twoBatchesCombinedCsvData =
                CsvUtil.readCsvNormalizedWithRequiredHeaders(
                        is1,
                        cashDashboardsColumnKeyMapping,
                        requiredColumns
                );

        InputStream is2 = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/csvFiles/openingClosingInconsistentWithStatus2.csv");

        List<Map<String, String>> secondBatchCsvData =
                CsvUtil.readCsvNormalizedWithRequiredHeaders(
                        is2,
                        cashDashboardsColumnKeyMapping,
                        requiredColumns
                );

        twoBatchesCombinedCsvData.addAll(secondBatchCsvData);

//      Debug print
//        for (Map<String, String> excelDatum : twoBatchesCombinedCsvData)
//            System.out.println(excelDatum);

        // 4. Navigate to cash items and select recon
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        // 5. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        // 6. Get required columns from Cash Items dashboard
        gridPage = new GridPage(driver);
        List<Map<String, String>> rawData = gridPage.getGridRawData(requiredColumns);

//      Debug print
//        System.out.println("\n");
//        for (Map<String, String> row : rawData)
//            System.out.println(row);

        // 8. Compare Cash Items data with expected data
        Assert.assertTrue(ListUtil.compare2DMaps(twoBatchesCombinedCsvData, rawData));

    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data from 2 batches")
    @Test(priority = 14, groups = "Cash Items", description = "Importing invalid data - Opening Balance inconsistent with Last Closing Balance")
    public void importingInvalidDataFromTwoBatches_3d() throws Exception {

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "description", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.searchReconAndTriggerEvent(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB2_openingInconsistentWithLastClosing()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Read the required columns from the CSV file
        InputStream is1 = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/csvFiles/openingInconsistentWithLastClosingWithStatus1.csv");

        List<Map<String, String>> twoBatchesCombinedCsvData =
                CsvUtil.readCsvNormalizedWithRequiredHeaders(
                        is1,
                        cashDashboardsColumnKeyMapping,
                        requiredColumns
                );

        InputStream is2 = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/csvFiles/openingInconsistentWithLastClosingWithStatus2.csv");

        List<Map<String, String>> secondBatchCsvData =
                CsvUtil.readCsvNormalizedWithRequiredHeaders(
                        is2,
                        cashDashboardsColumnKeyMapping,
                        requiredColumns
                );

        twoBatchesCombinedCsvData.addAll(secondBatchCsvData);

//      Debug print
//        for (Map<String, String> excelDatum : twoBatchesCombinedCsvData)
//            System.out.println(excelDatum);

        // 4. Navigate to cash items and select recon
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        // 5. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        // 6. Get required columns from Cash Items dashboard
        gridPage = new GridPage(driver);
        List<Map<String, String>> rawData = gridPage.getGridRawData(requiredColumns);

//      Debug print
//        System.out.println("\n");
//        for (Map<String, String> row : rawData)
//            System.out.println(row);

        // 8. Compare Cash Items data with expected data
        Assert.assertTrue(ListUtil.compare2DMaps(twoBatchesCombinedCsvData, rawData));

    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data from 2 batches")
    @Test(priority = 13, groups = "Cash Items", description = "Importing invalid data - Balance missing or inconsistent")
    public void importingInvalidDataFromTwoBatches_3e() throws Exception {

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "description", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.searchReconAndTriggerEvent(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB2_balanceMissingOrInconsistent()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Read the required columns from the CSV file
        InputStream is1 = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/csvFiles/balanceMissingOrInconsistentWithStatus1.csv");

        List<Map<String, String>> twoBatchesCombinedCsvData =
                CsvUtil.readCsvNormalizedWithRequiredHeaders(
                        is1,
                        cashDashboardsColumnKeyMapping,
                        requiredColumns
                );

        InputStream is2 = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/csvFiles/balanceMissingOrInconsistentWithStatus2.csv");

        List<Map<String, String>> secondBatchCsvData =
                CsvUtil.readCsvNormalizedWithRequiredHeaders(
                        is2,
                        cashDashboardsColumnKeyMapping,
                        requiredColumns
                );

        twoBatchesCombinedCsvData.addAll(secondBatchCsvData);

//      Debug print
//        for (Map<String, String> excelDatum : twoBatchesCombinedCsvData)
//            System.out.println(excelDatum);

        // 4. Navigate to cash items and select recon
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        // 5. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        // 6. Get required columns from Cash Items dashboard
        gridPage = new GridPage(driver);
        List<Map<String, String>> rawData = gridPage.getGridRawData(requiredColumns);

//      Debug print
//        System.out.println("\n");
//        for (Map<String, String> row : rawData)
//            System.out.println(row);

        // 8. Compare Cash Items data with expected data
        Assert.assertTrue(ListUtil.compare2DMaps(twoBatchesCombinedCsvData, rawData));

    }
}
