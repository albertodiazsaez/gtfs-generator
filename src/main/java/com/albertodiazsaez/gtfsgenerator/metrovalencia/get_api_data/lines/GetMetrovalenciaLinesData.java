package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.lines;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Arrays;

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
public class GetMetrovalenciaLinesData implements Tasklet {

    private JdbcTemplate jdbcTemplate;
    private WebClient webClient;

    @Autowired
    public GetMetrovalenciaLinesData(JdbcTemplate jdbcTemplate, WebClient webClient) {
        this.jdbcTemplate = jdbcTemplate;
        this.webClient = webClient;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        try {

            log.info("Calling FGV API for Lines");

            WebClient.ResponseSpec responseSpec = webClient.get()
                    .uri("https://www.fgv.es/ap18/api/public/es/api/v1/V/lineas").retrieve();

            FgvLineDto[] linesList = responseSpec.bodyToMono(FgvLineDto[].class)
                    .retryWhen(Retry.backoff(10, Duration.ofSeconds(2))).block();
            log.info("Saving Lines in local DB");

            jdbcTemplate.batchUpdate("INSERT INTO METROVALENCIA.FGV_LINES\n"
                    + "(ID, LINEA_ID_FGV, NOMBRE_CORTO, NOMBRE_LARGO, TIPO, COLOR, FORMA_ID, SEDE, CREATED_AT, UPDATED_AT, DELETED_AT)\n"
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);\n", new BatchPreparedStatementSetter() {

                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {

                            FgvLineDto fgvLineDto = linesList[i];
                            ps.setLong(1, fgvLineDto.getId());
                            ps.setLong(2, fgvLineDto.getLineaIDFGV());
                            ps.setString(3,
                                    fgvLineDto.getNombreCorto().replaceAll("\u0096", "-").replaceAll("\u0092", "'"));
                            ps.setString(4,
                                    fgvLineDto.getNombreLargo().replaceAll("\u0096", "-").replaceAll("\u0092", "'"));
                            ps.setString(5, fgvLineDto.getTipo());
                            ps.setString(6, fgvLineDto.getColor());
                            ps.setLong(7, fgvLineDto.getFormaID());
                            ps.setString(8, fgvLineDto.getSede());
                            ps.setString(9,
                                    fgvLineDto.getCreatedAt() != null ? fgvLineDto.getCreatedAt().toString() : "");
                            ps.setString(10,
                                    fgvLineDto.getUpdatedAt() != null ? fgvLineDto.getUpdatedAt().toString() : "");
                            ps.setString(11,
                                    fgvLineDto.getDeletedAt() != null ? fgvLineDto.getDeletedAt().toString() : "");

                        }

                        @Override
                        public int getBatchSize() {
                            return linesList.length;
                        }
                    });

            log.info("Saving Station-Lines in local DB");

            for (FgvLineDto fgvLineDto : linesList) {

                final FgvLineDto currentFgvLine = fgvLineDto;

                int[] stopsList = Arrays.stream(currentFgvLine.getStops().split(",")).mapToInt(Integer::parseInt)
                        .toArray();

                jdbcTemplate.batchUpdate("INSERT INTO METROVALENCIA.FGV_LINES_STATIONS\n" + "(ID_LINE, ID_STATION)\n"
                        + "VALUES(?, ?);\n", new BatchPreparedStatementSetter() {

                            @Override
                            public void setValues(PreparedStatement ps, int j) throws SQLException {

                                FgvLineDto fgvLineDto = currentFgvLine;
                                ps.setLong(1, fgvLineDto.getId());
                                ps.setLong(2, stopsList[j]);

                            }

                            @Override
                            public int getBatchSize() {
                                return stopsList.length;
                            }
                        });

            }
            return RepeatStatus.FINISHED;

        } catch (Exception e) {
            log.error("GetMetrovalenciaLinesData FAILED. Error: ", e.getMessage());
            throw e;
        }
    }

}
