package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.sync.dto;

import java.sql.Date;

import com.albertodiazsaez.gtfsgenerator.metrovalencia.utils.GenericFgvApiDto;
import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FgvDateServicesDto extends GenericFgvApiDto {

    private Long id;

    @JsonAlias("servicio_id")
    private Long servicioId;

    private Date fecha;

}
