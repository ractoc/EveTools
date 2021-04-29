package com.ractoc.eve.fleetmanager.mapper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class DateTimeUtil {

    public static Timestamp convertDateTimeToTimeStamp(String dateTime) {
        if (dateTime != null) {
            JsonObject jsonObject = JsonParser.parseString(dateTime).getAsJsonObject();
            int day = jsonObject.getAsJsonObject("date").get("day").getAsInt();
            int month = jsonObject.getAsJsonObject("date").get("month").getAsInt();
            int year = jsonObject.getAsJsonObject("date").get("year").getAsInt();
            int hour = jsonObject.getAsJsonObject("time").get("hour").getAsInt();
            int minute = jsonObject.getAsJsonObject("time").get("minute").getAsInt();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                    .withZone(ZoneId.systemDefault());
            String dateTimeString = ""
                    + convertValueToString(day) + "-"
                    + convertValueToString(month) + "-"
                    + year + " "
                    + convertValueToString(hour) + ":"
                    + convertValueToString(minute) + ":00";
            TemporalAccessor temporalAccessor = formatter.parse(dateTimeString);
            return Timestamp.from(Instant.from(temporalAccessor));
        } else {
            return null;
        }
    }

    private static String convertValueToString(int value) {
        String result = "";
        if (value < 10) {
            result += "0";
        }
        result += value;
        return result;
    }

}
