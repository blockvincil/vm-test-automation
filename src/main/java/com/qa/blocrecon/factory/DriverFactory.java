package com.qa.blocrecon.factory;

import org.openqa.selenium.WebDriver;

public class DriverFactory {
    private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    public WebDriver initDriver(String browser) {
        OptionsManager optionsManager = new OptionsManager();

        if (browser.equalsIgnoreCase("chrome")) {
            tlDriver.set(new org.openqa.selenium.chrome.ChromeDriver(optionsManager.getChromeOptions()));
        }  else {
            throw new IllegalArgumentException("Browser not supported: " + browser);
        }

        getDriver().manage().deleteAllCookies();
        getDriver().manage().window().maximize();
        return getDriver();
    }

    public static WebDriver getDriver() {
        return tlDriver.get();
    }
}
