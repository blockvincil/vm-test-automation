package com.qa.blocrecon.pages;

import com.qa.blocrecon.constants.AppConstants;
import com.qa.blocrecon.utils.ElementsUtil;
import com.qa.blocrecon.utils.WaitUtil;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

public class CashItemsPage {
    WebDriver driver;
    JavascriptExecutor js;
    ElementsUtil eleUtil;
    WaitUtil waitUtil;

    public CashItemsPage(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
        this.eleUtil = new ElementsUtil(driver);
        this.waitUtil = new WaitUtil(driver);
    }

    /**************************************************** Static Locators *************************************************/

    private final By pageLoader = By.xpath("//div[contains(@class, 'spinner')]");
    private final By toastMessage = By.xpath("//div[@class='notification-message']");
    private final By rightClickActionsList = By.xpath("(//div[@role='tree'])[last()]");
    private final By cashItemsDropdown = By.xpath("//select[@name='explorerName']");
    private final By cashItemsDataLoader = By.xpath("//div[contains(@class, 'ag-row-loading')]");
    private final By cashItemsDataRows = By.xpath("//div[@class='ag-center-cols-container']//div[@role='row']");
    private final By assetIdForFirstFailedRecord = By.xpath("(//*[text()='Failed'])[1]/../div[@col-id='asset_id']");
    private final By batchIdForFirstFailedRecord = By.xpath("(//*[text()='Failed'])[1]/../div[@col-id='batch_id']");
    private final By firstFailedRecord = By.xpath("//*[text()='Failed']");
    private final By firstPendingRecord = By.xpath("//*[text()='Pending']");
    private final By ignoreItemButton = By.xpath("//span[text()='Ignore Item']");
    private final By approveOrRejectButton = By.xpath("//span[text()='Approve/Reject']");
    private final By approveButton = By.xpath("//button[text()='Approve']");
    private final By ignoreBatchButton = By.xpath("//span[text()='Ignore Batch']");
    private final By ignoreFileButton = By.xpath("//span[text()='Ignore File']");
    private final By addBatchButton = By.xpath("//span[text()='Add Batch']");
    private final By addItemToBatchButton = By.xpath("//span[text()='Add Item to Batch']");
    private final By updateBatchBalancesButton = By.xpath("//span[text()='Update Batch Balances']");
    private final By approveDuplicatesButton = By.xpath("//span[text()='Approve Duplicates']");
    private final By updateBatchBalancesDialogBox_clisingBalanceTextBox = By.xpath("//input[@name='closingbalance']");
    private final By submitButton = By.xpath("//button[text()='Submit']");
    private final By reprocessButton = By.xpath("//button[@title='reprocess']");
    private final By refreshButton = By.xpath("//button[@title='refresh']");

    /*****************************************************Dynamic Locators*************************************************/

    private By returnStatusBasedOnAssetId(String assetId) {
        return By.xpath("//*[text()='"+assetId+"']/../div[@col-id='status']");
    }

    private By returnStatusBasedOnBatchId(String batchId) {
        return By.xpath("//*[text()='"+batchId+"']/../div[@col-id='status']");
    }

    private By returnStatusDetailsBasedOnBatchId(String batchId) {
        return By.xpath("//*[text()='"+batchId+"']/../div[@col-id='status_details']");
    }

    private By returnStatusBasedOnFileName(String fileName) {
        return By.xpath("//*[text()='"+fileName+"']/../div[@col-id='status']");
    }

    /******************************************************** Methods *****************************************************/


    public void reprocess() {
        eleUtil.doClick(reprocessButton, AppConstants.time10);
        eleUtil.waitForElementToDisappear(pageLoader, AppConstants.time3, AppConstants.time10);
        eleUtil.waitForElementToDisappear(cashItemsDataLoader, AppConstants.time3, AppConstants.time10);
        waitUtil.waitFor(3);

    }

    public List<String> getRightClickActionsList() {
        return eleUtil.getTextAsList(rightClickActionsList);
    }

    public void refresh() {
            eleUtil.doClick(refreshButton, AppConstants.time10);
            eleUtil.waitForElementToDisappear(pageLoader, AppConstants.time3, AppConstants.time10);
            eleUtil.waitForElementToDisappear(cashItemsDataLoader, AppConstants.time3, AppConstants.time10);
            waitUtil.waitFor(1);

    }
    /**
     * Selects a reconciliation from the dropdown.
     *
     * @param reconName The name of the reconciliation to select.
     */
    public void selectRecon(String reconName) {
        Allure.step("Select recon on Cash Items dashboard");
        eleUtil.doSelectByVisibleText(cashItemsDropdown, reconName);
        eleUtil.waitForElementToDisappear(pageLoader, AppConstants.time3, AppConstants.time10);
        eleUtil.waitForElementToDisappear(cashItemsDataLoader, AppConstants.time3, AppConstants.time10);
        waitUtil.waitFor(1);
    }

    /**
     * Checks if the Cash Items table is not empty.
     *
     * @return true if the table is not empty, false otherwise.
     */
    public boolean isCashItemsDataPresent() {
        Allure.step("Check if some data is present in Cash Items dashboard");
        // eleUtil.waitForElementToDisappear(cashItemsDataLoader, AppConstants.time10);
        return eleUtil.getElementsCount(cashItemsDataRows) > 0;
    }

    public String getAssetIdForFirstFailedRecord() {
        return eleUtil.doGetText(assetIdForFirstFailedRecord, AppConstants.time5);
    }

    public String getBatchIdForFirstFailedRecord() {
        return eleUtil.doGetText(batchIdForFirstFailedRecord, AppConstants.time5);
    }

    public void rightClick(By locator) {
        WebElement element = driver.findElement(locator);
        Actions actions = new Actions(driver);
        actions.contextClick(element).perform();
    }

    public void rightClickFirstFailedRecord() {
        WebElement element = driver.findElement(firstFailedRecord);
        Actions actions = new Actions(driver);
        actions.contextClick(element).perform();
    }

    public void ignoreItem() {
        Allure.step("Ignore item");
        rightClick(firstFailedRecord);
        eleUtil.doClick(ignoreItemButton);
        eleUtil.waitForElementToDisappear(pageLoader, AppConstants.time3, AppConstants.time10);
        eleUtil.waitForElementToDisappear(cashItemsDataLoader, AppConstants.time3, AppConstants.time10);
        waitUtil.waitFor(3);
//        System.out.println("Ignore item done");
    }

    public void approve() {
        Allure.step("Approve");
        rightClick(firstPendingRecord);
        eleUtil.doClick(approveOrRejectButton);
        eleUtil.doClick(approveButton, AppConstants.time10);
        eleUtil.waitForElementToDisappear(pageLoader, AppConstants.time3, AppConstants.time10);
        eleUtil.waitForElementToDisappear(cashItemsDataLoader, AppConstants.time3, AppConstants.time10);
        waitUtil.waitFor(1);
//        System.out.println("Ignore item done");
    }

    public void ignoreBatch() {
        Allure.step("Ignore batch");
        rightClick(firstFailedRecord);
        eleUtil.doClick(ignoreBatchButton);
        eleUtil.waitForElementToDisappear(pageLoader, AppConstants.time3, AppConstants.time10);
        eleUtil.waitForElementToDisappear(cashItemsDataLoader, AppConstants.time3, AppConstants.time10);
        waitUtil.waitFor(1);
    }

    public void ignoreFile() {
        Allure.step("Ignore file");
        rightClick(firstFailedRecord);
        eleUtil.doClick(ignoreFileButton);
        eleUtil.waitForElementToDisappear(pageLoader, AppConstants.time3, AppConstants.time10);
        eleUtil.waitForElementToDisappear(cashItemsDataLoader, AppConstants.time3, AppConstants.time10);
        waitUtil.waitFor(1);
    }

    public void addBatchAndReprocess() {
        Allure.step("Add batch and reprocess");
        rightClick(firstFailedRecord);
        eleUtil.doClick(addBatchButton);
        eleUtil.doClick(submitButton, AppConstants.time5);
        eleUtil.waitForElementToDisappear(pageLoader, AppConstants.time3, AppConstants.time10);
        eleUtil.waitForElementToDisappear(cashItemsDataLoader, AppConstants.time3, AppConstants.time10);
        waitUtil.waitFor(1);
        reprocess();
    }

    public void addBatch() {
        Allure.step("Add batch and reprocess");
        rightClick(firstFailedRecord);
        eleUtil.doClick(addBatchButton);
        eleUtil.doClick(submitButton, AppConstants.time5);
        eleUtil.waitForElementToDisappear(pageLoader, AppConstants.time3, AppConstants.time10);
        eleUtil.waitForElementToDisappear(cashItemsDataLoader, AppConstants.time3, AppConstants.time10);
        waitUtil.waitFor(1);
    }

    public String getToastMessage() {
        return eleUtil.doGetText(toastMessage, AppConstants.time3);
    }

    public void addItemToBatch() {
        Allure.step("Add batch and reprocess");
        rightClick(firstFailedRecord);
        eleUtil.doClick(addItemToBatchButton);
        eleUtil.doClick(submitButton, AppConstants.time5);
        eleUtil.waitForElementToDisappear(pageLoader, AppConstants.time3, AppConstants.time10);
        eleUtil.waitForElementToDisappear(cashItemsDataLoader, AppConstants.time3, AppConstants.time10);
        waitUtil.waitFor(1);
    }

    public void updateBatchBalances(int amount) {
        Allure.step("Update batch balances");
        rightClick(firstFailedRecord);
        eleUtil.doClick(updateBatchBalancesButton);
        eleUtil.doSendKeys(updateBatchBalancesDialogBox_clisingBalanceTextBox, String.valueOf(amount), AppConstants.time3);
        eleUtil.doClick(submitButton);
        eleUtil.waitForElementToDisappear(pageLoader, AppConstants.time3, AppConstants.time10);
        eleUtil.waitForElementToDisappear(cashItemsDataLoader, AppConstants.time3, AppConstants.time10);
        waitUtil.waitFor(3);
    }

    public void approveDuplicates() {
        Allure.step("Approve Duplicates");
        rightClick(firstFailedRecord);
        eleUtil.doClick(approveDuplicatesButton);
        eleUtil.waitForElementToDisappear(pageLoader, AppConstants.time3, AppConstants.time10);
        eleUtil.waitForElementToDisappear(cashItemsDataLoader, AppConstants.time3, AppConstants.time10);
        waitUtil.waitFor(1);

    }

    public String getStatusBasedOnAssetId(String assetId) {
        return eleUtil.doGetText(returnStatusBasedOnAssetId(assetId), AppConstants.time10);
    }

    public List<String> getStatusBasedOnBatchId(String batchId) {
        return eleUtil.getTextAsList(returnStatusBasedOnBatchId(batchId));
    }

    public List<String> getStatusDetailsBasedOnBatchId(String batchId) {
        return eleUtil.getTextAsList(returnStatusBasedOnBatchId(batchId));
    }

    public List<String> getStatusBasedOnFileName(String fileName) {
        return eleUtil.getTextAsList(returnStatusBasedOnFileName(fileName));
    }

}
