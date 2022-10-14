//package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.sync.dto;
//
//import java.time.LocalDateTime;
//
//import com.fasterxml.jackson.annotation.JsonAlias;
//import com.fasterxml.jackson.annotation.JsonFormat;
//
//import lombok.Data;
//
//@Data
//public class FgvNextStationsDto {
//
//    private long id;
//
//    @JsonAlias("servicio_id")
//    private long servicioID;
//
//    @JsonAlias("estacion_id")
//    private long estacionID;
//
//    @JsonAlias("estacion_siguiente_id")
//    private long estacionSiguienteID;
//
//    @JsonAlias("distancia_siguiente")
//    private long distanciaSiguiente;
//
//    private long coste;
//
//    private String sede;
//
//    @JsonAlias("created_at")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "es_ES")
//    private LocalDateTime createdAt;
//
//    @JsonAlias("updated_at")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "es_ES")
//    private LocalDateTime updatedAt;
//
//    @JsonAlias("deleted_at")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "es_ES")
//    private LocalDateTime deletedAt;
//
//}
