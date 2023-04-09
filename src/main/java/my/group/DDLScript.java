package my.group;

import my.group.utilities.MyLogger;
import org.slf4j.Logger;

import java.sql.*;

public class DDLScript {
    private  final Logger logger = new MyLogger().getLogger();

    public String executeQuery(Connection connection, String sql, String column) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(column);
            }
        } catch (SQLException e) {
            logger.error("Unable to execute query: {}",sql,e);
            System.exit(0);
        }
        return null;
    }

    public void executeUpdate(Connection connection, String sql) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            logger.info("Data Base is update: {}", sql);
        } catch (SQLException e) {
            logger.error("Unable to execute Update to Statement\n SQL request: {}", sql, e);
        }
    }
}