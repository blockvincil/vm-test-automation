package utils;

import com.qa.blocrecon.base.BaseTest;
import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    // Attach Excel whenever the test starts
    @Override
    public void onTestStart(ITestResult result) {

        System.out.println("TEST STARTED: " + result.getName());

        try {
            FileAttachmentUtil.attachExcel(
                    "src/test/resources/testdata/CashItems.xlsx"
            );
        } catch (Exception e) {
            System.out.println("Unable to attach Excel file: " + e.getMessage());
        }
    }

    private void capture(ITestResult result) {

        System.out.println("LISTENER TRIGGERED");

        BaseTest testClass = (BaseTest) result.getInstance();
        WebDriver driver = testClass.getDriver();

        if (driver != null) {

            // Screenshot
            ScreenshotUtil.attachScreenshot(driver);

            // Page URL
            Allure.addAttachment("Page URL", driver.getCurrentUrl());

            // Test Name
            Allure.addAttachment("Test Name", result.getName());

            // Exception
            if (result.getThrowable() != null) {
                Allure.addAttachment(
                        "Exception",
                        result.getThrowable().toString()
                );
            }
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        capture(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        capture(result);
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        capture(result);
    }
}