package com.qa.blocrecon.base;

import com.qa.blocrecon.db.EventLockRepository;
import com.qa.blocrecon.factory.ConfigReader;
import com.qa.blocrecon.factory.DriverFactory;
import com.qa.blocrecon.factory.OptionsManager;
import com.qa.blocrecon.pages.LoginPage;
import com.qa.blocrecon.services.EventService;
import com.qa.blocrecon.utils.DatabaseUtil;
import com.qa.blocrecon.utils.ElementsUtil;
import com.qa.blocrecon.utils.WaitUtil;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;

import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.sql.SQLException;
import java.util.Properties;

@Getter
public class BaseTest {

    public WebDriver driver;
    protected Properties prop;

    // Utilities
    protected DatabaseUtil dbUtil;
    protected ElementsUtil eleUtil;
    protected WaitUtil wait;
    protected SoftAssert softAssert;

    // Pages
    protected LoginPage loginPage;

    // Backend services
    protected EventService eventService;

    
    @BeforeMethod
    public void setUp() throws SQLException {

        // 1. Load config
        ConfigReader configReader = new ConfigReader();
        prop = configReader.initProperties();

        // 2. Init WebDriver
        DriverFactory driverFactory = new DriverFactory();
        driver = driverFactory.initDriver(
                prop.getProperty("browser").trim(),
                Boolean.parseBoolean(prop.getProperty("remote")),
                prop.getProperty("selenium.grid.url")
        );
        driver.get(prop.getProperty("url").trim());

        // 3. Init UI utilities & pages
        eleUtil = new ElementsUtil(driver);
        wait = new WaitUtil(driver);
        softAssert = new SoftAssert();
        loginPage = new LoginPage(driver);

        // 4. Init DB + backend services
        dbUtil = new DatabaseUtil();
        dbUtil.connect();

        eventService = new EventService(
                new EventLockRepository(dbUtil)
        );
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() throws SQLException {

        if (driver != null) {
            driver.quit();
        }

        if (dbUtil != null) {
            dbUtil.disconnect();
        }
    }
}