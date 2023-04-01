package my.group;

import my.group.Utilities.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import org.postgresql.Driver;

import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Hello world!
 */
public class App {
    private static final Logger LOGGER = new MyLogger().getLogger();
    private static final MyProperties PROPERTIES = new MyProperties();
    public static final Table TABLE = new Table();
    public static void main(String[] args) {
        readPropertyFile();
        String endPoint = PROPERTIES.getProperty("endPoint");
        String userName = PROPERTIES.getProperty("userName");
        String password = PROPERTIES.getProperty("password");

        Connection connection = createConnection(endPoint,userName,password);

        DDLScript ddl = createDDLScript(connection);
        TABLE.createTables(ddl);
        TABLE.fillTables(connection);



        ddl.closeStatement();
        closeConnection(connection);
    }


    private static void closeConnection(Connection connection)  {
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.error("Unable to close connection",e);
        }
    }

    private static DDLScript createDDLScript(Connection connection) {
        try {
            return  new DDLScript(connection);
        } catch (SQLException e) {
            LOGGER.error("Unable to create statement",e);
            System.exit(0);
        }
        return null;

    }


    private static Connection createConnection(String endPoint, String userName, String password) {
        try {
            Class<Driver> driverClass =Driver.class;
            return DriverManager.getConnection(endPoint,userName,password);
        } catch (SQLException e) {
            LOGGER.error("Unable to create database connection",e);
            System.exit(0);
        }
        return null;
    }


    private static void readPropertyFile() {
        try {
            PROPERTIES.readPropertyFile();
        } catch (IOException e) {
            LOGGER.error("Properties file not found");
        }
    }

}
