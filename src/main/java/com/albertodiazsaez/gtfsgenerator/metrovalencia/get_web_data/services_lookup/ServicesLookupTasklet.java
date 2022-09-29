package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.services_lookup;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.stations.FgvStationDto;
import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.sync.dto.FgvDateServicesDto;
import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.sync.dto.FgvServiceDto;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ServicesLookupTasklet implements Tasklet {

    private JdbcTemplate jdbcTemplate;

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        Long jobId = chunkContext.getStepContext().getJobInstanceId();

        Map<FgvServiceDto, LocalDate> servicesToCheck = new HashMap<>();

        List<FgvStationDto> stations = getStations();
        List<FgvServiceDto> services = getServices();
        List<FgvDateServicesDto> dateServices = getDateServices();

        servicesToCheck.putAll(getServicesByWeekDay(services, dateServices));

        // services.addAll(getServicesByDate());

        return null;
    }

    private List<FgvDateServicesDto> getDateServices() {

        LocalDate lastDayOfNextYear = LocalDate.now().with(TemporalAdjusters.lastDayOfYear()).plusYears(1);
        LocalDate currentDate = LocalDate.now();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("lastDayOfNextYear", lastDayOfNextYear.toString());
        params.addValue("currentDate", currentDate);

        return namedJdbcTemplate.query(
                "SELECT ID, SERVICIO_ID as servicioId, FECHA, SEDE FROM METROVALENCIA.FGV_DATE_SERVICES fds WHERE FECHA >= :currentDate AND FECHA < :lastDayOfNextYear;",
                params, new BeanPropertyRowMapper<FgvDateServicesDto>(FgvDateServicesDto.class));
    }

    private Map<FgvServiceDto, LocalDate> getServicesByWeekDay(List<FgvServiceDto> services,
            List<FgvDateServicesDto> dateServices) {
        List<FgvServiceDto> servicesWithWeekDay = services.stream().filter(s -> s.getDiasSemana() != StringUtils.EMPTY)
                .collect(Collectors.toList());

        // TODO Seguir aquí

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEEE", new Locale("es", "ES"));
        TemporalAccessor accessor = formatter.parse("L");
        log.error(DayOfWeek.from(accessor).toString());

        return new HashMap<>();
    }

    private List<FgvStationDto> getStations() {
        return jdbcTemplate.query(
                "SELECT ID, ESTACION_ID_FGV as estacionIDFGV, WEB_ID as webID, NOMBRE FROM METROVALENCIA.FGV_STATIONS fs ;",
                new BeanPropertyRowMapper<FgvStationDto>(FgvStationDto.class));
    }

    /**
     * Gets services by day
     * 
     * @return
     */
    private List<FgvServiceDto> getServices() {

        LocalDate lastDayOfNextYear = LocalDate.now().with(TemporalAdjusters.lastDayOfYear()).plusYears(1);
        LocalDate currentDate = LocalDate.now();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("lastDayOfNextYear", lastDayOfNextYear.toString());
        params.addValue("currentDate", currentDate);

        return namedJdbcTemplate.query(
                "SELECT ID, SERVICIO_ID_FGV as servicioIDFgv, FECHA_INICIO_VALIDEZ as fechaInicioValidez, FECHA_FIN_VALIDEZ as fechaFinValidez, DESCRIPCION, DIAS_SEMANA as diasSemana FROM METROVALENCIA.FGV_SERVICES fs WHERE FECHA_INICIO_VALIDEZ < :lastDayOfNextYear AND FECHA_FIN_VALIDEZ >= :currentDate;",
                params, new BeanPropertyRowMapper<FgvServiceDto>(FgvServiceDto.class));
    }

    /**
     * Gets Services filtered by next year.
     * 
     * @return
     */
    private List<FgvServiceDto> getServicesByDate() {
        // TODO Auto-generated method stub
        return null;
    }

}
