package com.qa.blocrecon.factory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class DriverFactory {
    private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    public WebDriver initDriver(String browser) {
        return initDriver(browser, false, null);
    }

    public WebDriver initDriver(String browser, boolean remote, String gridUrl) {
        OptionsManager optionsManager = new OptionsManager();

        if (browser.equalsIgnoreCase("chrome")) {
            if (remote && gridUrl != null && !gridUrl.trim().isEmpty()) {
                try {
                    RemoteWebDriver remoteDriver = new RemoteWebDriver(new URL(gridUrl), optionsManager.getChromeOptions());
                    configureTimeouts(remoteDriver);
                    tlDriver.set(remoteDriver);
                } catch (MalformedURLException e) {
                    throw new RuntimeException("Invalid Selenium Grid URL: " + gridUrl, e);
                }
            } else {
                tlDriver.set(new org.openqa.selenium.chrome.ChromeDriver(optionsManager.getChromeOptions()));
            }
        }  else if (browser.equalsIgnoreCase("edge")) {
            if (remote && gridUrl != null && !gridUrl.trim().isEmpty()) {
                try {
                    RemoteWebDriver remoteDriver = new RemoteWebDriver(new URL(gridUrl), optionsManager.getEdgeOptions());
                    configureTimeouts(remoteDriver);
                    tlDriver.set(remoteDriver);
                } catch (MalformedURLException e) {
                    throw new RuntimeException("Invalid Selenium Grid URL: " + gridUrl, e);
                }
            } else {
                tlDriver.set(new EdgeDriver(optionsManager.getEdgeOptions()));
            }
        } else {

            throw new IllegalArgumentException(
                    "Browser not supported: " + browser
            );
        }

        getDriver().manage().deleteAllCookies();
        getDriver().manage().window().maximize();
        return getDriver();
    }

    private void configureTimeouts(RemoteWebDriver remoteDriver) {
        remoteDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        remoteDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        remoteDriver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
    }

    public static WebDriver getDriver() {
        return tlDriver.get();
    }

    public static WebDriver createStandaloneEdgeDriver(boolean remote, String gridUrl) {
        OptionsManager optionsManager = new OptionsManager();
        WebDriver edgeDriver;

        if (remote && gridUrl != null && !gridUrl.trim().isEmpty()) {
            try {
                RemoteWebDriver remoteDriver = new RemoteWebDriver(new URL(gridUrl), optionsManager.getEdgeOptions());
                remoteDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
                remoteDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
                remoteDriver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
                edgeDriver = remoteDriver;
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid Selenium Grid URL: " + gridUrl, e);
            }
        } else {
            edgeDriver = new EdgeDriver(optionsManager.getEdgeOptions());
        }

        edgeDriver.manage().deleteAllCookies();
        edgeDriver.manage().window().maximize();
        return edgeDriver;
    }
}
