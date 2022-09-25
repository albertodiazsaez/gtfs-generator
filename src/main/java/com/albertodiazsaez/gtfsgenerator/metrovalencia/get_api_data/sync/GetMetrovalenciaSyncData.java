package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.sync;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.sync.dto.FgvDateServicesDto;
import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.sync.dto.FgvNextStationsDto;
import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.sync.dto.FgvServiceDto;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.util.retry.Retry;

@Slf4j
@Component
@AllArgsConstructor
public class GetMetrovalenciaSyncData implements Tasklet {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        try {

            WebClient.ResponseSpec responseSpec = webClientBuilder.build().get()
                    .uri("https://www.fgv.es/ap18/api/public/es/api/v1/V/sincronizacion").retrieve();

            // @formatter:off
            String stringResponseNextStations = responseSpec
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMinutes(1))
                    .retryWhen(Retry.fixedDelay(10, Duration.ofSeconds(3)))
                    .block();
            // @formatter:on
            JSONObject jsonResponse = new JSONObject(stringResponseNextStations);
            JSONObject jsonObjectSyncData = jsonResponse.getJSONObject("data");

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();

            getServicesData(objectMapper, jsonObjectSyncData.getJSONArray("servicios"));
            getDateServicesData(objectMapper, jsonObjectSyncData.getJSONArray("fechas_servicios"));
            getNextStationData(objectMapper, jsonObjectSyncData.getJSONArray("estaciones_siguientes"));

            return RepeatStatus.FINISHED;

        } catch (Exception e) {

            log.error("GetMetrovalenciaNextStationsData FAILED. Error: ", e.getMessage());
            throw e;

        }
    }

    private void getNextStationData(ObjectMapper objectMapper, JSONArray jsonArrayNextStations)
            throws IOException, StreamReadException, DatabindException {
        ArrayList<FgvNextStationsDto> listNextStations = objectMapper.readValue(jsonArrayNextStations.toString(),
                new TypeReference<ArrayList<FgvNextStationsDto>>() {
                });

        jdbcTemplate.batchUpdate("INSERT INTO METROVALENCIA.FGV_NEXT_STATIONS "
                + "(ID, SERVICIO_ID, ESTACION_ID, ESTACION_SIGUIENTE_ID, DISTANCIA_SIGUIENTE, COSTE, SEDE, CREATED_AT, UPDATED_AT, DELETED_AT) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {

                        FgvNextStationsDto fgvNextStation = listNextStations.get(i);
                        ps.setLong(1, fgvNextStation.getId());
                        ps.setLong(2, fgvNextStation.getServicioID());
                        ps.setLong(3, fgvNextStation.getEstacionID());
                        ps.setLong(4, fgvNextStation.getEstacionSiguienteID());
                        ps.setLong(5, fgvNextStation.getDistanciaSiguiente());
                        ps.setLong(6, fgvNextStation.getCoste());
                        ps.setString(7, fgvNextStation.getSede());
                        ps.setString(8,
                                fgvNextStation.getCreatedAt() != null ? fgvNextStation.getCreatedAt().toString() : "");
                        ps.setString(9,
                                fgvNextStation.getUpdatedAt() != null ? fgvNextStation.getUpdatedAt().toString() : "");
                        ps.setString(10,
                                fgvNextStation.getDeletedAt() != null ? fgvNextStation.getDeletedAt().toString() : "");

                    }

                    @Override
                    public int getBatchSize() {
                        return listNextStations.size();
                    }
                });
    }

    private void getServicesData(ObjectMapper objectMapper, JSONArray jsonArrayServicesData)
            throws IOException, StreamReadException, DatabindException {
        ArrayList<FgvServiceDto> listServices = objectMapper.readValue(jsonArrayServicesData.toString(),
                new TypeReference<ArrayList<FgvServiceDto>>() {
                });

        jdbcTemplate.batchUpdate(
                "INSERT INTO METROVALENCIA.FGV_SERVICES (ID, SERVICIO_ID_FGV, FECHA_INICIO_VALIDEZ, FECHA_FIN_VALIDEZ, DESCRIPCION, DIAS_SEMANA, SEDE, CREATED_AT, UPDATED_AT, DELETED_AT) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {

                        FgvServiceDto fgvService = listServices.get(i);
                        ps.setLong(1, fgvService.getId());
                        ps.setLong(2, fgvService.getServicioIDFgv());
                        ps.setDate(3, fgvService.getFechaInicioValidez());
                        ps.setDate(4, fgvService.getFechaFinValidez());
                        ps.setString(5,
                                fgvService.getDescripcion().replaceAll("\u0096", "-").replaceAll("\u0092", "'"));
                        ps.setString(6, fgvService.getDiasSemana().replaceAll("\u0096", "-").replaceAll("\u0092", "'"));
                        ps.setString(7, fgvService.getSede());
                        ps.setString(8, fgvService.getCreatedAt() != null ? fgvService.getCreatedAt().toString() : "");
                        ps.setString(9, fgvService.getUpdatedAt() != null ? fgvService.getUpdatedAt().toString() : "");
                        ps.setString(10, fgvService.getDeletedAt() != null ? fgvService.getDeletedAt().toString() : "");

                    }

                    @Override
                    public int getBatchSize() {
                        return listServices.size();
                    }
                });
    }

    private void getDateServicesData(ObjectMapper objectMapper, JSONArray jsonArrayDateServices)
            throws IOException, StreamReadException, DatabindException {
        ArrayList<FgvDateServicesDto> listDateServices = objectMapper.readValue(jsonArrayDateServices.toString(),
                new TypeReference<ArrayList<FgvDateServicesDto>>() {
                });

        jdbcTemplate.batchUpdate(
                "INSERT INTO METROVALENCIA.FGV_DATE_SERVICES (ID, SERVICIO_ID, FECHA, SEDE, CREATED_AT, UPDATED_AT, DELETED_AT) VALUES(?, ?, ?, ?, ?, ?, ?);",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {

                        FgvDateServicesDto fgvDateService = listDateServices.get(i);
                        ps.setLong(1, fgvDateService.getId());
                        ps.setLong(2, fgvDateService.getServicioId());
                        ps.setDate(3, fgvDateService.getFecha());
                        ps.setString(4, fgvDateService.getSede());
                        ps.setString(5,
                                fgvDateService.getCreatedAt() != null ? fgvDateService.getCreatedAt().toString() : "");
                        ps.setString(6,
                                fgvDateService.getUpdatedAt() != null ? fgvDateService.getUpdatedAt().toString() : "");
                        ps.setString(7,
                                fgvDateService.getDeletedAt() != null ? fgvDateService.getDeletedAt().toString() : "");

                    }

                    @Override
                    public int getBatchSize() {
                        return listDateServices.size();
                    }
                });
    }

}
