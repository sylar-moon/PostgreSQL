package my.group.utilities;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class MyProperties  {
    private final Properties properties;
    private static final String PROPERTIES_PATH = "config.properties";


    public MyProperties()  {
        properties=new Properties();
    }

    public void readPropertyFile() throws IOException{
        try (FileInputStream inputStream = new FileInputStream(PROPERTIES_PATH)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            properties.load(reader);
            reader.close();
        }
    }

    public String getProperty(String key)  {
        return properties.getProperty(key);
    }
}

