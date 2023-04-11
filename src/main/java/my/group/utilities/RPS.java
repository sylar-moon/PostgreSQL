package my.group.utilities;

import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

public class RPS {
    private final StopWatch watch = new StopWatch();
    private int count = 0;

    public void startWatch() {
        watch.start();
    }

    public double getRPS() {
        return count / (double) getTimeSecond();
    }

    public long getTimeSecond() {
        return watch.getTime(TimeUnit.SECONDS);
    }

    public void stopWatch() {
        watch.stop();
    }

    public void incrementCount() {
        count++;
    }

    public int getCount() {
        return count;
    }

}
