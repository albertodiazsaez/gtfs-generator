package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.stations;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.util.retry.Retry;

@Slf4j
@Component
public class GetMetrovalenciaStationsData implements Tasklet {

    private JdbcTemplate jdbcTemplate;

    private WebClient webClient;

    @Autowired
    public GetMetrovalenciaStationsData(JdbcTemplate jdbcTemplate, WebClient webClient) {
        this.jdbcTemplate = jdbcTemplate;
        this.webClient = webClient;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("Calling FGV API for Stations");
        try {

            WebClient.ResponseSpec responseSpec = webClient.get()
                    .uri("https://www.fgv.es/ap18/api/public/es/api/v1/V/estaciones").retrieve();

            FgvStationDto[] stationsList = responseSpec.bodyToMono(FgvStationDto[].class)
                    .retryWhen(Retry.backoff(10, Duration.ofSeconds(2))).block();
            log.info("Saving data in local DB");

            jdbcTemplate.batchUpdate("INSERT INTO METROVALENCIA.FGV_STATIONS\n"
                    + "(ID, ESTACION_ID_FGV, NOMBRE, TRANSBORDO, LATITUD, LONGITUD, DIRECCION, SEDE, CREATED_AT, UPDATED_AT, DELETED_AT)\n"
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", new BatchPreparedStatementSetter() {

                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {

                            FgvStationDto fgvStationDto = stationsList[i];
                            ps.setLong(1, fgvStationDto.getId());
                            ps.setLong(2, fgvStationDto.getEstacionIDFGV());
                            ps.setString(3,
                                    fgvStationDto.getNombre().replaceAll("\u0096", "-").replaceAll("\u0092", "'"));
                            ps.setLong(4, fgvStationDto.getTransbordo());
                            ps.setDouble(5, fgvStationDto.getLatitud());
                            ps.setDouble(6, fgvStationDto.getLongitud());
                            ps.setString(7,
                                    fgvStationDto.getDireccion().replaceAll("\u0096", "-").replaceAll("\u0092", "'"));
                            ps.setString(8, fgvStationDto.getSede());
                            ps.setString(9,
                                    fgvStationDto.getCreatedAt() != null ? fgvStationDto.getCreatedAt().toString()
                                            : "");
                            ps.setString(10,
                                    fgvStationDto.getUpdatedAt() != null ? fgvStationDto.getUpdatedAt().toString()
                                            : "");
                            ps.setString(11,
                                    fgvStationDto.getDeletedAt() != null ? fgvStationDto.getDeletedAt().toString()
                                            : "");

                        }

                        @Override
                        public int getBatchSize() {
                            return stationsList.length;
                        }
                    });
            return RepeatStatus.FINISHED;

        } catch (Exception e) {
            log.error("GetMetrovalenciaStationsData FAILED. Error: ", e.getMessage());
            throw e;
        }
    }

}
