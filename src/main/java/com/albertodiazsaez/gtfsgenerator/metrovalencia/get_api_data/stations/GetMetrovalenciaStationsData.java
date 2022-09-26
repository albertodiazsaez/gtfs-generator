package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.stations;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

            stationsList = setWebID(stationsList);

            final FgvStationDto[] stationsToSave = stationsList;

            log.info("Saving data in local DB");

            jdbcTemplate.batchUpdate("INSERT INTO METROVALENCIA.FGV_STATIONS\n"
                    + "(ID, ESTACION_ID_FGV, NOMBRE, TRANSBORDO, LATITUD, LONGITUD, DIRECCION, SEDE, CREATED_AT, UPDATED_AT, DELETED_AT, WEB_ID)\n"
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", new BatchPreparedStatementSetter() {

                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {

                            FgvStationDto fgvStationDto = stationsToSave[i];
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
                            ps.setLong(12, fgvStationDto.getWebID());

                        }

                        @Override
                        public int getBatchSize() {
                            return stationsToSave.length;
                        }
                    });

            return RepeatStatus.FINISHED;

        } catch (Exception e) {
            log.error("GetMetrovalenciaStationsData FAILED. Error: ", e.getMessage());
            throw e;
        }
    }

    private FgvStationDto[] setWebID(FgvStationDto[] stationsList) {

        Map<String, Long> mapStationsWebIDNames = getMapStationsWebIDNames();

        for (FgvStationDto fgvStationDto : stationsList) {
            fgvStationDto.setWebID(mapStationsWebIDNames.get(removeStringDashes(fgvStationDto.getNombre())));
        }
        return stationsList;
    }

    private Map<String, Long> getMapStationsWebIDNames() {

        // todo
        Map<String, Long> mapStationsWebIDNames = new HashMap<>();

        WebClient.ResponseSpec responseSpec = webClient.get()
                .uri("https://www.metrovalencia.es/es/consulta-de-horarios-y-planificador/").retrieve();

        String stringResponseToken = responseSpec.bodyToMono(String.class).timeout(Duration.ofMinutes(1))
                .retryWhen(Retry.fixedDelay(10, Duration.ofSeconds(3))).block();

        // @formatter:on
        Document tokenDoc = Jsoup.parse(stringResponseToken);

        Elements tokenElements = tokenDoc.select(".single-input option");

        for (Element stationElement : tokenElements) {

            Boolean elementIsStation = stationElement.attr("value") != null
                    && stationElement.attr("value") != StringUtils.EMPTY;

            if (elementIsStation) {
                mapStationsWebIDNames.put(removeStringDashes(stationElement.text()),
                        Long.parseLong(stationElement.attr("value")));
            }

        }

        return mapStationsWebIDNames;
    }

    private String removeStringDashes(String inputString) {

        String dash = "-";
        String unicodeDash = "\\u0096";
        String doubleSpace = " +";

        // @formatter:off
        return inputString
                .replaceAll(unicodeDash, StringUtils.SPACE)
                .replaceAll(dash, StringUtils.SPACE)
                .replaceAll(doubleSpace, StringUtils.SPACE);
        // @formatter:on
    }

}
