//package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.services_lookup;
//
//import java.sql.Date;
//import java.time.LocalDate;
//
//import com.fasterxml.jackson.annotation.JsonAlias;
//import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//
//import lombok.Data;
//
//@Data
//public class ServicesLookupDto {
//
//    @JsonAlias("SERVICE_DATE")
//    @JsonSerialize(as = Date.class)
//    private LocalDate serviceDate;
//
//    @JsonAlias("DATA_STORED")
//    private Boolean dataStored;
//
//    @JsonAlias("JOB_INSTANCE_ID")
//    private Long jobInstanceID;
//
//    @JsonAlias("STATION_ID")
//    private Long stationID;
//
//    @JsonAlias("STATION_WEB_ID")
//    private Long stationWebID;
//
//    @JsonAlias("CONSULTED_DATE")
//    @JsonSerialize(as = Date.class)
//    private LocalDate consultedDate;
//
//    @JsonAlias("SERVICE_ID")
//    private Long serviceID;
//
//}
