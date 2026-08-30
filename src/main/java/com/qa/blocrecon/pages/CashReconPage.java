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

import java.util.Random;

public class CashReconPage {
    WebDriver driver;
    JavascriptExecutor js;
    ElementsUtil eleUtil;
    WaitUtil waitUtil;

    public CashReconPage(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
        this.eleUtil = new ElementsUtil(driver);
        this.waitUtil = new WaitUtil(driver);
    }

    /************************************************ Static Locators *************************************************/

    private final By pageLoader = By.xpath("//div[contains(@class, 'spinner')]");
    private final By toastMessage = By.xpath("//div[@class='notification-message']");
    private final By rightClickActionsList = By.xpath("(//div[@role='tree'])[last()]");
    private final By cashReconDropdown = By.xpath("//select[@name='explorerName']");
    private final By cashReconDataLoader = By.xpath("//div[contains(@class, 'ag-row-loading')]");
    private final By cashItemsDataRows = By.xpath("//div[@class='ag-center-cols-container']//div[@role='row']");
    private final By matchStatusFilterBox = By.xpath("//input[@aria-label='MATCH STATUS Filter Input']");
    private final By assetIdFilterBox = By.xpath("//input[@aria-label='ASSET ID Filter Input']");
    private final By checkboxes = By.xpath("//div[@ref='eCheckbox']//input");
    private final By groupItemsButton = By.xpath("//span[text()='Group Items']");
    private final By viewOrRemoveGroupButton = By.xpath("//span[text()='View/Remove Group']");
    private final By submitButton = By.xpath("//button[text()='Submit']");
    private final By updateGroupButton = By.xpath("//button[text()='Update Group']");
    private final By firstCheckedRecord = By.xpath("(//div[@aria-selected='true'])[1]");
    private final By firstGroupedRecord = By.xpath("//*[contains(text(),'|group')]");
    private final By removeButtons = By.xpath("//button[text()='Remove']");
    private final By refreshButton = By.xpath("//button[@title='refresh']");

    /************************************************** Dynamic Locators **********************************************/

    /****************************************************** Methods ***************************************************/

    public void selectRecon(String reconName) {
        Allure.step("Select recon on Cash Items dashboard");
        eleUtil.doSelectByVisibleText(cashReconDropdown, reconName);
        eleUtil.waitForElementToDisappear(pageLoader, AppConstants.time3, AppConstants.time10);
        eleUtil.waitForElementToDisappear(cashReconDataLoader, AppConstants.time3, AppConstants.time10);
        waitUtil.waitFor(3);
    }

    public boolean isCashReconDataPresent() {
        Allure.step("Check if some data is present in Cash Items dashboard");
        // eleUtil.waitForElementToDisappear(cashItemsDataLoader, AppConstants.time10);
        return eleUtil.getElementsCount(cashItemsDataRows) > 0;
    }

    private int filterByMatchStatus(String status) {
        Allure.step("Filter by match status='UNMATCHED'");
        eleUtil.doSendKeys(matchStatusFilterBox, status, AppConstants.time5);
        waitUtil.waitFor(3);
        return eleUtil.getElementsCount(cashItemsDataRows);
    }

    public int filterByAssetId(String assetId) {
        Allure.step("Filter by Asset Id contains " + assetId);
        eleUtil.doSendKeys(assetIdFilterBox, assetId, AppConstants.time5);
        waitUtil.waitFor(3);
        return eleUtil.getElementsCount(cashItemsDataRows);
    }

    public void checkMultipleCheckboxes(int count) {
        Allure.step("Check " + count + " checkboxes");
        var checkboxElements = eleUtil.getElements(checkboxes);
        for (int i = 0; i < count && i < checkboxElements.size(); i++) {
            checkboxElements.get(i).click();
        }
    }

    private void removeRecordsFromGroup(int count) {
        Allure.step("Click " + count + " remove buttons with 1 second gap");
        var removeButtonElements = eleUtil.getElements(removeButtons);
        int buttonsToClick = Math.min(count, removeButtonElements.size());
        for (int i = 0; i < buttonsToClick; i++) {
            removeButtonElements.get(i).click();
            waitUtil.waitFor(1);
        }
    }

    private String getToastMessage() {
        Allure.step("Capture toast message");
        System.out.println("Searching for toast message");
        return eleUtil.doGetText(toastMessage, AppConstants.time10);
    }

    private void rightClick(By locator) {
        Allure.step("Right click based on locator");
        WebElement element = driver.findElement(locator);
        Actions actions = new Actions(driver);
        actions.contextClick(element).perform();
    }

    public String groupItems() {
        Allure.step("Group items");
        int unmatchedRecordsCount = filterByMatchStatus("UNMATCHED");

        if (unmatchedRecordsCount == 1) {
            Allure.step("Grouping not possible - only 1 unmatched record");
            return "Grouping not possible - only 1 unmatched record";
        }

        int checkboxesToCheck;
        if (unmatchedRecordsCount == 2) {
            checkboxesToCheck = 2;
        } else {
            checkboxesToCheck = unmatchedRecordsCount - 1;
        }



        checkMultipleCheckboxes(checkboxesToCheck);
        rightClick(firstCheckedRecord);
        eleUtil.doClick(groupItemsButton);
        eleUtil.doClick(submitButton, AppConstants.time5);
//        eleUtil.waitForElementToDisappear(pageLoader, AppConstants.time3, AppConstants.time10);
//        System.out.println("Page loader disappeared");
//        eleUtil.waitForElementToDisappear(cashReconDataLoader, AppConstants.time3, AppConstants.time10);
//        System.out.println("Cash recon data loader disappeared");
        waitUtil.waitFor(1);
        return getToastMessage();
    }

    public String viewOrRemoveGroup() {
        Allure.step("Removing all records from group view");
        waitUtil.waitFor(2);
        rightClick(firstGroupedRecord);
        eleUtil.doClick(viewOrRemoveGroupButton);
        removeRecordsFromGroup(eleUtil.getElements(removeButtons).size());
        try {
            eleUtil.doClick(updateGroupButton, AppConstants.time5);
        } catch (Exception e) {
            return "Application Crash Detected!";
        }
//        eleUtil.waitForElementToDisappear(pageLoader, AppConstants.time3, AppConstants.time10);
//        System.out.println("Page loader disappeared");
//        eleUtil.waitForElementToDisappear(cashReconDataLoader, AppConstants.time3, AppConstants.time10);
//        System.out.println("Cash recon data loader disappeared");
        waitUtil.waitFor(1);
        return getToastMessage();
    }

    public void refresh() {
        Allure.step("Manual refresh on Cash Recon page");
        eleUtil.doClick(refreshButton, AppConstants.time10);
        eleUtil.waitForElementToDisappear(pageLoader, AppConstants.time3, AppConstants.time10);
        eleUtil.waitForElementToDisappear(cashReconDataLoader, AppConstants.time3, AppConstants.time10);
        waitUtil.waitFor(2);

    }

}
