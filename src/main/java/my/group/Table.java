package my.group;

import com.opencsv.CSVReader;
import my.group.DTO.Good;
import my.group.DTO.GoodFactory;
import my.group.utilities.MyLogger;
import my.group.utilities.MyValidator;
import my.group.utilities.RPS;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
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
    private final DDLScript ddlScript = new DDLScript();
    private static final String PATH = "createTables.sql";
    public RPS fillTablesAndGetRPS(Connection connection) {
        RPS rps = new RPS();
        int counter = 0;
        rps.startWatch();
        String sqlForStore = "INSERT INTO stores(id,city,address) VALUES (?,?,?)";
        counter+=fillTable(connection, "stores.csv", sqlForStore);

        String sqlForType = "INSERT INTO types(id,name_type) VALUES (?,?)";
        counter+=fillTable(connection, "types.csv", sqlForType);

        String sqlForBrand = "INSERT INTO brands(id,name_brand) VALUES (?,?)";
        counter+=fillTable(connection, "brands.csv", sqlForBrand);
        rps.setCount(counter);
        rps.stopWatch();
        return rps;
    }


    /**
     * Заполняет таблицу базы данных строками из csv файла
     *
     * @param connection подключение к базе данных
     * @param path       путь к csv файлу
     * @param sql        sql запроз на добавление строки в таблицу
     */
    private int fillTable(Connection connection, String path, String sql) {
       int counter = 0;
        try (CSVReader reader = new CSVReader(new FileReader(path));
             PreparedStatement statement = connection.prepareStatement(sql)) {
            //В цикле считуем все строки из csv файла и добавляем в базу данных с помощью запроса sql
            for (String[] strings : reader) {
                statement.setInt(1, Integer.parseInt(strings[0]));
                for (int i = 1; i < strings.length; i++) {
                    counter++;
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
        return counter;
    }

    public RPS fillGoodsTableAndGetRPS(Connection connection, int countGoods) {
        RPS rps = new RPS();
        rps.startWatch();
        int sizeBatch = 1000;
        int countTypes = getCountTable(connection, "types");
        int countBrands = getCountTable(connection, "brands");
        Supplier<Stream<Good>> supplier = () -> new GoodFactory().creatRandomGood(countTypes, countBrands);
        String sql = "INSERT INTO goods(id,name_goods,types_id,brands_id) VALUES (?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int counter = 0;
            while (counter != countGoods) {
                counter++;
                Good good = getGood(supplier);
                if (validator.validateGood(good)) {
                    rps.incrementCount();
                    statement.setInt(1, rps.getCount());
                    statement.setString(2, good.getGoodName());
                    statement.setInt(3, good.getTypeId());
                    statement.setInt(4, good.getBrandId());
                    statement.addBatch();
                    if(rps.getCount()%sizeBatch==0){
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
        return rps;
    }

    //Разобраться с подсчетом товаров в магазинах
    public RPS fillStoreGoodTable(Connection connection, int sizeGoods) {
        RPS rps = new RPS();
        rps.startWatch();
        int sizeBatch = 1000;
        int countStores = getCountTable(connection, "stores");
        String sql = "INSERT INTO store_good(stores_id,goods_id,goods_quantity) VALUES (?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            while (sizeGoods!=0){
                int randomNumbStore = random.nextInt(countStores);
                //Количество магазинов с товаром
                int countStoresWithGood = randomNumbStore == 0 ? 1 : randomNumbStore;
                //id первого магазина с товаром
                int firstIdStoreWithGood = random.nextInt(countStores - countStoresWithGood) + 1;
                for (int i = firstIdStoreWithGood; i < countStoresWithGood + firstIdStoreWithGood; i++) {
                    statement.setInt(1, i);
                    statement.setInt(2, sizeGoods);
                    statement.setInt(3,random.nextInt(100)+1);
                    statement.addBatch();
                    rps.incrementCount();
                }
                if(rps.getCount()%sizeBatch==0){
                    statement.executeBatch();
                }
                sizeGoods--;
            }
            statement.executeBatch();
        } catch (SQLException e) {
            logger.error("Unable to fill store_good table with sql query: {}", sql, e);
        }
        rps.stopWatch();
        return rps;
    }

    private Good getGood(Supplier<Stream<Good>> supplier) {
        Optional<Good> optionalPerson = supplier.get().findAny();
        return optionalPerson.orElse(null);
    }

    /**
     * С помощью sql запросов создаем 5 таблиц :
     * stores,brands,types,goods and store_good
     *
     * @param connection соеденнение с базой
     */
    public void createTables(Connection connection) {
        try {
            File file = new File(PATH);
            String queries = new String(Files.readAllBytes(file.toPath()));
            String[] queriesArr = queries.split(";");
            for (String s : queriesArr) {
                ddlScript.executeUpdate(connection,s);
            }
        } catch (IOException e) {
            logger.error("Unable to create tables from queries in file: {}",PATH,e);
        }
    }

    public int getCountTable(Connection connection, String tableName) {
        String sql = "SELECT COUNT(*) AS row_count FROM " + tableName;
        return Integer.parseInt(ddlScript.executeQuery(connection,sql,"row_count")) ;
    }


    public int findIndexType(Connection connection, String typeGood) {
        if (typeGood.equals("firstType")) {
            return 1;
        }
        String sql = "SELECT id " +
                "FROM types" +
                " WHERE name_type = '" + typeGood + "'";
       return Integer.parseInt(ddlScript.executeQuery(connection,sql,"id")) ;
    }

    public String  getAddressStore(Connection connection, int indexType) {
        String sql1 = "SELECT store_good.stores_id, SUM(store_good.goods_quantity) AS total_quantity\n" +
                "FROM store_good\n" +
                "INNER JOIN goods ON store_good.goods_id = goods.id\n" +
                "WHERE goods.types_id = "+indexType+"\n" +
                "GROUP BY store_good.stores_id\n" +
                "ORDER BY total_quantity DESC\n" +
                "LIMIT 1;";
        int idStore = Integer.parseInt(ddlScript.executeQuery(connection,sql1,"stores_id"));
        String sql2 = "SELECT address, city " +
                "FROM stores " +
                "WHERE id ="+ idStore;
        return ddlScript.executeQuery(connection, sql2, "city") + ", " +
                ddlScript.executeQuery(connection, sql2, "address");
    }
}
