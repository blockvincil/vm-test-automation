package com.qa.blocrecon.pages;

import com.qa.blocrecon.constants.AppConstants;
import com.qa.blocrecon.utils.ElementsUtil;
import com.qa.blocrecon.utils.WaitUtil;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class SignOffPage {
    WebDriver driver;
    JavascriptExecutor js;
    ElementsUtil eleUtil;
    WaitUtil waitUtil;

    public SignOffPage(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
        this.eleUtil = new ElementsUtil(driver);
        this.waitUtil = new WaitUtil(driver);
    }

    private final By dateFilter = By.xpath("//select[@name='dateFilter']");
    private final By signOffDataLoader = By.xpath("//div[contains(@class, 'ag-row-loading')]");
    private final By signOffDataRows = By.xpath("//div[@class='ag-center-cols-container']//div[@role='row']");


    public void selectDateFilter(String dateFilterName) {
        Allure.step("Select date filter in Sign Off dashboard");
        eleUtil.doSelectByVisibleText(dateFilter, dateFilterName);
        eleUtil.waitForElementToDisappear(signOffDataLoader, AppConstants.time3, AppConstants.time10);
        waitUtil.waitFor(1);
    }

    public boolean signOffDataPresent() {
        Allure.step("Check if some data is present in the Sign Off dashboard");
        // eleUtil.waitForElementToDisappear(sourceDataLoader, AppConstants.time10);
        return eleUtil.getElementsCount(signOffDataRows) > 0;
    }


}