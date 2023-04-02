package my.group.utilities;

import my.group.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyLogger {
    private static final String PATH_LOGGER = "logback.xml";

    public Logger getLogger() {
        System.setProperty("logback.configurationFile", PATH_LOGGER);
        return LoggerFactory.getLogger(App.class);
    }
}
