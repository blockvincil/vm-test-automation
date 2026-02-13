package com.qa.blocrecon.pages;

import com.qa.blocrecon.constants.AppConstants;
import com.qa.blocrecon.utils.ElementsUtil;
import com.qa.blocrecon.utils.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class HomePage {
    WebDriver driver;
    JavascriptExecutor js;
    ElementsUtil eleUtil;
    WaitUtil waitUtil;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
        this.eleUtil = new ElementsUtil(driver);
        this.waitUtil = new WaitUtil(driver);
    }

    /*****************************************************Static Locators**************************************************/

    private final By settingDropdown = By.id("settingDropdown");
    private final By responsiveSidebarSlider = By.xpath("//div[text()='Responsive Sidebar']/..//span");
    private final By processSetup = By.xpath("//span[text()='Process Setup']/..");
    private final By explorers = By.xpath("//span[text()='Explorers']/..");
    private final By cashRecon = By.xpath("(//span[text()='Cash Recon']/..)[1]");
    private final By eventRuleHierarchies = By.xpath("//span[text()='Event Rule Hierarchies']/..");
    private final By sourceExplorer = By.xpath("//span[text()='Source Explorer']/..");
    private final By cashItems = By.xpath("//span[text()='Cash Items']/..");
    private final By cashBalances = By.xpath("//span[text()='Cash Balances']/..");
    private final By eventRuleHierarchiesList = By.xpath("//ul[@class='flex-nowrap px-0 nav flex-column nav-tabs']");
    private final By cashItemsDropdown = By.xpath("//select[@name='explorerName']");
    private final By selectSourceDropdown = By.tagName("select");
    private final By cashBalancesDropdown = By.xpath("//select[@name='explorerName']");


    private void clickResponsiveSidebarSlider() {
        eleUtil.doClick(responsiveSidebarSlider);
    }

    private void doClickSettingsDropdown() {
        eleUtil.doClick(settingDropdown, AppConstants.time10);
    }

    public void clickProcessSetup() {
        waitUtil.waitForElementToBeClickable(processSetup, AppConstants.time10).click();
        waitUtil.waitFor(2);
    }

    public void clickExplorers() {
        try {
            waitUtil.waitForElementToBeClickable(explorers, AppConstants.time3).click();
        } catch (Exception e) {
            clickProcessSetup();
            waitUtil.waitForElementToBeClickable(explorers, AppConstants.time10).click();
        }
        waitUtil.waitFor(2);
    }

    private void clickCashRecon() {
        try {
            waitUtil.waitForElementToBeClickable(cashRecon, AppConstants.time10).click();
        } catch (Exception e) {
            clickProcessSetup();
            waitUtil.waitForElementToBeClickable(cashRecon, AppConstants.time10).click();
        }
    }

    private void clickCashItems() {
        waitUtil.waitForElementToBeClickable(cashItems, AppConstants.time10).click();
        waitUtil.waitForElementToBeClickable(cashItemsDropdown, AppConstants.time20);
    }

    private void clickEventRuleHierarchies() {
        waitUtil.waitForElementToBeClickable(eventRuleHierarchies, AppConstants.time10).click();
        waitUtil.waitForElementVisible(eventRuleHierarchiesList, AppConstants.time10);
    }

    private void clickSourceExplorer() {
        waitUtil.waitForElementToBeClickable(sourceExplorer, AppConstants.time10).click();
        waitUtil.waitForElementVisible(selectSourceDropdown, AppConstants.time20);
    }

    private void clickCashBalances() {
        waitUtil.waitForElementToBeClickable(cashBalances, AppConstants.time10).click();
        waitUtil.waitForElementVisible(cashBalancesDropdown, AppConstants.time20);
    }

    /**
     Navigates to the Event Rule Hierarchies page.
     @return An instance of the EventRuleHierarchiesPage.
     @throws InterruptedException If the thread is interrupted during the wait.
     */
    public EventRuleHierarchiesPage goToEventRuleHierarchies() throws InterruptedException {
        clickProcessSetup();
        waitUtil.waitFor(1);
        clickEventRuleHierarchies();
        waitUtil.waitFor(1);
        // clickProcessSetup();
        return new EventRuleHierarchiesPage(driver);
    }

    /**
     Navigates to the Source Explorer page.
     @return An instance of the SourceExplorerPage.
     @throws InterruptedException If the thread is interrupted during the wait.
     */
    public SourceExplorerPage goToSourceExplorer() throws InterruptedException {
        clickExplorers();
        waitUtil.waitFor(1);
        clickSourceExplorer();
        waitUtil.waitFor(1);
        return new SourceExplorerPage(driver);
    }

    public CashBalancesPage goToCashBalances() {
        try {
            waitUtil.waitFor(1);
            clickCashBalances();
        } catch (Exception e) {
            clickCashRecon();
            waitUtil.waitFor(1);
            clickCashBalances();
        }

        return new CashBalancesPage(driver);
    }

    /**
     Navigates to the Cash Items page.
     @return An instance of the CashItemsPage.
     */
    public CashItemsPage goToCashItems() {
        clickCashRecon();
        waitUtil.waitFor(1);
        clickCashItems();
        waitUtil.waitFor(1);
        return new CashItemsPage(driver);
    }

    /**
     Disables the responsive sidebar.
     */
    public void disableResponsiveSidebar() {
        doClickSettingsDropdown();
        clickResponsiveSidebarSlider();
        doClickSettingsDropdown();
        waitUtil.waitForElementToBeClickable(processSetup, AppConstants.time10);
    }
}
