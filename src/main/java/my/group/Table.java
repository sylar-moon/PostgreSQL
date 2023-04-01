package my.group;

import com.opencsv.CSVReader;
import my.group.Utilities.MyLogger;
import org.slf4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Table {
    private final Logger logger = new MyLogger().getLogger();


    void fillTables(Connection connection) {
        String sqlForStore = "INSERT INTO stores(id,city,address) VALUES (?,?,?)";
        fillTable(connection, "stores.csv", sqlForStore);

        String sqlForType = "INSERT INTO types(id,name_type) VALUES (?,?)";
        fillTable(connection, "types.csv", sqlForType);

        String sqlForBrand = "INSERT INTO brands(id,name_brand) VALUES (?,?)";
        fillTable(connection, "brands.csv", sqlForBrand);
    }


    /**
     * Заполняет
     * @param connection
     * @param path
     * @param sql
     */
    public void fillTable(Connection connection, String path, String sql) {
        try (CSVReader reader = new CSVReader(new FileReader(path));
             PreparedStatement statement = connection.prepareStatement(sql)) {
            //В цикле считуем все строки из csv файла и добавляем в базу данных с помощью запроса sql
            for (String[] strings : reader) {
                statement.setInt(1, Integer.parseInt(strings[0]));
                for (int i = 1; i < strings.length; i++) {
                    statement.setString(i + 1, strings[i]);
                }
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (IOException e) {
            logger.error("Unable to read csv file: {}", path, e);
        } catch (SQLException e) {
            logger.error("Unable to insert store into stores", e);
        }
    }

    /**
     * С помощью sql запросов создаем 5 таблиц :
     * stores,brands,types,goods and store_good
     * @param ddl ddl script для передачи sql запроса
     */
    public void createTables(DDLScript ddl) {
        ddl.executeUpdate("CREATE TABLE stores (\n" +
                "  id INT,\n" +
                "  city VARCHAR(20) NOT NULL,\n" +
                "  address VARCHAR(100) NOT NULL,\n" +
                "  CONSTRAINT stores_pk PRIMARY KEY (id)\n" +
                ")");
        ddl.executeUpdate("CREATE TABLE brands (\n" +
                "  id INT,\n" +
                "  name_brand VARCHAR(40) NOT NULL,\n" +
                "  CONSTRAINT brands_pk PRIMARY KEY (id)\n" +
                ")");
        ddl.executeUpdate("CREATE TABLE types (\n" +
                "  id INT,\n" +
                "  name_type VARCHAR(40) NOT NULL,\n" +
                "  CONSTRAINT types_pk PRIMARY KEY (id)\n" +
                ")");

        ddl.executeUpdate("CREATE TABLE goods (\n" +
                "  id INT,\n" +
                "  name_goods VARCHAR(40) NOT NULL,\n" +
                "  types_id INT NOT NULL,\n" +
                "  brands_id INT NOT NULL,\n" +
                "  FOREIGN KEY(types_id) REFERENCES types(id),\n" +
                "  FOREIGN KEY(brands_id) REFERENCES brands(id),\n" +
                "  CONSTRAINT goods_pk PRIMARY KEY (id)\n" +
                ")");

        ddl.executeUpdate("CREATE TABLE store_good (\n" +
                "  id INT,\n" +
                "  stores_id INT NOT NULL,\n" +
                "  goods_id INT NOT NULL,\n" +
                "  FOREIGN KEY(stores_id) REFERENCES stores(id),\n" +
                "  FOREIGN KEY(goods_id) REFERENCES goods(id),\n" +
                "  CONSTRAINT store_good_pk PRIMARY KEY (id)\n" +
                ")");

    }

}
