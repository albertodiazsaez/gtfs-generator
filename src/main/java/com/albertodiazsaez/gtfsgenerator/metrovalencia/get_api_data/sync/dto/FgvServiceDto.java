//package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.sync.dto;
//
//import java.sql.Date;
//
//import com.albertodiazsaez.gtfsgenerator.metrovalencia.utils.GenericFgvApiDto;
//import com.fasterxml.jackson.annotation.JsonAlias;
//import com.fasterxml.jackson.annotation.JsonFormat;
//
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//
//@Data
//@EqualsAndHashCode(callSuper = true)
//public class FgvServiceDto extends GenericFgvApiDto {
//
//    private Long id;
//
//    @JsonAlias("servicio_id_FGV")
//    private Long servicioIDFgv;
//
//    @JsonAlias("fecha_inicio_validez")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", locale = "es_ES")
//    private Date fechaInicioValidez;
//
//    @JsonAlias("fecha_fin_validez")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", locale = "es_ES")
//    private Date fechaFinValidez;
//
//    private String descripcion;
//
//    @JsonAlias("dias_semana")
//    private String diasSemana;
//
//}
