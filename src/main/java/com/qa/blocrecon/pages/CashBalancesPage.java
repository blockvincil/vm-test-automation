package com.qa.blocrecon.pages;

import com.qa.blocrecon.constants.AppConstants;
import com.qa.blocrecon.utils.ElementsUtil;
import com.qa.blocrecon.utils.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class CashBalancesPage {
    WebDriver driver;
    JavascriptExecutor js;
    ElementsUtil eleUtil;
    WaitUtil waitUtil;

    public CashBalancesPage(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
        this.eleUtil = new ElementsUtil(driver);
        this.waitUtil = new WaitUtil(driver);
    }


    /**************************************************** Static Locators *************************************************/

    private final By pageLoader = By.xpath("//div[contains(@class, 'spinner')]");
    private final By cashBalancesDataLoader = By.xpath("//div[contains(@class, 'ag-row-loading')]");
    private final By cashBalancesDropdown = By.xpath("//select[@name='explorerName']");
    private final By cashBalancesDataList = By.xpath("//div[@role='treegrid']");
    private final By cashBalancesDataRows = By.xpath("//div[@class='ag-center-cols-container']//div[@role='row']");

    /******************************************************** Methods *****************************************************/


    public void selectRecon(String reconName) {
        eleUtil.doSelectByVisibleText(cashBalancesDropdown, reconName);
        eleUtil.waitForElementToDisappear(pageLoader, AppConstants.time3, AppConstants.time10);
        eleUtil.waitForElementToDisappear(cashBalancesDataLoader, AppConstants.time3, AppConstants.time10);
        waitUtil.waitForElementVisible(cashBalancesDataList, AppConstants.time10);
    }

    public boolean isCashBalancesDataPresent() {
        // eleUtil.waitForElementToDisappear(sourceDataLoader, AppConstants.time10);
        return eleUtil.getElementsCount(cashBalancesDataRows) > 0;
    }

}