package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final int MAX_RETRY = 1; // retry once

    @Override
    public boolean retry(ITestResult result) {

        if (retryCount < MAX_RETRY) {
            retryCount++;
            System.out.println("Retrying test: "
                    + result.getName()
                    + " | Attempt: "
                    + retryCount);
            return true;
        }

        return false;
    }
}