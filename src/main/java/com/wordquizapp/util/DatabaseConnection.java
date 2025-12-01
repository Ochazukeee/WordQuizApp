package com.wordquizapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class for managing database connections.
 */
public class DatabaseConnection {
    // Database connection details 
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=test_database;encrypt=false";
    private static final String DB_USER = "admin"; 
    private static final String DB_PASSWORD = "e8u4c6hj0f"; 
    
    // Logger for outputting logs
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    
    /**
     * Establishes and retrieves a database connection.
     * @return A database Connection object.
     * @throws SQLException If a SQL execution error occurs.
     */
    public static Connection getConnection() throws SQLException {
        logger.info("=== Starting database connection ===");
        logger.info("Connection URL: " + DB_URL);
        logger.info("Connection User: " + DB_USER);
        
        try {
            // Loading the SQL Server JDBC driver
            logger.info("Loading JDBC driver...");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            logger.info("JDBC driver loaded successfully");
            
            logger.info("Attempting to connect to the database...");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            if (conn != null && !conn.isClosed()) {
                logger.info("Successfully connected to the database!");
                logger.info("Connection Status: " + (conn.isClosed() ? "Closed" : "Open"));
                return conn;
            } else {
                logger.severe("Database connection is null or closed.");
                throw new SQLException("Failed to get a database connection.");
            }
            
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "JDBC driver not found", e);
            logger.severe("Solution: Check if the mssql-jdbc jar file is included in your classpath.");
            throw new SQLException("Database driver not found", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection error", e);
            logger.severe("Error Code: " + e.getErrorCode());
            logger.severe("SQLState: " + e.getSQLState());
            logger.severe("Solution: Check your connection details and the status of your SQL Server.");
            throw e;
        }
    }
    
    /**
     * Tests the database connection.
     * @return true if the connection is successful, otherwise false.
     */
    public static boolean testConnection() {
        logger.info("=== Starting database connection test ===");
        
        try (Connection conn = getConnection()) {
            boolean isConnected = conn != null && !conn.isClosed();
            logger.info("Connection Test Result: " + (isConnected ? "Success" : "Failure"));
            
            if (isConnected) {
                // Retrieves and outputs database metadata
                String dbProduct = conn.getMetaData().getDatabaseProductName();
                String dbVersion = conn.getMetaData().getDatabaseProductVersion();
                logger.info("Database Product: " + dbProduct);
                logger.info("Database Version: " + dbVersion);
            }
            
            return isConnected;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "An error occurred during the database connection test", e);
            logger.severe("Detailed Error Information:");
            logger.severe("- Message: " + e.getMessage());
            logger.severe("- SQLState: " + e.getSQLState());
            logger.severe("- Error Code: " + e.getErrorCode());
            
            // Provides solutions for common errors
            if (e.getMessage().contains("Login failed")) {
                logger.severe("Solution: Check your username or password.");
            } else if (e.getMessage().contains("Connection refused") || 
                       e.getMessage().contains("No connection could be made")) {
                logger.severe("Solution: Check if your SQL Server is running and the port number is correct.");
            } else if (e.getMessage().contains("database") && e.getMessage().contains("does not exist")) {
                logger.severe("Solution: Check if the database 'WordApp' exists.");
            }
            
            return false;
        }
    }
    
    /**
     * Outputs debug connection information (password is masked).
     */
    public static void printConnectionInfo() {
        logger.info("=== Database Connection Information ===");
        logger.info("URL: " + DB_URL);
        logger.info("User: " + DB_USER);
        logger.info("Password: " + (DB_PASSWORD != null ? "**** (Set)" : "null (Not set)"));
        logger.info("========================");
    }
}