package utils;

import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenshotUtil {

    @Attachment(value = "Failure Screenshot", type = "image/png")
    public static byte[] attachScreenshot(WebDriver driver) {
        try {
            if (driver == null) {
                System.err.println("Driver is null, cannot capture screenshot");
                return new byte[0];
            }

            String currentUrl = driver.getCurrentUrl();
            if (currentUrl == null || currentUrl.isEmpty()) {
                System.err.println("Driver session is invalid, cannot capture screenshot");
                return new byte[0];
            }

            return ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
            return new byte[0];
        }
    }
}

