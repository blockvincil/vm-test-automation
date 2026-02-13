package com.qa.blocrecon.pages;

import com.qa.blocrecon.constants.AppConstants;
import com.qa.blocrecon.utils.ElementsUtil;
import com.qa.blocrecon.utils.WaitUtil;
import org.apache.commons.collections.CollectionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.*;
import java.util.stream.Collectors;

public class EventRuleHierarchiesPage {
    WebDriver driver;
    JavascriptExecutor js;
    ElementsUtil eleUtil;
    WaitUtil waitUtil;

    public EventRuleHierarchiesPage(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
        this.eleUtil = new ElementsUtil(driver);
        this.waitUtil = new WaitUtil(driver);
    }

    /*****************************************************Static Locators**************************************************/

    private final By reconList = By.xpath("//ul[@class='flex-nowrap px-0 nav flex-column nav-tabs']");
    private final By eventsList = By.xpath("//span[@title='Events']/ancestor::ul");
    private final By searchBar = By.xpath("//input[@placeholder='Search...']");
    private final By triggerEventButton = By.xpath("//button[text()='Trigger Event']");
    private final By eventTriggeredNotification = By.xpath("//div[@class='notification-message' and text()='Event triggered successfully']");

    /*****************************************************Dynamic Locators*************************************************/

    private By reconSearchResult(String searchTerm) {
        return By.xpath("//a[@title = '" + searchTerm + "']");
    }

    private By event(String eventName) {
        return By.xpath("//a[@title='"+eventName+"']");
    }

    /*************************************************** Helper Methods ****************************************************/

    private void searchAndSelectRecon(String reconName) {
        waitUtil.waitForElementVisible(reconList, AppConstants.time10);
        eleUtil.doSendKeys(searchBar, reconName, AppConstants.time3);
        eleUtil.doClick(reconSearchResult(reconName), AppConstants.time10);
        waitUtil.waitForElementsVisible(eventsList, AppConstants.time10);
    }

    private void selectEvent(String eventName) {
        eleUtil.doClick(event(eventName), AppConstants.time10);
        waitUtil.waitForElementVisible(triggerEventButton, AppConstants.time20);
    }

    private void selectEventAndTrigger(String eventName) {
        selectEvent(eventName);
        waitUtil.waitFor(2);
        triggerEvent();
        waitUtil.waitFor(5);
    }

    private void triggerEvent() {
        waitUtil.waitForElementVisible(triggerEventButton, AppConstants.time10);
        js.executeScript("arguments[0].click();", driver.findElement(triggerEventButton));
        waitUtil.waitForElementVisible(eventTriggeredNotification, AppConstants.time10);
    }

    /**************************************************** Flow Methods ****************************************************/

    /**
     Searches for a reconciliation and triggers an event.
     @param reconName The name of the reconciliation to search for.
     @param eventName The name of the event to trigger.
     */
    public void searchReconAndTriggerEvent(String reconName, String eventName) {
        searchAndSelectRecon(reconName);
        selectEventAndTrigger(eventName);
        waitUtil.waitFor(5);
    }

}
