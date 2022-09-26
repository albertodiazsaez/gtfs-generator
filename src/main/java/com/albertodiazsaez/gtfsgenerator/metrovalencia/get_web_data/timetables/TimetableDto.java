package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.timetables;

import java.sql.Date;
import java.sql.Time;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class TimetableDto {

    @JsonAlias("ORIGIN_STATION_ID")
    private Long originStationID;

    @JsonAlias("DESTINATION_STATION_ID")
    private Long destinationStationID;

    @JsonAlias("DEPARTURE_DATE")
    private Date departureDate;

    @JsonAlias("DEPARTURE_TIME")
    private Time departureTime;

}
