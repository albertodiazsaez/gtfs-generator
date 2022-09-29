package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.services_lookup;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class ServicesLookupDto {

    @JsonAlias("SERVICE_DATE")
    private Date serviceDate;

    @JsonAlias("DATA_STORED")
    private Boolean dataStored;

    @JsonAlias("JOB_INSTANCE_ID")
    private Long jobInstanceID;

    @JsonAlias("STATION_ID")
    private Long stationID;

    @JsonAlias("STATION_WEB_ID")
    private Long stationWebID;

}
