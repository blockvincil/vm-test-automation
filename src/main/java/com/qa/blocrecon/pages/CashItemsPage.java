package com.qa.blocrecon.pages;

import com.qa.blocrecon.constants.AppConstants;
import com.qa.blocrecon.utils.ElementsUtil;
import com.qa.blocrecon.utils.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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
    private final By cashItemsDropdown = By.xpath("//select[@name='explorerName']");
    private final By cashItemsDataLoader = By.xpath("//div[contains(@class, 'ag-row-loading')]");
    private final By cashItemsDataRows = By.xpath("//div[@class='ag-center-cols-container']//div[@role='row']");

    /******************************************************** Methods *****************************************************/

    /**
     * Selects a reconciliation from the dropdown.
     *
     * @param reconName The name of the reconciliation to select.
     */
    public void selectRecon(String reconName) {
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
        // eleUtil.waitForElementToDisappear(cashItemsDataLoader, AppConstants.time10);
        return eleUtil.getElementsCount(cashItemsDataRows) > 0;
    }

}
