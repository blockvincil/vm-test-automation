package com.qa.blocrecon.db;

import com.qa.blocrecon.utils.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EventLockRepository {

    private final DatabaseUtil dbUtil;

    public EventLockRepository(DatabaseUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    /* ======================================================
       DB READ METHODS (NO ASSERTIONS HERE)
       ====================================================== */

    public String getLatestEventStatus(String reconId) {

        String query = """
            SELECT status
            FROM st_event_lock
            WHERE recon_id = ?
            ORDER BY start_time DESC
            LIMIT 1
        """;

        try (PreparedStatement ps =
                     dbUtil.conn.prepareStatement(query)) {

            ps.setString(1, reconId);
            ResultSet rs = ps.executeQuery();

            return rs.next() ? rs.getString("status") : null;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to fetch latest event status for reconId: " + reconId, e);
        }
    }

    public String getLatestErrorDescription(String reconId) {

        String query = """
            SELECT error_description
            FROM st_event_lock
            WHERE recon_id = ?
            ORDER BY start_time DESC
            LIMIT 1
        """;

        try (PreparedStatement ps =
                     dbUtil.conn.prepareStatement(query)) {

            ps.setString(1, reconId);
            ResultSet rs = ps.executeQuery();

            return rs.next() ? rs.getString("error_description") : null;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to fetch error description for reconId: " + reconId, e);
        }
    }
}
