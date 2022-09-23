package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.timetables;

import java.sql.Date;
import java.sql.Time;

import lombok.Data;

@Data
public class TimetableDto {

    private Long originStationID;

    private Long destinationStationID;

    private Date departureDate;

    private Time departureTime;

}
