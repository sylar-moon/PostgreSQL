package my.group.DTO;

import my.group.DDLScript;
import my.group.Utilities.MyLogger;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.stream.Stream;

public class GoodFactory {
    private static final Random random = new Random();
    private static final Logger LOGGER = new MyLogger().getLogger();

    public Stream<Good> creatRandomGood (DDLScript ddl){
        int countTypes = getCountTable(ddl,"types");
        int countBrands = getCountTable(ddl,"brands");
        String randomName = RandomStringUtils.random(40,true,false);
        return Stream.generate(()->new Good(randomName,random.nextInt(countTypes)+1,random.nextInt(countBrands)+1));

    }

    private int getCountTable(DDLScript ddl, String tableName) {
        String sql = "SELECT COUNT(*) AS row_count FROM "+tableName;
        ResultSet resultSet = ddl.executeQuery(sql);
        try {
            if(resultSet.next()){
                return resultSet.getInt("row_count");
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to get count table: {}",tableName,e);
            System.exit(0);
        }
        return 0;
    }
}
