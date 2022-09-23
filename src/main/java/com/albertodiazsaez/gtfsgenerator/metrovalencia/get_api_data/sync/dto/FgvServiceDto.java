package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.sync.dto;

import java.sql.Date;

import com.albertodiazsaez.gtfsgenerator.metrovalencia.utils.GenericFgvApiDto;
import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FgvServiceDto extends GenericFgvApiDto {

    private Long id;

    @JsonAlias("servicio_id_FGV")
    private Long servicioIDFgv;

    @JsonAlias("fecha_inicio_validez")
    private Date fechaInicioValidez;

    @JsonAlias("fecha_fin_validez")
    private Date fechaFinValidez;

    private String descripcion;

    @JsonAlias("dias_semana")
    private String diasSemana;

}
