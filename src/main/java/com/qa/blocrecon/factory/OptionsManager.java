package com.qa.blocrecon.factory;

import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;

public class OptionsManager {

    public ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        // Basic options
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        // options.addArguments("--disable-popup-blocking");

        // Auto-detect Jenkins/CI environment or headless property
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        } else {
            options.addArguments("--start-maximized");
        }

        // Suppress "Chrome is being controlled..." message
        // options.setExperimentalOption("useAutomationExtension", false);
        // options.setExperimentalOption("excludeSwitches", new String[] {
        // "enable-automation" });

        // // Optional: disable infobars as a fallback
        // Map<String, Object> prefs = new HashMap<>();
        // prefs.put("credentials_enable_service", false);
        // prefs.put("profile.password_manager_enabled", false);
        // options.setExperimentalOption("prefs", prefs);

        return options;
    }
}
