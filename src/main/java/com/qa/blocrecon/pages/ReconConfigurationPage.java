package com.qa.blocrecon.pages;

import com.qa.blocrecon.constants.AppConstants;
import com.qa.blocrecon.utils.ElementsUtil;
import com.qa.blocrecon.utils.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class ReconConfigurationPage {

    WebDriver driver;
    JavascriptExecutor js;
    ElementsUtil eleUtil;
    WaitUtil waitUtil;

    public ReconConfigurationPage(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
        this.eleUtil = new ElementsUtil(driver);
        this.waitUtil = new WaitUtil(driver);
    }

    private final By reconList = By.xpath("//ul[@class='flex-nowrap px-0 nav flex-column nav-tabs']");
    private final By reconSearchBar = By.xpath("//input[@placeholder='Search...']");
    private final By reconFieldsListGrid = By.xpath("//div[@grid-id=5]");
    private final By addField = By.xpath("//div[.//button[contains(.,'Add')]]/button");
    private final By fieldNameTextBox = By.xpath("//label[text()='Field Name']/../input");
    private final By saveNewField = By.xpath("//button[text()='Cancel']/../button[@type='submit']");
    private final By makerActionNeededToast = By.xpath("//div[normalize-space()='Maker action added']");
    private final By pendingApprovalsTab = By.xpath("//span[text()='Pending Approvals']/..");
    private final By reviewButton = By.xpath("//button[@title='Review']");
    private final By approveButton = By.xpath("//button[text()='Approve']");
    private final By approvedSuccessfullyToast = By.xpath("//div[contains(.,'Status Updated Successfully')]");

    private By searchResult(String searchTerm) {
        return By.xpath("//a[@title = '" + searchTerm + "']");
    }

    private void searchAndSelectRecon(String reconName) {
        waitUtil.waitForElementVisible(reconList, AppConstants.time10);
        eleUtil.doSendKeys(reconSearchBar, reconName, AppConstants.time3);
        eleUtil.doClick(searchResult(reconName), AppConstants.time10);
        waitUtil.waitFor(5);
    }

    private void createNewField(String fieldName) {
        eleUtil.doClick(addField, AppConstants.time10);
        eleUtil.doSendKeys(fieldNameTextBox, fieldName, AppConstants.time3);
        eleUtil.doClick(saveNewField, AppConstants.time10);
        waitUtil.waitForElementVisible(makerActionNeededToast, AppConstants.time10);
    }

    public void createNewFieldForRecon(String reconName, String fieldName) {
        searchAndSelectRecon(reconName);
        createNewField(fieldName);
    }

    private void approveLatestPendingApproval() {
        eleUtil.doClick(pendingApprovalsTab, AppConstants.time10);
        eleUtil.doClick(reviewButton, AppConstants.time10);
        eleUtil.doClick(approveButton, AppConstants.time10);
        waitUtil.waitForElementVisible(approvedSuccessfullyToast, AppConstants.time10);
    }

    public void selectReconAndApproveChanges(String reconName) {
        searchAndSelectRecon(reconName);
        approveLatestPendingApproval();
    }
}
