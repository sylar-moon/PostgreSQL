package my.group.DTO;

import org.apache.commons.lang3.RandomStringUtils;


import java.util.Random;
import java.util.stream.Stream;

public class GoodFactory {
    private  final Random random = new Random();

    public Stream<Good> creatRandomGood (int countTypes){
        String randomName = RandomStringUtils.random(random.nextInt(20),true,false);
        return Stream.generate(()->new Good(randomName,random.nextInt(countTypes)+1));
    }

}
