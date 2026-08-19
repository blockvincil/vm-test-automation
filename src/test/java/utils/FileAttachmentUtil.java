package utils;

import io.qameta.allure.Allure;

import java.io.FileInputStream;
import java.io.IOException;

public class FileAttachmentUtil {

    public static void attachExcel(String filePath) {

        try (FileInputStream inputStream = new FileInputStream(filePath)) {

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