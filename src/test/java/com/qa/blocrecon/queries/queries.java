package com.qa.blocrecon.queries;

import com.qa.blocrecon.utils.DatabaseUtil;
import io.qameta.allure.Allure;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class queries {

    private final DatabaseUtil dbUtil;

    public queries(DatabaseUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    public void executeQuery(String query, String description) {
        Allure.step("Running SQL Query for: " + description, () -> {
            try (PreparedStatement ps = dbUtil.conn.prepareStatement(query)) {
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to execute query: " + query, e);
            }
        });
    }
}
