package my.group;

import my.group.utilities.MyLogger;
import org.slf4j.Logger;

import java.sql.*;

public class DDLScript {
    private static final Logger LOGGER = new MyLogger().getLogger();
    private final Statement statement;

    DDLScript(Connection connection) throws SQLException {
        this.statement = connection.createStatement();
    }

    public void closeStatement() {
        try {
            statement.close();
        } catch (SQLException e) {
            LOGGER.error("Unable to close Statement", e);
        }
    }

    public Statement getStatement() {
        return statement;
    }

    public void executeUpdate(String sql) {
        try {
            statement.executeUpdate(sql);
            LOGGER.info("Data Base is update: {}", sql);
        } catch (SQLException e) {
            LOGGER.error("Unable to execute Update to Statement\n SQL request: {}", sql, e);
        }
    }

    public String executeQuery(String sql,String column) {
        try {
            ResultSet resultSet =  statement.executeQuery(sql);
            if (resultSet.next()) {
                return resultSet.getString(column);
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to execute Query to Statement\n SQL request: {}", sql, e);
            System.exit(0);
        }
        return null;
    }

}
