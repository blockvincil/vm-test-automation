package com.qa.blocrecon.pages;

import com.qa.blocrecon.constants.AppConstants;
import com.qa.blocrecon.utils.ElementsUtil;
import com.qa.blocrecon.utils.WaitUtil;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class ReconSourceFieldMapPage {

    WebDriver driver;
    JavascriptExecutor js;
    ElementsUtil eleUtil;
    WaitUtil waitUtil;

    public ReconSourceFieldMapPage(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
        this.eleUtil = new ElementsUtil(driver);
        this.waitUtil = new WaitUtil(driver);
    }

    private final By reconList = By.xpath("//ul[@class='flex-nowrap px-0 nav flex-column nav-tabs']");
    private final By reconSearchBar = By.xpath("//input[@placeholder='Search...']");
    private final By sourcesList = By.xpath("//span[@title='Sources']/ancestor::ul");
    private final By sourcesSearchBar = By.xpath("//*[text()='Sources']/following::input[@placeholder='Search...'][1]");
    private final By mappingsGrid = By.xpath("//div[@role='treegrid']");

    private By reconFieldInMapping(String fieldName) {
        return By.xpath("//div[@col-id='display_name' and text()='" + fieldName + "']");
    }

    private By searchResult(String searchTerm) {
        return By.xpath("//a[@title = '" + searchTerm + "']");
    }

    private void searchAndSelectRecon(String reconName) {
        waitUtil.waitForElementVisible(reconList, AppConstants.time10);
        eleUtil.doSendKeys(reconSearchBar, reconName, AppConstants.time3);
        eleUtil.doClick(searchResult(reconName), AppConstants.time10);
        waitUtil.waitForElementsVisible(sourcesList, AppConstants.time10);
    }

    private void searchAndSelectSource(String sourceName) {
        waitUtil.waitForElementVisible(sourcesList, AppConstants.time10);
        eleUtil.doSendKeys(sourcesSearchBar, sourceName, AppConstants.time3);
        waitUtil.waitFor(1);
        eleUtil.doClick(searchResult(sourceName), AppConstants.time10);
        waitUtil.waitForElementVisible(mappingsGrid, AppConstants.time20);
    }

    public void selectReconAndSource(String reconName, String sourceName) {
        Allure.step("Search for a recon and select a source");
        searchAndSelectRecon(reconName);
        searchAndSelectSource(sourceName);
        waitUtil.waitFor(3);
    }

    public String verifyFieldInMapping(String fieldName) {
        try {
            waitUtil.waitForElementVisible(reconFieldInMapping(fieldName), AppConstants.time10);
            return "Field '" + fieldName + "' found in source field mapping successfully.";
        } catch (Exception e) {
            return "Error: Field '" + fieldName + "' not found in source field mapping. ";
        }
    }
}
