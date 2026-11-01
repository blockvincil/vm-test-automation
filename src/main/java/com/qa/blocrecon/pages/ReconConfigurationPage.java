package com.qa.blocrecon.pages;

import com.qa.blocrecon.constants.AppConstants;
import com.qa.blocrecon.utils.ElementsUtil;
import com.qa.blocrecon.utils.WaitUtil;
import io.qameta.allure.Allure;
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

    private By searchResult(String searchTerm) {
        return By.xpath("//a[@title = '" + searchTerm + "']");
    }

    private void searchAndSelectRecon(String reconName) {
        waitUtil.waitForElementVisible(reconList, AppConstants.time10);
        eleUtil.doSendKeys(reconSearchBar, reconName, AppConstants.time3);
        eleUtil.doClick(searchResult(reconName), AppConstants.time10);
        waitUtil.waitForElementsVisible(reconFieldsListGrid, AppConstants.time10);
    }
}
