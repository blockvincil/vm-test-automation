package com.qa.blocrecon.queries;

import com.qa.blocrecon.utils.DatabaseUtil;
import io.qameta.allure.Allure;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class queries {

    private final DatabaseUtil dbUtil;

    public queries(DatabaseUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    public void executeUpdate(String query) {
        Allure.step("Executing SQL update: " + query, () -> {
            try (PreparedStatement ps = dbUtil.conn.prepareStatement(query)) {
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to execute query: " + query, e);
            }
        });
    }
}
