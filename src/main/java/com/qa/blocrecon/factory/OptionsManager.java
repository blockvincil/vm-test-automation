package com.qa.blocrecon.factory;

import org.openqa.selenium.chrome.ChromeOptions;

public class OptionsManager {

    public ChromeOptions getChromeOptions() {

        ChromeOptions options = new ChromeOptions();

        boolean headless = Boolean.parseBoolean(
                System.getProperty("headless", "false"));

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        } else {
            options.addArguments("--start-maximized");
        }

        System.out.println("Chrome Options: " + options.asMap());

        return options;
    }
}