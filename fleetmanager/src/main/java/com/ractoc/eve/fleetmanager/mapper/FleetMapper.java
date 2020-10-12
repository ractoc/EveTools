package com.ractoc.eve.fleetmanager.mapper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ractoc.eve.domain.BaseMapper;
import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.Fleet;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.FleetImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.OptionalInt;

@Mapper
public interface FleetMapper extends BaseMapper {
    FleetMapper INSTANCE = Mappers.getMapper(FleetMapper.class);

    @Named("corporationIdToCorporationRestricted")
    static boolean corporationIdToCorporationRestricted(OptionalInt corporationId) {
        return corporationId.isPresent() && corporationId.getAsInt() > 0;
    }

    @Named("startToTimeStamp")
    static Timestamp startToTimeStamp(String start) {
        JsonObject jsonObject = JsonParser.parseString(start).getAsJsonObject();
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
    }

    private static String convertValueToString(int value) {
        String result = "";
        if (value < 10) {
            result += "0";
        }
        result += value;
        return result;
    }

    @Mapping(source = "start", target = "startDateTime", qualifiedByName = "startToTimeStamp")
    FleetImpl modelToDb(FleetModel model);

    @Mapping(source = "corporationId", target = "corporationRestricted", qualifiedByName = "corporationIdToCorporationRestricted")
    FleetModel dbToModel(Fleet fleet);
}
