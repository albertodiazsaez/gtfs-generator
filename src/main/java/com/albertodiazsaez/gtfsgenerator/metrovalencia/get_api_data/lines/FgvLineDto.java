package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.lines;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class FgvLineDto {

    private Long id;

    @JsonAlias("linea_id_FGV")
    private Long lineaIDFGV;

    @JsonAlias("nombre_corto")
    private String nombreCorto;

    @JsonAlias("nombre_largo")
    private String nombreLargo;

    private String tipo;

    private String color;

    @JsonAlias("forma_id")
    private Integer formaID;

    private String stops;

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
