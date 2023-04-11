package my.group;

import my.group.utilities.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import org.postgresql.Driver;

import java.sql.DriverManager;
import java.sql.SQLException;

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
        int countGoods =Integer.parseInt(PROPERTIES.getProperty("countGoods"));
        String typeGood = args.length!=0?args[0]:"firstType";
        Connection connection = createConnection(endPoint,userName,password);

        TABLE.createTables(connection);
        RPS rpsFillTables = TABLE.fillTablesAndGetRPS(connection);
        RPS rpsGoods = TABLE.fillGoodsTableAndGetRPS(connection,countGoods);
        int indexType = TABLE.findIndexType(connection,typeGood);
        RPS rpsStoreGood = TABLE.fillStoreGoodTable(connection,rpsGoods.getCount());
        RPS.startWatch();
        String addressStore = TABLE.getAddressStore(connection,indexType);
        LOGGER.info("Store search time : {}", RPS.getTimeSecond());
        LOGGER.info("Time to fill tables with stores, brands and types  : {}",(rpsFillTables.getTimeSecond()));
        LOGGER.info("Time to fill the table with goods : {}",(rpsGoods.getTimeSecond()));
        LOGGER.info("Number of added goods : {}",(rpsGoods.getCount()));
        LOGGER.info("RPS of filling the table with goods : {}",(rpsGoods.getRPS()));
        LOGGER.info("Time to complete the table with goods and shops: {}",(rpsStoreGood.getTimeSecond()));
        LOGGER.info("RPS filling the table with goods and shops : {}",(rpsStoreGood.getRPS()));
        LOGGER.info("Number of rows in the table with goods and products : {}",(rpsStoreGood.getCount()));

        LOGGER.info(addressStore);
        RPS.stopWatch();

        closeConnection(connection);
    }


    private static void closeConnection(Connection connection)  {
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.error("Unable to close connection",e);
        }
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