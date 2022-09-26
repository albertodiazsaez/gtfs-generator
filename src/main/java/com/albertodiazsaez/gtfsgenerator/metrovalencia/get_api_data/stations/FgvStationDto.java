package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.stations;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class FgvStationDto {

    private Long id;
    @JsonAlias("estacion_id_FGV")
    private Long estacionIDFGV;

    @JsonAlias("web_id")
    private Long webID;

    private String nombre;

    private Long transbordo;

    private Double latitud;

    private Double longitud;

    private String direccion;

    private String sede;

    @JsonAlias("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "es_ES")
    private LocalDateTime createdAt;

    @JsonAlias("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "es_ES")
    private LocalDateTime updatedAt;

    @JsonAlias("deleted_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "es_ES")
    private LocalDateTime deletedAt;

}
