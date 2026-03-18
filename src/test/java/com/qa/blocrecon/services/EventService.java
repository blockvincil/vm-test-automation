package com.qa.blocrecon.services;

import com.qa.blocrecon.db.EventLockRepository;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.testng.Assert;

public class EventService {

    private final EventLockRepository repository;

    // configurable defaults
    private static final int DEFAULT_TIMEOUT_SEC = 30;
    private static final int POLL_INTERVAL_MS = 1000;

    public EventService(EventLockRepository repository) {
        this.repository = repository;
    }

    /* ======================================================
       PUBLIC VERIFICATION METHODS (used by test cases)
       ====================================================== */

    /**
     * Asserts that the latest event for the given reconciliation ID has completed.
     * @param reconId The reconciliation ID to check.
     */
    public void assertLatestEventCompleted(String reconId) {
        Allure.step("Check if the latest triggered event is completed using DB query");
        assertLatestEventCompleted(reconId, DEFAULT_TIMEOUT_SEC);
    }

    public void assertLatestEventFailedOrCompletedWithError(String reconId) {
        assertLatestEventFailedOrCompletedWithError(reconId, DEFAULT_TIMEOUT_SEC);
    }

    /* ======================================================
       PRIVATE LOGIC METHODS (used within class)
       ====================================================== */

    /**
     * Asserts that the latest event for the given reconciliation ID has completed within the specified timeout.
     * @param reconId The reconciliation ID to check.
     * @param timeoutSeconds The maximum time to wait for the event to complete.
     */
    private void assertLatestEventCompleted(String reconId, int timeoutSeconds) {

        String finalStatus = waitForEventToFinish(reconId, timeoutSeconds);

        if ("Completed".equalsIgnoreCase(finalStatus)) {
            return;
        }

        if ("Running".equalsIgnoreCase(finalStatus)) {
            Assert.fail("Event did not complete within " + timeoutSeconds + " seconds for reconId: " + reconId);
        }

        String errorDescription =
                repository.getLatestErrorDescription(reconId);

        Assert.fail(
                "Event failed for reconId: " + reconId +
                        " | Status: " + finalStatus +
                        " | Error: " + errorDescription
        );
    }

    private void assertLatestEventFailedOrCompletedWithError(String reconId, int timeoutSeconds) {

        String finalStatus = waitForEventToFinish(reconId, timeoutSeconds);

        if ("Failed".equalsIgnoreCase(finalStatus) || "Completed With Error".equalsIgnoreCase(finalStatus)) {
            return;
        }

        if ("Running".equalsIgnoreCase(finalStatus)) {
            Assert.fail("Event did not complete within " + timeoutSeconds + " seconds for reconId: " + reconId);
        }

        Assert.fail(
                "Expected event to fail, but status was: " + finalStatus +
                        " for reconId: " + reconId
        );
    }

    /* ======================================================
       INTERNAL POLLING LOGIC
       ====================================================== */

    private String waitForEventToFinish(String reconId, int timeoutSeconds) {

        long endTime = System.currentTimeMillis()
                + (timeoutSeconds * 1000L);

        String status;

        while (System.currentTimeMillis() < endTime) {

            status = repository.getLatestEventStatus(reconId);

            if (status == null) {
                sleep();
                continue;
            }

            if (isFinalStatus(status)) {
                return status;
            }

            sleep();
        }

        return repository.getLatestEventStatus(reconId);
    }

    private boolean isFinalStatus(String status) {
        return "Completed".equalsIgnoreCase(status)
                || "Failed".equalsIgnoreCase(status)
                || "Error".equalsIgnoreCase(status)
                || "Completed With Error".equalsIgnoreCase(status);
    }

    private void sleep() {
        try {
            Thread.sleep(POLL_INTERVAL_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
