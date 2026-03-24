package edu.cegepvicto.dimhortons;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConnectionFactory {
    private ConnectionFactory() {}
    // source: https://stackoverflow.com/questions/50379839/connection-java-mysql-public-key-retrieval-is-not-allowed
    private static final String DEFAULT_URL = "jdbc:mysql://127.0.0.1:3306/dimhortons?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "mysql";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DEFAULT_URL, DEFAULT_USER, DEFAULT_PASS);
    }
}
