package com.qa.blocrecon.pages;

import com.qa.blocrecon.constants.AppConstants;
import com.qa.blocrecon.utils.ElementsUtil;
import com.qa.blocrecon.utils.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class LoginPage {
    WebDriver driver;
    JavascriptExecutor js;
    ElementsUtil eleUtil;
    WaitUtil waitUtil;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
        this.eleUtil = new ElementsUtil(driver);
        this.waitUtil = new WaitUtil(driver);
    }

    /*****************************************************Static Locators**************************************************/

    private final By usernameTextBox = By.id("username");
    private final By passwordTextBox = By.id("password");
    private final By loginButton = By.xpath("//button[text()='Log In']");
    private final By settingDropdown = By.id("settingDropdown");
    private final By loader = By.xpath("//div[@class='status d-flex']");

    /******************************************************** Methods *****************************************************/

    /**
     * Enters the username in the username text box.
     * @param username The username to enter.
     */
    private void enterUsername(String username) {
        eleUtil.doSendKeys(usernameTextBox, username, AppConstants.time3);
    }

    /**
     * Enters the password in the password text box.
     * @param password The password to enter.
     */
    private void enterPassword(String password) {
        eleUtil.doSendKeys(passwordTextBox, password, AppConstants.time3);
    }

    /**
     * Clicks the login button.
     */
    private void clickLoginButton() {
        eleUtil.doClick(loginButton, AppConstants.time3);
    }

    /**
     * Waits for the settings icon to be clickable.
     */
    private void waitForSettingsIconPresence() {
        waitUtil.waitForElementToBeClickable(settingDropdown, AppConstants.time20);
    }

    /**
     * Enters the username and password and clicks the login button.
     * @param username The username to enter.
     * @param password The password to enter.
     * @return The HomePage object.
     */
    public HomePage enterCredentialsAndClickLoginButton(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        waitUtil.waitForLoaderVisible(loader, AppConstants.time20);
        waitUtil.waitForLoaderInvisible(loader, AppConstants.time20);
        waitForSettingsIconPresence();
        return new HomePage(driver);
    }
}
