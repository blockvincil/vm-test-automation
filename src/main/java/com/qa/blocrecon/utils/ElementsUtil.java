package com.qa.blocrecon.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ElementsUtil {

    private WebDriver driver;
    private WaitUtil waitUtil;

    public ElementsUtil(WebDriver driver) {
        this.driver = driver;
        this.waitUtil = new WaitUtil(driver);
    }

    // --- WITH TIMEOUT ---

    public boolean waitForElementToDisappear(By locator, int timeoutSeconds) {

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

            return wait.until(drv -> {
                List<WebElement> elements = drv.findElements(locator);
                return elements.isEmpty() || elements.stream().noneMatch(WebElement::isDisplayed);
            });

        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean waitForElementToDisappear(By locator,
                                             int appearTimeoutSeconds,
                                             int disappearTimeoutSeconds) {

        try {
            // Phase 1: short wait to see if element appears at all
            WebDriverWait appearWait =
                    new WebDriverWait(driver, Duration.ofSeconds(appearTimeoutSeconds));

            boolean appeared = appearWait.until(drv ->
                    !drv.findElements(locator).isEmpty()
            );

            // Phase 2: element appeared → wait for disappearance
            WebDriverWait disappearWait =
                    new WebDriverWait(driver, Duration.ofSeconds(disappearTimeoutSeconds));

            return disappearWait.until(drv -> {
                List<WebElement> elements = drv.findElements(locator);
                return elements.isEmpty() ||
                        elements.stream().noneMatch(WebElement::isDisplayed);
            });

        } catch (TimeoutException e) {
            // Element never appeared OR never disappeared
            return true; // safe to continue
        }
    }

    public boolean isElementVisible(By locator, int timeout) {
        try {
            waitUtil.waitForElementVisible(locator, timeout);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean areElementsVisible(By locator, int timeout) {
        try {
            List<WebElement> elements = waitUtil.waitForElementsVisible(locator, timeout);
            return !elements.isEmpty();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isElementClickable(By locator, int timeout) {
        try {
            waitUtil.waitForElementToBeClickable(locator, timeout);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void doClick(By locator, int timeout) {
        WebElement element = waitUtil.waitForElementToBeClickable(locator, timeout);
        element.click();
    }

    public void doSendKeys(By locator, String value, int timeout) {
        WebElement element = waitUtil.waitForElementVisible(locator, timeout);
        element.clear();
        element.sendKeys(value);
    }

    public String doGetText(By locator, int timeout) {
        return waitUtil.waitForElementVisible(locator, timeout).getText();
    }

    public String doGetAttribute(By locator, String attrName, int timeout) {
        return waitUtil.waitForElementVisible(locator, timeout).getAttribute(attrName);
    }

    public void doSelectByVisibleText(By locator, String visibleText, int timeout) {
        WebElement element = waitUtil.waitForElementVisible(locator, timeout);
        Select select = new Select(element);
        select.selectByVisibleText(visibleText);
    }

    public void doSelectByValue(By locator, String value, int timeout) {
        WebElement element = waitUtil.waitForElementVisible(locator, timeout);
        Select select = new Select(element);
        select.selectByValue(value);
    }

    public void doSelectByIndex(By locator, int index, int timeout) {
        WebElement element = waitUtil.waitForElementVisible(locator, timeout);
        Select select = new Select(element);
        select.selectByIndex(index);
    }

    // --- WITHOUT TIMEOUT ---

    public boolean isElementVisible(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    public void doClick(By locator) {
        WebElement element = driver.findElement(locator);
        element.click();
    }

    public void doSelectByVisibleText(By locator, String visibleText) {
        WebElement element = driver.findElement(locator);
        Select select = new Select(element);
        select.selectByVisibleText(visibleText);
    }

    public boolean areElementsVisible(By locator) {
        try {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement e : elements) {
                if (!e.isDisplayed()) return false;
            }
            return !elements.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementClickable(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element.isDisplayed() && element.isEnabled();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    // --- MISC ---

    public boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public int getElementsCount(By locator) {
        return driver.findElements(locator).size();
    }

    public List<WebElement> getElements(By locator) {
        return driver.findElements(locator);
    }
}

