package cody.ecommerce.cody_app.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeZoneUtil {
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("UTC+7");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("-HHmmss");

    public static String getCurrentDateTime() {
        return ZonedDateTime.now(DEFAULT_ZONE_ID).format(formatter);
    }

    public static ZonedDateTime now() {
        return ZonedDateTime.now(DEFAULT_ZONE_ID);
    }

    public static ZonedDateTime toUtc7(ZonedDateTime dateTime) {
        return dateTime.withZoneSameInstant(DEFAULT_ZONE_ID);
    }
}