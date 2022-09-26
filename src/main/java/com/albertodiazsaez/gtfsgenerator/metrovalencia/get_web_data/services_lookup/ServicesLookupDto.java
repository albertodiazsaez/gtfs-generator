package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.services_lookup;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class ServicesLookupDto {

    @JsonAlias("SERVICE_DATE")
    private Date serviceDate;

    @JsonAlias("LINE_ID")
    private Long lineID;

    @JsonAlias("JOB_INSTANCE_ID")
    private Long jobInstanceID;

    @JsonAlias("DATA_STORED")
    private Boolean dataStored;

}
