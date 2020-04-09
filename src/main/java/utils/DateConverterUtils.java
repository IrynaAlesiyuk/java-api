package utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DateConverterUtils {

    private static final Logger LOGGER = LogManager.getLogger(DateConverterUtils.class.getName());

    public static long convertUtcToEpoch(String date) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);
        long epochMilli = Instant.from(fmt.parse(date)).toEpochMilli();

        LOGGER.info("Date " + date + " is converted to " + epochMilli);
        return epochMilli;
    }
}
