package utils;

import com.qa.blocrecon.base.BaseTest;
import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private void capture(ITestResult result) {

        System.out.println("LISTENER TRIGGERED");

        BaseTest testClass = (BaseTest) result.getInstance();
        WebDriver driver = testClass.getDriver();

        if (driver != null) {

            // Screenshot
            ScreenshotUtil.attachScreenshot(driver);

            // Attach additional debug info
            Allure.addAttachment("Page URL", driver.getCurrentUrl());
            Allure.addAttachment("Test Name", result.getName());

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