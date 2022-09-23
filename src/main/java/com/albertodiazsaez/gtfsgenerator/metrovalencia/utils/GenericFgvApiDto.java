package com.albertodiazsaez.gtfsgenerator.metrovalencia.utils;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class GenericFgvApiDto {

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
