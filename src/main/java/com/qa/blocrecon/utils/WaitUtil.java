package com.qa.blocrecon.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class WaitUtil {

    private WebDriver driver;

    public WaitUtil(WebDriver driver) {
        this.driver = driver;
    }

    public WebElement waitForElementVisible(By locator, int timeout) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeout))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public List<WebElement> waitForElementsVisible(By locator, int timeout) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeout))
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }


    /**
     * Pauses the execution for the specified number of seconds
     * @param seconds Number of seconds to wait
     */
    public void waitFor(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted", e);
        }
    }
    
    /**
     * Waits for an element to be absent from the DOM
     * @param locator The locator of the element to wait for
     * @param timeoutInSeconds Maximum time to wait in seconds
     * @return true if element is absent within timeout, false otherwise
     */
    public boolean waitForElementAbsent(By locator, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (Exception e) {
            return true; // Element is not present, which is what we want
        }
    }

    public WebElement waitForElementPresence(By locator, int timeout) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeout))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public WebElement waitForElementToBeClickable(By locator, int timeout) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeout))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public boolean waitForElementInvisibility(By locator, int timeout) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeout))
                .until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public Alert waitForAlert(int timeout) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeout))
                .until(ExpectedConditions.alertIsPresent());
    }
    
    /**
     * Waits for the loader to become invisible
     * @param loaderLocator Locator of the loader element
     * @param timeoutInSeconds Maximum time to wait in seconds
     * @throws TimeoutException if the loader does not become invisible within the timeout
     */
    public void waitForLoaderInvisible(By loaderLocator, int timeoutInSeconds) {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                .until(ExpectedConditions.invisibilityOfElementLocated(loaderLocator));
    }
    
    /**
     * Waits for the loader to become visible
     * @param loaderLocator Locator of the loader element
     * @param timeoutInSeconds Maximum time to wait in seconds
     * @throws TimeoutException if the loader does not become visible within the timeout
     */
    public void waitForLoaderVisible(By loaderLocator, int timeoutInSeconds) {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                .until(ExpectedConditions.visibilityOfElementLocated(loaderLocator));
    }

}
