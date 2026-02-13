package com.qa.blocrecon.pages;

import com.qa.blocrecon.constants.AppConstants;
import com.qa.blocrecon.utils.ElementsUtil;
import com.qa.blocrecon.utils.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.*;

public class SourceExplorerPage {
    WebDriver driver;
    JavascriptExecutor js;
    ElementsUtil eleUtil;
    WaitUtil waitUtil;

    public SourceExplorerPage(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
        this.eleUtil = new ElementsUtil(driver);
        this.waitUtil = new WaitUtil(driver);
    }

    /*****************************************************Static Locators**************************************************/

    private final By pageLoader = By.xpath("//div[contains(@class, 'spinner')]");
    private final By selectSourceDropdown = By.tagName("select");
    private final By sourceDataLoader = By.xpath("//div[contains(@class, 'ag-row-loading')]");
    private final By sourceDataList = By.xpath("//div[@class='ag-center-cols-clipper']");
    private final By sourceDataRows = By.xpath("//div[@class='ag-center-cols-container']//div[@role='row']");

    /*****************************************************Dynamic Locators*************************************************/

    private By sourceDataRow(int rowNumber) {
        return By.xpath("(//div[@class='ag-center-cols-container']//div[@role='row'])[" + rowNumber + "]");
    }

    /******************************************************** Methods *****************************************************/

    /**
     * Selects a source from the dropdown and waits for the data to load.
     * @param sourceName The name of the source to select.
     */
    public void selectSource(String sourceName) {
        eleUtil.doSelectByVisibleText(selectSourceDropdown, sourceName);
        eleUtil.waitForElementToDisappear(pageLoader, AppConstants.time3, AppConstants.time10);
        eleUtil.waitForElementToDisappear(sourceDataLoader, AppConstants.time3, AppConstants.time10);
        waitUtil.waitForElementVisible(sourceDataList, AppConstants.time10);
        waitUtil.waitFor(1);
    }

    /**
     * Checks if the source table is not empty.
     *
     * @return true if the table is not empty, false otherwise.
     */
    public boolean isSourcesDataPresent() {
        // eleUtil.waitForElementToDisappear(sourceDataLoader, AppConstants.time10);
        return eleUtil.getElementsCount(sourceDataRows) > 0;
    }
}
