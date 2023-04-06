package my.group;

import com.opencsv.CSVReader;
import my.group.DTO.Good;
import my.group.DTO.GoodFactory;
import my.group.utilities.MyLogger;
import my.group.utilities.MyValidator;
import my.group.utilities.RPS;
import org.slf4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Table {
    private final Logger logger = new MyLogger().getLogger();
    private final MyValidator validator = new MyValidator();
    private final Random random = new Random();
    private final RPS rps = new RPS();
    void fillTables(Connection connection) {
        String sqlForStore = "INSERT INTO stores(id,city,address) VALUES (?,?,?)";
        fillTable(connection, "stores.csv", sqlForStore);

        String sqlForType = "INSERT INTO types(id,name_type) VALUES (?,?)";
        fillTable(connection, "types.csv", sqlForType);

        String sqlForBrand = "INSERT INTO brands(id,name_brand) VALUES (?,?)";
        fillTable(connection, "brands.csv", sqlForBrand);
    }


    /**
     * Заполняет таблицу базы данных строками из csv файла
     *
     * @param connection подключение к базе данных
     * @param path       путь к csv файлу
     * @param sql        sql запроз на добавление строки в таблицу
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
            logger.error("Unable to fill table with sql query: {}", sql, e);
        }
    }

    public void fillGoodsTable(Connection connection, DDLScript ddl, int countGoods) {
        rps.startWatch();
        int sizeBatch = 1000;
        int idCounter = 0;
        int countTypes = getCountTable(ddl, "types");
        int countBrands = getCountTable(ddl, "brands");
        int countStores = getCountTable(ddl, "stores");
        Supplier<Stream<Good>> supplier = () -> new GoodFactory().creatRandomGood(countTypes, countBrands);
        String sql = "INSERT INTO goods(id,name_goods,types_id,brands_id) VALUES (?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int counter = 0;
            while (counter != countGoods) {
                counter++;
                Good good = getGood(supplier);
                if (validator.validateGood(good)) {
                    rps.incrementCount();

                    idCounter++;
                    statement.setInt(1, idCounter);
                    statement.setString(2, good.getGoodName());
                    statement.setInt(3, good.getTypeId());
                    statement.setInt(4, good.getBrandId());
                    statement.addBatch();

//                    deliverToStores(connection, countStores, idCounter);
                    if(idCounter%sizeBatch==0){
                        statement.executeBatch();
                    }

                }
            }
            statement.executeBatch();

        } catch (SQLException e) {
            logger.error("Unable to fill goods table with sql query: {}", sql, e);
        }


        rps.stopWatch();
        logger.info(String.valueOf(rps.getRPS()));

    }

    //Разобраться с подсчетом товаров в магазинах
    private void deliverToStores(Connection connection, int countStores, int idGood) {
        int randomCount = random.nextInt(countStores);
        int countStoresWithGood = randomCount == 0 ? 1 : randomCount;
        int firstIdStoreWithGood = random.nextInt(countStores - countStoresWithGood) + 1;
        String sql = "INSERT INTO store_good(stores_id,goods_id) VALUES (?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = firstIdStoreWithGood; i < countStoresWithGood + firstIdStoreWithGood; i++) {
                statement.setInt(1, i);
                statement.setInt(2, idGood);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            logger.error("Unable to fill store_good table with sql query: {}", sql, e);
        }
    }

    private Good getGood(Supplier<Stream<Good>> supplier) {
        Optional<Good> optionalPerson = supplier.get().findAny();
        return optionalPerson.orElse(null);
    }

    /**
     * С помощью sql запросов создаем 5 таблиц :
     * stores,brands,types,goods and store_good
     *
     * @param ddl ddl script для передачи sql запроса
     */
    public void createTables(DDLScript ddl) {
//        ddl.executeUpdate("CREATE TABLE stores (\n" +
//                "  id INT,\n" +
//                "  city VARCHAR(20) NOT NULL,\n" +
//                "  address VARCHAR(100) NOT NULL,\n" +
//                "  CONSTRAINT stores_pk PRIMARY KEY (id)\n" +
//                ")");
//
//        ddl.executeUpdate("CREATE TABLE brands (\n" +
//                "  id INT,\n" +
//                "  name_brand VARCHAR(40) NOT NULL,\n" +
//                "  CONSTRAINT brands_pk PRIMARY KEY (id)\n" +
//                ")");
//
//        ddl.executeUpdate("CREATE TABLE types (\n" +
//                "  id INT,\n" +
//                "  name_type VARCHAR(40) NOT NULL,\n" +
//                "  CONSTRAINT types_pk PRIMARY KEY (id)\n" +
//                ")");
//        ddl.executeUpdate("CREATE INDEX name_type ON types (name_type)");


        ddl.executeUpdate("CREATE TABLE goods (\n" +
                "  id INT,\n" +
                "  name_goods VARCHAR(40) NOT NULL,\n" +
                "  types_id INT NOT NULL,\n" +
                "  brands_id INT NOT NULL,\n" +
                "  FOREIGN KEY(types_id) REFERENCES types(id),\n" +
                "  FOREIGN KEY(brands_id) REFERENCES brands(id),\n" +
                "  CONSTRAINT goods_pk PRIMARY KEY (id)\n" +
                ")");
        ddl.executeUpdate("CREATE INDEX types_id ON goods (types_id)");

//        ddl.executeUpdate("CREATE TABLE store_good" +
//                "  (stores_id INT NOT NULL,\n" +
//                "  goods_id INT NOT NULL,\n" +
//                "  FOREIGN KEY(stores_id) REFERENCES stores(id),\n" +
//                "  FOREIGN KEY(goods_id) REFERENCES goods(id))"
//               );
//        ddl.executeUpdate("CREATE INDEX stores_id ON store_good (stores_id)");
//        ddl.executeUpdate("CREATE INDEX goods_id ON store_good (goods_id)");

    }

    public int getCountTable(DDLScript ddl, String tableName) {
        String sql = "SELECT COUNT(*) AS row_count FROM " + tableName;
        return Integer.parseInt(ddl.executeQuery(sql,"row_count")) ;
    }


    public int findIndexType(DDLScript ddl, String typeGood) {
        if (typeGood.equals("randomType")) {
            int countTypes = getCountTable(ddl, "types");
            return random.nextInt(countTypes) + 1;
        }
        String sql = "SELECT id " +
                "FROM types" +
                " WHERE name_type = '" + typeGood + "'";
       return Integer.parseInt(ddl.executeQuery(sql,"id")) ;

    }

    public String getAddressStore(DDLScript ddl, int indexType) {
    String sql1 = "SELECT store_good.stores_id, COUNT(*) AS count \n" +
            "FROM store_good \n" +
            "INNER JOIN goods ON store_good.goods_id = goods.id\n" +
            "WHERE goods.types_id = " + indexType+
            "GROUP BY store_good.stores_id \n" +
            "ORDER BY count DESC \n" +
            "LIMIT 1;";
    int idStore = Integer.parseInt(ddl.executeQuery(sql1,"stores_id"));
    String sql2 = "SELECT address " +
            "FROM stores " +
            "WHERE id ="+ idStore;
        return ddl.executeQuery(sql2,"address");
    }
}
