package my.group;

import my.group.utilities.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.sql.Connection;

import org.postgresql.Driver;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This program reads data from the config.properties file to connect to the database and
 * creates a connection to PostgreSQL which is on AWS.
 * After that, using the createTables.sql file, it creates tables in the database: good, store, store_good, type.
 * Fills tables with data from csv files: stores.csv, types.csv.
 * Creates products with a random name and type from the type table in the amount specified in the config.properties file
 * Randomly allocates goods to stores in the store_good table
 * Finds the address of the store with the largest number of goods of a certain type,
 * type is specified in the arguments when starting the program
 */

public class App {
    private static final Logger LOGGER = new MyLogger().getLogger();
    private static final MyProperties PROPERTIES = new MyProperties();
    private static final Table TABLE = new Table();
    private static final RPS RPS = new RPS();

    public static void main(String[] args) {
        readPropertyFile();
        String endPoint = PROPERTIES.getProperty("endPoint");
        String userName = PROPERTIES.getProperty("userName");
        String password = PROPERTIES.getProperty("password");
        int countGoods = Integer.parseInt(PROPERTIES.getProperty("countGoods"));
        String typeGood = args.length != 0 ? args[0] : "firstType";
        Connection connection = createConnection(endPoint, userName, password);

        TABLE.createTables(connection);
        RPS rpsFillTables = TABLE.fillTablesAndGetRPS(connection);
        RPS rpsGoods = TABLE.fillGoodsTableAndGetRPS(connection, countGoods);
        int indexType = TABLE.findIndexType(connection, typeGood);
        RPS rpsStoreGood = TABLE.fillStoreGoodTable(connection, rpsGoods.getCount());
        RPS.startWatch();
        String addressStore = TABLE.getAddressStore(connection, indexType);
        LOGGER.info("Time to fill tables with stores and types  : {} millisecond", (rpsFillTables.getTimeMilliSecond()));
        LOGGER.info("Time to fill the table with goods : {} second", (rpsGoods.getTimeSecond()));
        LOGGER.info("Number of added goods : {}", (rpsGoods.getCount()));
        LOGGER.info("RPS of filling the table with goods : {}", (rpsGoods.getRPS()));
        LOGGER.info("Time to complete the table with goods and shops: {} second", (rpsStoreGood.getTimeSecond()));
        LOGGER.info("RPS filling the table with goods and shops : {}", (rpsStoreGood.getRPS()));
        LOGGER.info("Number of rows in the table with goods and shops : {}", (rpsStoreGood.getCount()));

        LOGGER.info(addressStore);
        LOGGER.info("Store search time : {} millisecond", RPS.getTimeMilliSecond());

        RPS.stopWatch();

        closeConnection(connection);
    }


    private static void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.error("Unable to close connection", e);
        }
    }

    private static Connection createConnection(String endPoint, String userName, String password) {
        try {
            Class<Driver> driverClass = Driver.class;

            return DriverManager.getConnection(endPoint, userName, password);
        } catch (SQLException e) {
            LOGGER.error("Unable to create database connection", e);
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