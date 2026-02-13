package com.qa.blocrecon.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class DatabaseUtil {

    public Connection conn;
    private Properties prop;

    public DatabaseUtil() {
        prop = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            prop.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DB config file", e);
        }
    }

    public void connect() throws SQLException {
        String serverName = prop.getProperty("db.serverName");
        String port = prop.getProperty("db.port");
        String dbName = prop.getProperty("db.dbName");
        String user = prop.getProperty("db.user");
        String password = prop.getProperty("db.password");

        String dbUrl = "jdbc:postgresql://" + serverName + ":" + port + "/" + dbName;
        conn = DriverManager.getConnection(dbUrl, user, password);
    }

    public void disconnect() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    private String stripDecimalIfWhole(Number number) {
        // Handles Integer, Long, BigDecimal, Double, etc.
        if (number instanceof java.math.BigDecimal) {
            java.math.BigDecimal bd = (java.math.BigDecimal) number;
            return bd.stripTrailingZeros().toPlainString();
        }

        double d = number.doubleValue();
        if (d == Math.floor(d)) {
            return String.valueOf((long) d);
        }

        return number.toString();
    }

    public List<Map<String, String>> executeSelectQuery(String query) throws SQLException {
        List<Map<String, String>> results = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, String> row = new LinkedHashMap<>();

                for (int i = 1; i <= colCount; i++) {
                    Object value = rs.getObject(i);
                    String colName = meta.getColumnLabel(i);

                    if (value == null) {
                        row.put(colName, null);
                    }
                    // Remove trailing .0 for whole numbers
                    else if (value instanceof Number) {
                        row.put(colName, stripDecimalIfWhole((Number) value));
                    }
                    else {
                        row.put(colName, value.toString());
                    }
                }
                results.add(row);
            }
        }

        return results;
    }


}
