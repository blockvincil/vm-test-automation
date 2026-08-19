package com.qa.blocrecon.testcases;

import com.qa.blocrecon.base.BaseTest;
import com.qa.blocrecon.factory.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.SkipException;
import utils.FileAttachmentUtil;
import utils.TestListener;
import com.qa.blocrecon.pages.*;
import com.qa.blocrecon.records.EventRuleHierarchiesPageDTO;
import com.qa.blocrecon.queries.queries;
import com.qa.blocrecon.utils.*;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.time.Duration;
import java.util.*;

@Listeners(TestListener.class)
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
        System.out.println(eventRuleHierarchiesPageDTO);
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
            Map.entry("expected_description", "Description"),
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
    @Test(priority = 1, groups = "Cash Items", description = "1a")
    public void _1a() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/correctAutomationWithStatus.xlsx");

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "dbcr_4", "openingbalance",
                "openingbalancedbcr_5", "closingbalance", "closingbalancedbcr_7", "itemdate", "openingbalancedate",
                "closingbalancedate");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
    @Test(priority = 2, groups = "Cash Items", description = "1b")
    public void _1b() throws Exception {

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/correctAutomationWithStatus.xlsx");

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "db_cr", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
        for (Map<String, String> row : excelData)
            System.out.println(row);

        // 4. Navigate to cash items and select recon & view
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        // 5. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        // 6. Fetch required data from Cash Items through UI
        gridPage = new GridPage(driver);
        List<Map<String, String>> cashItemsGridData = gridPage.getGridRawData(requiredColumns);
//      Debug print
        System.out.println("\n");
        for (Map<String, String> row : cashItemsGridData)
            System.out.println(row);

        // 7. Compare Excel data with cash items data
        Assert.assertTrue(ListUtil.compare2DMaps(excelData, cashItemsGridData));
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing valid data")
    @Test(priority = 3, groups = "Cash Items", description = "1c")
    public void _1c() throws Exception {

        /*  Assumptions:
            1. Test data is properly imported to the source
               (This test case does not perform source vs test data validation)

            2. Test data has flown into cash items after validations
               (This test case does not perform cash items vs enriched source data validation)
         */

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/correctAutomationWithStatus.xlsx");

        // 0. Define the list of required columns required for validation
        List<String> requiredColumnsFromCashItems = Arrays.asList("account", "fund", "subaccount", "currency", "closingbalance",
                "closingbalance_dbcr", "batch_id", "source_batch_id");

        List<String> requiredColumnsFromCashBalances = Arrays.asList("account", "fund", "subaccount", "currency", "closingbalance",
                "closingbalance_dbcr", "balance_id");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
    @Story("Importing invalid data from single batch")
    @Test(priority = 4, groups = "Cash Items", description = "2a")
    public void _2a() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/openingClosingInconsistentWithStatus.xlsx");

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "dbcr_4", "openingbalance",
                "openingbalancedbcr_5", "closingbalance", "closingbalancedbcr_7", "itemdate", "openingbalancedate",
                "closingbalancedate");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
    @Story("Importing invalid data from single batch")
    @Test(priority = 5, groups = "Cash Items", description = "2b")
    public void _2b() throws Exception {

        /*  Assumptions:
            1. Test data is properly imported to the source
               (This test case does not perform source vs test data validation)

            2. Test data has flown into cash items after validations
               (This test case does not perform cash items vs enriched source data validation)
         */

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/openingClosingInconsistentWithStatus.xlsx");

        // 0. Define the list of required columns required for validation
        List<String> requiredColumnsFromCashItems = Arrays.asList("account", "fund", "subaccount", "currency", "closingbalance",
                "closingbalance_dbcr", "batch_id", "source_batch_id");

        List<String> requiredColumnsFromCashBalances = Arrays.asList("account", "fund", "subaccount", "currency", "closingbalance",
                "closingbalance_dbcr", "balance_id");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
    @Story("Importing invalid data from single batch")
    @Test(priority = 6, groups = "Cash Items", description = "2c")
    public void _2c() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/openingClosingInconsistentWithStatus.xlsx");

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
    @Story("Importing invalid data from single batch")
    @Test(priority = 7, groups = "Cash Items", description = "2d")
    public void _2d() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/balanceMissingOrInconsistentWithStatus.xlsx");

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
        for (Map<String, String> excelDatum : excelData)
            System.out.println(excelDatum);

        // 4. Navigate to cash items and select recon & view
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        // 5. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        // 6. Get required columns from Cash Items dashboard
        gridPage = new GridPage(driver);
        List<Map<String, String>> rawData = gridPage.getGridRawData(requiredColumns);

//      Debug print
        System.out.println("\n");
        for (Map<String, String> row : rawData)
            System.out.println(row);

        // 8. Compare Cash Items data with expected data
        Assert.assertTrue(ListUtil.compare2DMaps(excelData, rawData));

    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data from single batch")
    @Test(priority = 8, groups = "Cash Items", description = "2e")
    public void _2e() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/missingMandatoryFieldsWithStatus.xlsx");

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
    @Story("Importing invalid data from single batch")
    @Test(priority = 9, groups = "Cash Items", description = "2f")
    public void _2f() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/accountMappingNotFoundWithStatus.xlsx");

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
    @Story("Importing invalid data from single batch")
    @Test(priority = 10, groups = "Cash Items", description = "2g")
    public void _2g() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/failedInTransformationWithStatus.xlsx");

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
    @Test(priority = 11, groups = "Cash Items", description = "3a")
    public void _3a() throws Exception {

        FileAttachmentUtil.attachCsv("dataFiles/excelFiles/openingClosingInconsistentWithStatus1.xlsx");
        FileAttachmentUtil.attachCsv("dataFiles/excelFiles/openingClosingInconsistentWithStatus2.xlsx");

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "dbcr_4", "openingbalance",
                "openingbalancedbcr_5", "closingbalance", "closingbalancedbcr_7", "itemdate", "openingbalancedate",
                "closingbalancedate");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
//        for (Map<String, String> row : twoBatchesCombinedCsvData)
//            System.out.println(row);

        // 6. Fetch required data from source through UI
        gridPage = new GridPage(driver);
        List<Map<String, String>> sourceGridData = gridPage.getGridRawData(requiredColumns);

//      Debug print
//        System.out.println("\n");
//        for (Map<String, String> row : sourceGridData)
//            System.out.println(row);

        // 7. Compare Excel data with source data
        Assert.assertTrue(ListUtil.compare2DMaps(twoBatchesCombinedCsvData, sourceGridData));
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data from 2 batches")
    @Description("Known issue: Incorrect entries in cash balances")
    @Issue("JIRA yet to be created")
    @Test(priority = 12, groups = "Cash Items", description = "3b")
    public void _3b() throws Exception {

        FileAttachmentUtil.attachCsv("dataFiles/excelFiles/correctAutomationWithStatus.xlsx");

        /*  Assumptions:
            1. Test data is properly imported to the source
               (This test case does not perform source vs test data validation)

            2. Test data has flown into cash items after validations
               (This test case does not perform cash items vs enriched source data validation)
         */

        boolean isBugStillPresent = true;

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

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
    @Test(priority = 13, groups = "Cash Items", description = "3c")
    public void _3c() throws Exception {

        FileAttachmentUtil.attachCsv("dataFiles/csvFiles/openingClosingInconsistentWithStatus1.csv");
        FileAttachmentUtil.attachCsv("dataFiles/csvFiles/openingClosingInconsistentWithStatus2.csv");

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
    @Test(priority = 14, groups = "Cash Items", description = "3d")
    public void _3d() throws Exception {

        FileAttachmentUtil.attachCsv("dataFiles/csvFiles/openingClosingInconsistentWithStatus1.csv");
        FileAttachmentUtil.attachCsv("dataFiles/csvFiles/openingClosingInconsistentWithStatus2.csv");

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
        for (Map<String, String> excelDatum : twoBatchesCombinedCsvData)
            System.out.println(excelDatum);

        // 4. Navigate to cash items and select recon
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        // 5. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        // 6. Get required columns from Cash Items dashboard
        gridPage = new GridPage(driver);
        List<Map<String, String>> rawData = gridPage.getGridRawData(requiredColumns);

//      Debug print
        System.out.println("\n");
        for (Map<String, String> row : rawData)
            System.out.println(row);

        // 8. Compare Cash Items data with expected data
        Assert.assertTrue(ListUtil.compare2DMaps(twoBatchesCombinedCsvData, rawData));

    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data from 2 batches")
    @Test(priority = 15, groups = "Cash Items", description = "3e")
    public void _3e() throws Exception {

        FileAttachmentUtil.attachCsv("dataFiles/csvFiles/balanceMissingOrInconsistentWithStatus1.csv");
        FileAttachmentUtil.attachCsv("dataFiles/csvFiles/balanceMissingOrInconsistentWithStatus2.csv");

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data from 2 batches")
    @Test(priority = 16, groups = "Cash Items", description = "3f")
    public void _3f() throws Exception {

        FileAttachmentUtil.attachCsv("dataFiles/csvFiles/missingMandatoryFieldsWithStatus1.csv");
        FileAttachmentUtil.attachCsv("dataFiles/csvFiles/missingMandatoryFieldsWithStatus2.csv");
        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB2_missingMandatoryFields()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Read the required columns from the CSV file
        InputStream is1 = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/csvFiles/missingMandatoryFieldsWithStatus1.csv");

        List<Map<String, String>> twoBatchesCombinedCsvData =
                CsvUtil.readCsvNormalizedWithRequiredHeaders(
                        is1,
                        cashDashboardsColumnKeyMapping,
                        requiredColumns
                );

        InputStream is2 = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/csvFiles/missingMandatoryFieldsWithStatus2.csv");

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
        Assert.assertTrue(ListUtil.compare2DMaps(twoBatchesCombinedCsvData, rawData));

    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data from two batches")
    @Test(priority = 17, groups = "Cash Items", description = "3g")
    public void _3g() throws Exception {

        FileAttachmentUtil.attachCsv("dataFiles/csvFiles/accountMappingNotFoundWithStatus1.csv");
        FileAttachmentUtil.attachCsv("dataFiles/csvFiles/accountMappingNotFoundWithStatus2.csv");

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB2_accountMappingNotFound()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Read the required columns from the CSV file
        InputStream is1 = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/csvFiles/accountMappingNotFoundWithStatus1.csv");

        List<Map<String, String>> twoBatchesCombinedCsvData =
                CsvUtil.readCsvNormalizedWithRequiredHeaders(
                        is1,
                        cashDashboardsColumnKeyMapping,
                        requiredColumns
                );

        InputStream is2 = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/csvFiles/accountMappingNotFoundWithStatus2.csv");

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
        Assert.assertTrue(ListUtil.compare2DMaps(twoBatchesCombinedCsvData, rawData));

    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data from two batches")
    @Test(priority = 18, groups = "Cash Items", description = "3h")
    public void _3h() throws Exception {

        FileAttachmentUtil.attachCsv("dataFiles/csvFiles/failedInTransformationWithStatus1.csv");
        FileAttachmentUtil.attachCsv("dataFiles/csvFiles/failedInTransformationWithStatus2.csv");

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB2_failedInTransformation()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventFailedOrCompletedWithError(
                prop.getProperty("recon_id")
        );

        // 3. Read the required columns from the CSV file
        InputStream is1 = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/csvFiles/failedInTransformationWithStatus1.csv");

        List<Map<String, String>> twoBatchesCombinedCsvData =
                CsvUtil.readCsvNormalizedWithRequiredHeaders(
                        is1,
                        cashDashboardsColumnKeyMapping,
                        requiredColumns
                );

        InputStream is2 = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/csvFiles/failedInTransformationWithStatus2.csv");

        List<Map<String, String>> secondBatchCsvData =
                CsvUtil.readCsvNormalizedWithRequiredHeaders(
                        is2,
                        cashDashboardsColumnKeyMapping,
                        requiredColumns
                );

        twoBatchesCombinedCsvData.addAll(secondBatchCsvData);

//      Debug print
//        for (Map<String, String> excelDatum : secondBatchCsvData)
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
        Assert.assertTrue(ListUtil.compare2DMaps(twoBatchesCombinedCsvData, rawData));
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Importing invalid data from two batches")
    @Description("Known issue: Clarity required regarding this case")
    @Issue("JIRA yet to be created")
    @Test(priority = 19, groups = "Cash Items", description = "3i")
    public void _3i() throws Exception {

        FileAttachmentUtil.attachCsv("dataFiles/csvFiles/duplicateBatchIdentifiedWithStatus1.csv");
        FileAttachmentUtil.attachCsv("dataFiles/csvFiles/duplicateBatchIdentifiedWithStatus2.csv");

        boolean isBugStillPresent = true;

        if (isBugStillPresent) {
            throw new SkipException("Skipping due to known bug");
        }

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB2_duplicateBatchIdentified()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Read the required columns from the CSV file
        InputStream is1 = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/csvFiles/duplicateBatchIdentifiedWithStatus1.csv");

        List<Map<String, String>> twoBatchesCombinedCsvData =
                CsvUtil.readCsvNormalizedWithRequiredHeaders(
                        is1,
                        cashDashboardsColumnKeyMapping,
                        requiredColumns
                );

        InputStream is2 = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/csvFiles/duplicateBatchIdentifiedWithStatus2.csv");

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
        Assert.assertTrue(ListUtil.compare2DMaps(twoBatchesCombinedCsvData, rawData));
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Have calculations and check citems dashboard")
    @Test(priority = 20, groups = "Cash Items", description = "4a")
    public void _4a() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/4a_AfterCalculation.xlsx");

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "db_cr", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getCase_4a()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Fetch data with status and status details from Excel
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/case4/4a_AfterCalculation.xlsx");

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
    @Story("Have calculations and check citems dashboard")
    @Test(priority = 21, groups = "Cash Items", description = "4b")
    public void _4b() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/4b_AfterCalculation.xlsx");

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "db_cr", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getCase_4b()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Fetch data with status and status details from Excel
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/case4/4b_AfterCalculation.xlsx");

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
    @Story("Have calculations and check citems dashboard")
    @Test(priority = 22, groups = "Cash Items", description = "4d")
    public void _4d() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/4d_AfterCalculation.xlsx");

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "db_cr", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getCase_4d()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Fetch data with status and status details from Excel
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/case4/4d_AfterCalculation.xlsx");

        List<Map<String, String>> excelData =
                ExcelUtil.readExcelNormalizedWithRequiredHeaders(is, "Sheet1", cashDashboardsColumnKeyMapping, requiredColumns);

//      Debug print
        for (Map<String, String> row : excelData)
            System.out.println(row);

        // 4. Navigate to cash items and select recon & view
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        // 5. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        // 6. Fetch required data from Cash Items through UI
        gridPage = new GridPage(driver);
        List<Map<String, String>> cashItemsGridData = gridPage.getGridRawData(requiredColumns);
//      Debug print
        System.out.println("\n");
        for (Map<String, String> row : cashItemsGridData)
            System.out.println(row);

        // 7. Compare Excel data with cash items data
        Assert.assertTrue(ListUtil.compare2DMaps(excelData, cashItemsGridData));
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Have calculations and check citems dashboard")
    @Test(priority = 23, groups = "Cash Items", description = "4e")
    public void _4e() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/4e_AfterCalculation.xlsx");

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "db_cr", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getCase_4e()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Fetch data with status and status details from Excel
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/case4/4e_AfterCalculation.xlsx");

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
    @Story("Right click actions on cash items dashboard without maker checker")
    @Test(priority = 24, groups = "Cash Items", description = "7a")
    public void _7a() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/balanceMissingOrInconsistentWithStatus.xlsx");

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "db_cr", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
        softAssert.assertTrue(ListUtil.compare2DMaps(excelData, rawData));

        gridPage.adjustZoom(15);

        String firstFailedAssetId = cashItemsPage.getAssetIdForFirstFailedRecord();
        System.out.println(firstFailedAssetId);

        cashItemsPage.ignoreItem();

        Assert.assertEquals(cashItemsPage.getStatusBasedOnAssetId(cashItemsPage.getStatusBasedOnAssetId(firstFailedAssetId)), "Ignored", "Wrong status");
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Right click actions on cash items dashboard without maker checker")
    @Test(priority = 25, groups = "Cash Items", description = "7b")
    public void _7b() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/balanceMissingOrInconsistentWithStatus.xlsx");

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "db_cr", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
        softAssert.assertTrue(ListUtil.compare2DMaps(excelData, rawData));

        gridPage.adjustZoom(15);

        cashItemsPage.ignoreBatch();

        List<String> statusListFromUi = cashItemsPage.getStatusBasedOnBatchId(cashItemsPage.getBatchIdForFirstFailedRecord());
        boolean allIgnored = statusListFromUi.stream()
                .allMatch(status -> status.equals("Ignored"));
        Assert.assertTrue(allIgnored, "Wrong status");
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Right click actions on cash items dashboard without maker checker")
    @Test(priority = 26, groups = "Cash Items", description = "7c")
    public void _7c() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/balanceMissingOrInconsistentWithStatus.xlsx");

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "db_cr", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
        softAssert.assertTrue(ListUtil.compare2DMaps(excelData, rawData));

        gridPage.adjustZoom(15);

        cashItemsPage.ignoreFile();

        List<String> statusListFromUi = cashItemsPage.getStatusBasedOnFileName("balanceMissingOrInconsistentWithStatus.xlsx");
//        System.out.println(statusListFromUi);
        boolean allIgnored = statusListFromUi.stream()
                .allMatch(status -> status.equals("Ignored"));
        Assert.assertTrue(allIgnored, "Wrong status");
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Right click actions on cash items dashboard without maker checker")
    @Test(priority = 27, groups = "Cash Items", description = "7f")
    public void _7f() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/case7/7f_file1.xlsx");
        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/case7/7f_file2.xlsx");

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "db_cr", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getCase_7f()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );
        // 4. Navigate to cash items and select recon & view
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        // 5. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        gridPage = new GridPage(driver);
        gridPage.adjustZoom(15);

        String batchIdForFirstFailedRecord = cashItemsPage.getBatchIdForFirstFailedRecord();

        cashItemsPage.addItemToBatch();

        List<String> statusListFromUi = cashItemsPage.getStatusBasedOnBatchId(batchIdForFirstFailedRecord);
        boolean allValidated = statusListFromUi.stream()
                .allMatch(status -> status.equals("Validated"));
        Assert.assertTrue(allValidated, "Wrong status");
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Right click actions on cash items dashboard without maker checker")
    @Test(priority = 26, groups = "Cash Items", description = "7g")
    public void _7g() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/balanceMissingOrInconsistentWithStatus.xlsx");

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "db_cr", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
        softAssert.assertTrue(ListUtil.compare2DMaps(excelData, rawData));

        gridPage.adjustZoom(15);

        String batchIdForFirstFailedRecord = cashItemsPage.getBatchIdForFirstFailedRecord();

        cashItemsPage.updateBatchBalances(4500);

        List<String> statusListFromUi = cashItemsPage.getStatusBasedOnBatchId(batchIdForFirstFailedRecord);
        boolean allValidated = statusListFromUi.stream()
                .allMatch(status -> status.equals("Validated"));
        Assert.assertTrue(allValidated, "Wrong status");
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Right click actions on cash items dashboard without maker checker")
    @Test(priority = 27, groups = "Cash Items", description = "7h")
    public void _7h() throws Exception {

        FileAttachmentUtil.attachCsv("dataFiles/excelFiles/openingInconsistentWithLastClosingWithStatus1.csv");
        FileAttachmentUtil.attachCsv("dataFiles/excelFiles/openingInconsistentWithLastClosingWithStatus2.csv");

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
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
        for (Map<String, String> excelDatum : twoBatchesCombinedCsvData)
            System.out.println(excelDatum);

        // 4. Navigate to cash items and select recon
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        // 5. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        // 6. Get required columns from Cash Items dashboard
        gridPage = new GridPage(driver);
        List<Map<String, String>> rawData = gridPage.getGridRawData(requiredColumns);

//      Debug print
        System.out.println("\n");
        for (Map<String, String> row : rawData)
            System.out.println(row);

        // 8. Compare Cash Items data with expected data
        Assert.assertTrue(ListUtil.compare2DMaps(twoBatchesCombinedCsvData, rawData));

        gridPage.adjustZoom(15);

        String batchIdForFirstFailedRecord = cashItemsPage.getBatchIdForFirstFailedRecord();

        cashItemsPage.addBatchAndReprocess();

        List<String> statusListFromUi = cashItemsPage.getStatusBasedOnBatchId(batchIdForFirstFailedRecord);
        boolean allValidated = statusListFromUi.stream()
                .allMatch(status -> status.equals("Validated"));
        Assert.assertTrue(allValidated, "Wrong status");
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Right click actions on cash items dashboard without maker checker")
    @Test(priority = 27, groups = "Cash Items", description = "7i")
    public void _7i() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/correctAutomationWithStatus.xlsx");

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "db_cr", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getCase_7i()
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

        gridPage = new GridPage(driver);
        gridPage.adjustZoom(15);

        String batchIdForFirstFailedRecord = cashItemsPage.getBatchIdForFirstFailedRecord();

        cashItemsPage.approveDuplicates();

        List<String> statusListFromUi = cashItemsPage.getStatusDetailsBasedOnBatchId(batchIdForFirstFailedRecord);
        boolean statusCheck = statusListFromUi.stream()
                .allMatch(status -> status.equals("Duplicate Batch Identified"));
        Assert.assertFalse(statusCheck, "Wrong status");
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Right click actions with maker checker enabled")
    @Test(priority = 28, groups = "Cash Items", description = "6a")
    public void _6a() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/balanceMissingOrInconsistentWithStatus.xlsx");

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        // Store the Chrome driver instance for switching back later
        WebDriver chromeDriver = driver;

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "db_cr", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("mc_recon_name"),
                eventRuleHierarchiesPageDTO.getB1_balanceMissingOrInconsistent()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("mc_recon_id")
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
        cashItemsPage.selectRecon(prop.getProperty("mc_recon_name"));

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
        softAssert.assertTrue(ListUtil.compare2DMaps(excelData, rawData));


        gridPage.adjustZoom(15);
        String firstFailedAssetId = cashItemsPage.getAssetIdForFirstFailedRecord();
        System.out.println(firstFailedAssetId);

        cashItemsPage.ignoreItem();

        // Open a new Edge browser after existing operations
        WebDriver edgeDriver = DriverFactory.createStandaloneEdgeDriver(
                Boolean.parseBoolean(prop.getProperty("remote")),
                prop.getProperty("selenium.grid.url")
        );
        edgeDriver.get(prop.getProperty("url").trim());

        try {
            // Perform operations in Edge browser here
            LoginPage edgeLoginPage = new LoginPage(edgeDriver);
            HomePage edgeHomePage = edgeLoginPage.enterCredentialsAndClickLoginButton(prop.getProperty("username2"), prop.getProperty("password2"));
            edgeHomePage.disableResponsiveSidebar();

            CashItemsPage edgeCashItemsPage = edgeHomePage.goToCashItems();
            edgeCashItemsPage.selectRecon(prop.getProperty("mc_recon_name"));

            GridPage edgeGridPage = new GridPage(edgeDriver);
            edgeGridPage.adjustZoom(15);
            edgeCashItemsPage.approve();
            edgeGridPage.adjustZoom(100);

            // Switch back to Chrome for validation
            driver = chromeDriver;
            cashItemsPage.refresh();

            // Perform validation in Chrome
            Assert.assertEquals(cashItemsPage.getStatusBasedOnAssetId(firstFailedAssetId), "Ignored", "Wrong status");

        } finally {
            // Close the Edge browser instance
            edgeDriver.quit();
        }
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Right click actions with maker checker enabled")
    @Test(priority = 29, groups = "Cash Items", description = "6b")
    public void _6b() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/balanceMissingOrInconsistentWithStatus.xlsx");

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        WebDriver chromeDriver = driver;

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "db_cr", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("mc_recon_name"),
                eventRuleHierarchiesPageDTO.getB1_balanceMissingOrInconsistent()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("mc_recon_id")
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
        cashItemsPage.selectRecon(prop.getProperty("mc_recon_name"));

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
        softAssert.assertTrue(ListUtil.compare2DMaps(excelData, rawData));

        gridPage.adjustZoom(15);

        cashItemsPage.ignoreBatch();

        // Open a new Edge browser after existing operations
        WebDriver edgeDriver = DriverFactory.createStandaloneEdgeDriver(
                Boolean.parseBoolean(prop.getProperty("remote")),
                prop.getProperty("selenium.grid.url")
        );
        edgeDriver.get(prop.getProperty("url").trim());

        try {
            // Perform operations in Edge browser here
            LoginPage edgeLoginPage = new LoginPage(edgeDriver);
            HomePage edgeHomePage = edgeLoginPage.enterCredentialsAndClickLoginButton(prop.getProperty("username2"), prop.getProperty("password2"));
            edgeHomePage.disableResponsiveSidebar();

            CashItemsPage edgeCashItemsPage = edgeHomePage.goToCashItems();
            edgeCashItemsPage.selectRecon(prop.getProperty("mc_recon_name"));

            GridPage edgeGridPage = new GridPage(edgeDriver);
            edgeGridPage.adjustZoom(15);
            edgeCashItemsPage.approve();
            edgeGridPage.adjustZoom(100);

            // Switch back to Chrome for validation
            driver = chromeDriver;
            cashItemsPage.refresh();

            List<String> statusListFromUi = cashItemsPage.getStatusBasedOnBatchId(cashItemsPage.getBatchIdForFirstFailedRecord());
            boolean allIgnored = statusListFromUi.stream()
                    .allMatch(status -> status.equals("Ignored"));
            Assert.assertTrue(allIgnored, "Wrong status");
        } finally {
            // Close the Edge browser instance
            edgeDriver.quit();
        }
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Right click actions with maker checker enabled")
    @Test(priority = 30, groups = "Cash Items", description = "6c")
    public void _6c() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/balanceMissingOrInconsistentWithStatus.xlsx");

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        WebDriver chromeDriver = driver;

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "db_cr", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("mc_recon_name"),
                eventRuleHierarchiesPageDTO.getB1_balanceMissingOrInconsistent()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("mc_recon_id")
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
        cashItemsPage.selectRecon(prop.getProperty("mc_recon_name"));

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
        softAssert.assertTrue(ListUtil.compare2DMaps(excelData, rawData));

        gridPage.adjustZoom(15);

        cashItemsPage.ignoreFile();

        // Open a new Edge browser after existing operations
        WebDriver edgeDriver = DriverFactory.createStandaloneEdgeDriver(
                Boolean.parseBoolean(prop.getProperty("remote")),
                prop.getProperty("selenium.grid.url")
        );
        edgeDriver.get(prop.getProperty("url").trim());

        try {
            // Perform operations in Edge browser here
            LoginPage edgeLoginPage = new LoginPage(edgeDriver);
            HomePage edgeHomePage = edgeLoginPage.enterCredentialsAndClickLoginButton(prop.getProperty("username2"), prop.getProperty("password2"));
            edgeHomePage.disableResponsiveSidebar();

            CashItemsPage edgeCashItemsPage = edgeHomePage.goToCashItems();
            edgeCashItemsPage.selectRecon(prop.getProperty("mc_recon_name"));

            GridPage edgeGridPage = new GridPage(edgeDriver);
            edgeGridPage.adjustZoom(15);
            edgeCashItemsPage.approve();
            edgeGridPage.adjustZoom(100);

            // Switch back to Chrome for validation
            driver = chromeDriver;
            cashItemsPage.refresh();

            List<String> statusListFromUi = cashItemsPage.getStatusBasedOnFileName("balanceMissingOrInconsistentWithStatus.xlsx");
//        System.out.println(statusListFromUi);
            boolean allIgnored = statusListFromUi.stream()
                    .allMatch(status -> status.equals("Ignored"));
            Assert.assertTrue(allIgnored, "Wrong status");
        } finally {
            // Close the Edge browser instance
            edgeDriver.quit();

        }

    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Right click actions with maker checker enabled")
    @Test(priority = 31, groups = "Cash Items", description = "6f")
    public void _6f() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/case7/7f_file1.xlsx");
        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/case7/7f_file2.xlsx");

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        WebDriver chromeDriver = driver;

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("mc_recon_name"),
                eventRuleHierarchiesPageDTO.getCase_7f()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("mc_recon_id")
        );
        // 4. Navigate to cash items and select recon & view
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("mc_recon_name"));

        // 5. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        gridPage = new GridPage(driver);
        gridPage.adjustZoom(15);

        String batchIdForFirstFailedRecord = cashItemsPage.getBatchIdForFirstFailedRecord();

        cashItemsPage.addItemToBatch();

        // Open a new Edge browser after existing operations
        WebDriver edgeDriver = DriverFactory.createStandaloneEdgeDriver(
                Boolean.parseBoolean(prop.getProperty("remote")),
                prop.getProperty("selenium.grid.url")
        );
        edgeDriver.get(prop.getProperty("url").trim());

        try {
            // Perform operations in Edge browser here
            LoginPage edgeLoginPage = new LoginPage(edgeDriver);
            HomePage edgeHomePage = edgeLoginPage.enterCredentialsAndClickLoginButton(prop.getProperty("username2"), prop.getProperty("password2"));
            edgeHomePage.disableResponsiveSidebar();

            CashItemsPage edgeCashItemsPage = edgeHomePage.goToCashItems();
            edgeCashItemsPage.selectRecon(prop.getProperty("mc_recon_name"));

            GridPage edgeGridPage = new GridPage(edgeDriver);
            edgeGridPage.adjustZoom(15);
            edgeCashItemsPage.approve();
            edgeGridPage.adjustZoom(100);

            // Switch back to Chrome for validation
            driver = chromeDriver;
            cashItemsPage.refresh();

            List<String> statusListFromUi = cashItemsPage.getStatusBasedOnBatchId(batchIdForFirstFailedRecord);
            boolean allValidated = statusListFromUi.stream()
                    .allMatch(status -> status.equals("Validated"));
            Assert.assertTrue(allValidated, "Wrong status");
        } finally {
            // Close the Edge browser instance
            edgeDriver.quit();
        }
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Right click actions with maker checker enabled")
    @Test(priority = 32, groups = "Cash Items", description = "6g")
    public void _6g() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/balanceMissingOrInconsistentWithStatus.xlsx");

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        WebDriver chromeDriver = driver;

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "db_cr", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("mc_recon_name"),
                eventRuleHierarchiesPageDTO.getB1_balanceMissingOrInconsistent()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("mc_recon_id")
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
        cashItemsPage.selectRecon(prop.getProperty("mc_recon_name"));

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
        softAssert.assertTrue(ListUtil.compare2DMaps(excelData, rawData));

        gridPage.adjustZoom(15);

        String batchIdForFirstFailedRecord = cashItemsPage.getBatchIdForFirstFailedRecord();

        cashItemsPage.updateBatchBalances(4500);

        // Open a new Edge browser after existing operations
        WebDriver edgeDriver = DriverFactory.createStandaloneEdgeDriver(
                Boolean.parseBoolean(prop.getProperty("remote")),
                prop.getProperty("selenium.grid.url")
        );
        edgeDriver.get(prop.getProperty("url").trim());

        try {
            // Perform operations in Edge browser here
            LoginPage edgeLoginPage = new LoginPage(edgeDriver);
            HomePage edgeHomePage = edgeLoginPage.enterCredentialsAndClickLoginButton(prop.getProperty("username2"), prop.getProperty("password2"));
            edgeHomePage.disableResponsiveSidebar();

            CashItemsPage edgeCashItemsPage = edgeHomePage.goToCashItems();
            edgeCashItemsPage.selectRecon(prop.getProperty("mc_recon_name"));

            GridPage edgeGridPage = new GridPage(edgeDriver);
            edgeGridPage.adjustZoom(15);
            edgeCashItemsPage.approve();
            edgeGridPage.adjustZoom(100);

            // Switch back to Chrome for validation
            driver = chromeDriver;
            cashItemsPage.refresh();

            List<String> statusListFromUi = cashItemsPage.getStatusBasedOnBatchId(batchIdForFirstFailedRecord);
            boolean allValidated = statusListFromUi.stream()
                    .allMatch(status -> status.equals("Validated"));
            Assert.assertTrue(allValidated, "Wrong status");
        } finally {
            // Close the Edge browser instance
            edgeDriver.quit();
        }
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Right click actions with maker checker enabled")
    @Test(priority = 33, groups = "Cash Items", description = "6h")
    public void _6h() throws Exception {

        FileAttachmentUtil.attachCsv("dataFiles/excelFiles/openingInconsistentWithLastClosingWithStatus1.csv");
        FileAttachmentUtil.attachCsv("dataFiles/excelFiles/openingInconsistentWithLastClosingWithStatus2.csv");
        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "db_cr", "amount", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        WebDriver chromeDriver = driver;

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("mc_recon_name"),
                eventRuleHierarchiesPageDTO.getB2_openingInconsistentWithLastClosing()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("mc_recon_id")
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
        cashItemsPage.selectRecon(prop.getProperty("mc_recon_name"));

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
        Assert.assertTrue(ListUtil.compare2DMaps(twoBatchesCombinedCsvData, rawData), "Data mismatch in Cash Items");

        gridPage.adjustZoom(15);

        String batchIdForFirstFailedRecord = cashItemsPage.getBatchIdForFirstFailedRecord();

        cashItemsPage.addBatch();

        // Open a new Edge browser after existing operations
        WebDriver edgeDriver = DriverFactory.createStandaloneEdgeDriver(
                Boolean.parseBoolean(prop.getProperty("remote")),
                prop.getProperty("selenium.grid.url")
        );
        edgeDriver.get(prop.getProperty("url").trim());

        try {
            // Perform operations in Edge browser here
            LoginPage edgeLoginPage = new LoginPage(edgeDriver);
            HomePage edgeHomePage = edgeLoginPage.enterCredentialsAndClickLoginButton(prop.getProperty("username2"), prop.getProperty("password2"));
            edgeHomePage.disableResponsiveSidebar();

            CashItemsPage edgeCashItemsPage = edgeHomePage.goToCashItems();
            edgeCashItemsPage.selectRecon(prop.getProperty("mc_recon_name"));

            GridPage edgeGridPage = new GridPage(edgeDriver);
            edgeGridPage.adjustZoom(15);
            edgeCashItemsPage.approve();
            edgeGridPage.adjustZoom(100);

            // Switch back to Chrome for validation
            driver = chromeDriver;
            cashItemsPage.reprocess();
//            cashItemsPage.refresh();

            List<String> statusListFromUi = cashItemsPage.getStatusBasedOnBatchId(batchIdForFirstFailedRecord);

//          // Debug print
//          System.out.println("\n");
//          System.out.println(statusListFromUi);

            boolean allValidated = statusListFromUi.stream()
                    .allMatch(status -> status.equals("Validated"));
            Assert.assertTrue(allValidated, "Wrong status");
        } finally {
            // Close the Edge browser instance
            edgeDriver.quit();
        }
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Right click actions with maker checker enabled")
    @Test(priority = 34, groups = "Cash Items", description = "6i")
    public void _6i() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/correctAutomationWithStatus.xlsx");

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "db_cr", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details");

        WebDriver chromeDriver = driver;

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("mc_recon_name"),
                eventRuleHierarchiesPageDTO.getCase_7i()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("mc_recon_id")
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
        cashItemsPage.selectRecon(prop.getProperty("mc_recon_name"));

        // 5. Check if Cash Items table is not empty
        Assert.assertTrue(cashItemsPage.isCashItemsDataPresent(), "Cash Items table is empty but event is completed");

        gridPage = new GridPage(driver);
        gridPage.adjustZoom(15);

        String batchIdForFirstFailedRecord = cashItemsPage.getBatchIdForFirstFailedRecord();

        cashItemsPage.approveDuplicates();

        WebDriver edgeDriver = DriverFactory.createStandaloneEdgeDriver(
                Boolean.parseBoolean(prop.getProperty("remote")),
                prop.getProperty("selenium.grid.url")
        );
        edgeDriver.get(prop.getProperty("url").trim());

        try {
            // Perform operations in Edge browser here
            LoginPage edgeLoginPage = new LoginPage(edgeDriver);
            HomePage edgeHomePage = edgeLoginPage.enterCredentialsAndClickLoginButton(prop.getProperty("username2"), prop.getProperty("password2"));
            edgeHomePage.disableResponsiveSidebar();

            CashItemsPage edgeCashItemsPage = edgeHomePage.goToCashItems();
            edgeCashItemsPage.selectRecon(prop.getProperty("mc_recon_name"));

            GridPage edgeGridPage = new GridPage(edgeDriver);
            edgeGridPage.adjustZoom(15);
            edgeCashItemsPage.approve();
            edgeGridPage.adjustZoom(100);

            // Switch back to Chrome for validation
            driver = chromeDriver;
            cashItemsPage.refresh();

            List<String> statusListFromUi = cashItemsPage.getStatusDetailsBasedOnBatchId(batchIdForFirstFailedRecord);
            boolean statusCheck = statusListFromUi.stream()
                    .allMatch(status -> status.equals("Duplicate Batch Identified"));
            Assert.assertFalse(statusCheck, "Wrong status");
        } finally {
            // Close the Edge browser instance
            edgeDriver.quit();
        }
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Check right click actions for certain failure messages")
    @Test(priority = 35, groups = "Cash Items", description = "8a")
    public void _8a() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/openingClosingInconsistentWithStatus.xlsx");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB1_openingClosingInconsistent()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 4. Navigate to cash items and select recon & view
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        cashItemsPage.rightClickFirstFailedRecord();

        //System.out.println(cashItemsPage.getRightClickActionsList());

        List<String> expectedRightClickOptions = List.of("Ignore Item", "Ignore Batch", "Ignore File", "Add Item to Batch", "Update Batch Balances");
        List<String> unexpectedRightClickOptions = List.of("Add Batch");
        List<String> actualRightClickOptions = List.of(cashItemsPage.getRightClickActionsList().get(0).split("\\R"));

//        System.out.println(expectedRightClickOptions);
//        System.out.println(actualRightClickOptions);

        softAssert.assertTrue(actualRightClickOptions.containsAll(expectedRightClickOptions), "Expected right click action not found");
        softAssert.assertTrue(!actualRightClickOptions.containsAll(unexpectedRightClickOptions), "Unexpected right click action found");
        softAssert.assertAll();

    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Check right click actions for certain failure messages")
    @Test(priority = 36, groups = "Cash Items", description = "8b")
    public void _8b() throws Exception {

        FileAttachmentUtil.attachCsv("dataFiles/csvFiles/openingClosingInconsistentWithStatus1.csv");
        FileAttachmentUtil.attachCsv("dataFiles/csvFiles/openingClosingInconsistentWithStatus2.csv");

        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB2_openingInconsistentWithLastClosing()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 4. Navigate to cash items and select recon & view
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        cashItemsPage.rightClickFirstFailedRecord();

        //System.out.println(cashItemsPage.getRightClickActionsList());

        List<String> expectedRightClickOptions = List.of("Ignore Item", "Ignore Batch", "Ignore File", "Add Batch", "Update Batch Balances");
        List<String> unexpectedRightClickOptions = List.of("Add Item to Batch");
        List<String> actualRightClickOptions = List.of(cashItemsPage.getRightClickActionsList().get(0).split("\\R"));

//        System.out.println(expectedRightClickOptions);
//        System.out.println(actualRightClickOptions);

        softAssert.assertTrue(actualRightClickOptions.containsAll(expectedRightClickOptions), "Expected right click action not found");
        softAssert.assertTrue(!actualRightClickOptions.containsAll(unexpectedRightClickOptions), "Unexpected right click action found");
        softAssert.assertAll();

    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Check right click actions for certain failure messages")
    @Test(priority = 37, groups = "Cash Items", description = "8c")
    public void _8c() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/balanceMissingOrInconsistentWithStatus.xlsx");

        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getB1_balanceMissingOrInconsistent()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 4. Navigate to cash items and select recon & view
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        cashItemsPage.rightClickFirstFailedRecord();

        //System.out.println(cashItemsPage.getRightClickActionsList());

        List<String> expectedRightClickOptions = List.of("Ignore Item", "Ignore Batch", "Ignore File", "Update Batch Balances");
        List<String> unexpectedRightClickOptions = List.of("Add Item to Batch", "Add batch");
        List<String> actualRightClickOptions = List.of(cashItemsPage.getRightClickActionsList().get(0).split("\\R"));

//        System.out.println(expectedRightClickOptions);
//        System.out.println(actualRightClickOptions);

        softAssert.assertTrue(actualRightClickOptions.containsAll(expectedRightClickOptions), "Expected right click action not found");
        softAssert.assertTrue(!actualRightClickOptions.containsAll(unexpectedRightClickOptions), "Unexpected right click action found");
        softAssert.assertAll();

    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Check right click actions for certain failure messages")
    @Test(priority = 38, groups = "Cash Items", description = "8d")
    public void _8d() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/case8/8d.xlsx");

        String setFundToNull = "update cr_accounts set fund=null where account='AUTO4';";

        queries dbQueries = new queries(dbUtil);
        dbQueries.executeUpdate(setFundToNull);

        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getCase_8d()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 4. Navigate to cash items and select recon & view
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        cashItemsPage.addItemToBatchWithoutWaits();
        String toastMessage = cashItemsPage.getToastMessage();

        String setFundValue = "update cr_accounts set fund='FG4' where account='AUTO4';";
        dbQueries.executeUpdate(setFundValue);

        Assert.assertTrue(toastMessage.contains("Add Item To Batch Failed"), "Add item to batch should not be allowed, but was allowed");
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Check right click actions for certain failure messages")
    @Test(priority = 39, groups = "Cash Items", description = "8e")
    public void _8e() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/case8/8e_1.xlsx");
        FileAttachmentUtil.attachExcel("dataFiles/case8/8e_2.xlsx");

        String setFundToNull = "update cr_accounts set fund=null where account='AUTO4';";

        queries dbQueries = new queries(dbUtil);
        dbQueries.executeUpdate(setFundToNull);

        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getCase_8e()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 4. Navigate to cash items and select recon & view
        cashItemsPage = homePage.goToCashItems();
        cashItemsPage.selectRecon(prop.getProperty("recon_name"));

        cashItemsPage.addBatchWithoutWaits();
        String toastMessage = cashItemsPage.getToastMessage();
        System.out.println(toastMessage);

        String setFundValue = "update cr_accounts set fund='FG4' where account='AUTO4';";
        dbQueries.executeUpdate(setFundValue);

        Assert.assertTrue(toastMessage.contains("Add Item To Batch Failed"), "Add item to batch should not be allowed, but was allowed");
    }
    /********************************************** Production Issues *************************************************/

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Items")
    @Story("Fund and fund group lookup after reprocess")
    @Test(priority = 40, groups = "Prod Issues", description = "PROD - Fund and fund group lookup after reprocess")
    public void _PROD1() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/prod1.xlsx");
        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/prod1_after.xlsx");

        /*
          Assumptions:
          1. Test data is properly imported to the source
             (This test case does not perform source vs test data validation)
        */

        // 0. Define the list of required columns required for validation
        List<String> requiredColumns = Arrays.asList("subaccount", "currency", "amount", "db_cr", "openingbalance",
                "openingbalance_dbcr", "closingbalance", "closingbalance_dbcr", "itemdate", "openingbalancedate",
                "closingbalancedate", "status", "status_details", "fund", "fundgroup");

        String changeCurrencyToAED = "update cr_accounts_map set currency='AED' where account='ACCSUB0884';";

        queries dbQueries = new queries(dbUtil);
        dbQueries.executeUpdate(changeCurrencyToAED);

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getProd1()
        );

        // 2. Backend verification (Event status validation)
        eventService.assertLatestEventCompleted(
                prop.getProperty("recon_id")
        );

        // 3. Read the required columns from the excel file
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/excelFiles/prod1.xlsx");

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
        softAssert.assertTrue(ListUtil.compare2DMaps(excelData, rawData), "Data mismatch before reprocess");

        String changeCurrencyToINR = "update cr_accounts_map set currency='INR' where account='ACCSUB0884';";
        dbQueries.executeUpdate(changeCurrencyToINR);

        cashItemsPage.waitFor(5);

        cashItemsPage.reprocess();

        InputStream is1 = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/excelFiles/prod1_after.xlsx");

        List<Map<String, String>> excelDataAfterReprocess =
                ExcelUtil.readExcelNormalizedWithRequiredHeaders(is1, "Sheet1", cashDashboardsColumnKeyMapping, requiredColumns);

//        gridPage = new GridPage(driver);
        List<Map<String, String>> rawDataAfterReprocess = gridPage.getGridRawData(requiredColumns);

//      Debug print
//        System.out.println("\n");
//        for (Map<String, String> row : rawData)
//            System.out.println(row);

        // 8. Compare Cash Items data with expected data
        softAssert.assertTrue(ListUtil.compare2DMaps(excelDataAfterReprocess, rawDataAfterReprocess), "Data mismatch after reprocess");
        softAssert.assertAll();
    }

    @Owner("QA")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Cash Recon")
    @Story("Transformation errors in API Import - Event & Citems statuses")
    @Test(priority = 41, groups = "Prod Issues", description = "PROD - Transformation errors in API Import - Event & citems statuses")
    public void _PROD2() throws Exception {

        FileAttachmentUtil.attachExcel("dataFiles/excelFiles/le_51_records.xlsx");

        List<String> requiredColumns = Arrays.asList("status", "status_details");

        // 1. Trigger import from Event Rule Hierarchies dashboard
        eventRuleHierarchiesPage = homePage.goToEventRuleHierarchies();

        eventRuleHierarchiesPage.selectReconAndEventAndTrigger(
                prop.getProperty("recon_name"),
                eventRuleHierarchiesPageDTO.getLe_import()
        );

//         2. Backend verification (Event status validation)
        try {
            eventService.assertLatestEventFailedOrCompletedWithError(
                    prop.getProperty("recon_id")
            );
        } catch (AssertionError e) {
            softAssert.fail(e.getMessage());
        }

        // 3. Read the required columns from the excel file
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("dataFiles/excelFiles/le_51_records.xlsx");

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
        softAssert.assertEquals(excelData, rawData, "Status mismatch");

        softAssert.assertAll();

    }

}