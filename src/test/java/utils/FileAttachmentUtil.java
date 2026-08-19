package utils;

import io.qameta.allure.Allure;

import java.io.IOException;
import java.io.InputStream;

public class FileAttachmentUtil {

    public static void attachExcel(String resourcePath) {

        try (InputStream inputStream = FileAttachmentUtil.class
                .getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (inputStream == null) {
                System.out.println("Failed to attach Excel: File not found in classpath: " + resourcePath);
                return;
            }

            Allure.addAttachment(
                    "Test Data - Excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    inputStream,
                    ".xlsx"
            );

        } catch (IOException e) {
            System.out.println(
                    "Failed to attach Excel: " + e.getMessage()
            );
        }
    }
}