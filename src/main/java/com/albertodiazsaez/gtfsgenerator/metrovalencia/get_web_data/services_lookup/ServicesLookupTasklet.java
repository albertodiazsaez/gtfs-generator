//package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.services_lookup;
//
//import java.sql.Date;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.time.DayOfWeek;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.time.temporal.TemporalAdjusters;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.batch.core.StepContribution;
//import org.springframework.batch.core.scope.context.ChunkContext;
//import org.springframework.batch.core.step.tasklet.Tasklet;
//import org.springframework.batch.repeat.RepeatStatus;
//import org.springframework.jdbc.core.BatchPreparedStatementSetter;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.stereotype.Component;
//
//import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.stations.FgvStationDto;
//import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.sync.dto.FgvDateServicesDto;
//import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.sync.dto.FgvServiceDto;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Component
//@AllArgsConstructor
//public class ServicesLookupTasklet implements Tasklet {
//
//    private JdbcTemplate jdbcTemplate;
//
//    private NamedParameterJdbcTemplate namedJdbcTemplate;
//
//    @Override
//    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
//
//        Long jobId = chunkContext.getStepContext().getJobInstanceId();
//
//        Map<FgvServiceDto, LocalDate> servicesToCheck = new HashMap<>();
//
//        List<FgvStationDto> stations = getStations();
//        List<FgvServiceDto> services = getServices();
//        List<FgvDateServicesDto> dateServices = getDateServices();
//
//        LocalDate todayDate = LocalDate.now();
//
//        log.info("Getting Services by Week Day");
//        servicesToCheck.putAll(getServicesByWeekDay(services, dateServices, todayDate));
//
//        log.info("Getting Services by Date");
//        servicesToCheck.putAll(getServicesByDate(services, dateServices, todayDate));
//
//        List<ServicesLookupDto> result = getServicesLookupFromServicesAndStations(servicesToCheck, stations, jobId,
//                todayDate);
//
//        jdbcTemplate.batchUpdate("INSERT INTO METROVALENCIA.BATCH_SERVICES_LOOKUP\n"
//                + "(SERVICE_DATE, DATA_STORED, JOB_INSTANCE_ID, STATION_ID, STATION_WEB_ID, CONSULTED_DATE, SERVICE_ID)\n"
//                + "VALUES(?, ?, ?, ?, ?, ?, ?);", new BatchPreparedStatementSetter() {
//
//                    @Override
//                    public void setValues(PreparedStatement ps, int i) throws SQLException {
//                        ServicesLookupDto serviceLookupToInsert = result.get(i);
//
//                        ps.setDate(1, Date.valueOf(serviceLookupToInsert.getServiceDate()));
//                        ps.setBoolean(2, serviceLookupToInsert.getDataStored());
//                        ps.setLong(3, serviceLookupToInsert.getJobInstanceID());
//                        ps.setLong(4, serviceLookupToInsert.getStationID());
//                        ps.setLong(5, serviceLookupToInsert.getStationWebID());
//                        ps.setDate(6, Date.valueOf(serviceLookupToInsert.getConsultedDate()));
//                        ps.setLong(7, serviceLookupToInsert.getServiceID());
//
//                    }
//
//                    @Override
//                    public int getBatchSize() {
//                        return result.size();
//                    }
//                });
//
//        return RepeatStatus.FINISHED;
//    }
//
//    private List<ServicesLookupDto> getServicesLookupFromServicesAndStations(
//            Map<FgvServiceDto, LocalDate> servicesDateMap, List<FgvStationDto> stations, Long jobId,
//            LocalDate todayDate) {
//
//        List<ServicesLookupDto> result = new ArrayList<>();
//
//        for (FgvStationDto station : stations) {
//            for (Map.Entry<FgvServiceDto, LocalDate> entry : servicesDateMap.entrySet()) {
//
//                ServicesLookupDto serviceLookup = new ServicesLookupDto();
//
//                serviceLookup.setDataStored(false);
//                serviceLookup.setJobInstanceID(jobId);
//                serviceLookup.setServiceDate(entry.getValue());
//                serviceLookup.setStationID(station.getEstacionIDFGV());
//                serviceLookup.setStationWebID(station.getWebID());
//                serviceLookup.setConsultedDate(todayDate);
//                serviceLookup.setServiceID(entry.getKey().getId());
//
//                result.add(serviceLookup);
//
//            }
//        }
//
//        return result;
//
//    }
//
//    private List<FgvDateServicesDto> getDateServices() {
//
//        LocalDate lastDayOfNextYear = LocalDate.now().with(TemporalAdjusters.lastDayOfYear()).plusYears(1);
//        LocalDate currentDate = LocalDate.now();
//
//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("lastDayOfNextYear", lastDayOfNextYear.toString());
//        params.addValue("currentDate", currentDate);
//
//        return namedJdbcTemplate.query(
//                "SELECT ID, SERVICIO_ID as servicioId, FECHA, SEDE FROM METROVALENCIA.FGV_DATE_SERVICES fds WHERE FECHA >= :currentDate AND FECHA < :lastDayOfNextYear;",
//                params, new BeanPropertyRowMapper<FgvDateServicesDto>(FgvDateServicesDto.class));
//    }
//
//    private Map<FgvServiceDto, LocalDate> getServicesByWeekDay(List<FgvServiceDto> services,
//            List<FgvDateServicesDto> dateServices, LocalDate todayDate) {
//
//        Map<FgvServiceDto, LocalDate> result = new HashMap<>();
//
//        List<FgvServiceDto> servicesWithWeekDay = services.stream().filter(s -> s.getDiasSemana() != StringUtils.EMPTY)
//                .collect(Collectors.toList());
//
//        DateTimeFormatter shortWeekDateFormatter = DateTimeFormatter.ofPattern("EEEEE", new Locale("es", "ES"));
//
//        for (FgvServiceDto service : servicesWithWeekDay) {
//            String[] weekDaysShortNames = service.getDiasSemana().split(", ");
//
//            LocalDate serviceLookupDate = null;
//            LocalDate possibleDate = null;
//
//            while (serviceLookupDate == null) {
//
//                for (String weekDate : weekDaysShortNames) {
//
//                    DayOfWeek dayOfWeek = DayOfWeek.from(shortWeekDateFormatter.parse(weekDate));
//
//                    if (possibleDate == null) {
//                        possibleDate = todayDate.with(TemporalAdjusters.nextOrSame(dayOfWeek));
//                    }
//
//                    final String possibleDateString = possibleDate.toString();
//                    List<FgvDateServicesDto> dateServicesInDay = dateServices.stream()
//                            .filter(ds -> ds.getFecha().toString().equals(possibleDateString)).toList();
//
//                    if (dateServicesInDay.isEmpty()) {
//                        serviceLookupDate = possibleDate;
//                        break;
//                    }
//
//                    possibleDate = possibleDate.plusDays(7);
//
//                }
//
//            }
//
//            result.put(service, serviceLookupDate);
//
//        }
//
//        return result;
//    }
//
//    private List<FgvStationDto> getStations() {
//        return jdbcTemplate.query(
//                "SELECT ID, ESTACION_ID_FGV as estacionIDFGV, WEB_ID as webID, NOMBRE FROM METROVALENCIA.FGV_STATIONS fs ;",
//                new BeanPropertyRowMapper<FgvStationDto>(FgvStationDto.class));
//    }
//
//    /**
//     * Gets services by day
//     * 
//     * @return
//     */
//    private List<FgvServiceDto> getServices() {
//
//        LocalDate lastDayOfNextYear = LocalDate.now().with(TemporalAdjusters.lastDayOfYear()).plusYears(1);
//        LocalDate currentDate = LocalDate.now();
//
//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("lastDayOfNextYear", lastDayOfNextYear.toString());
//        params.addValue("currentDate", currentDate);
//
//        return namedJdbcTemplate.query(
//                "SELECT ID, SERVICIO_ID_FGV as servicioIDFgv, FECHA_INICIO_VALIDEZ as fechaInicioValidez, FECHA_FIN_VALIDEZ as fechaFinValidez, DESCRIPCION, DIAS_SEMANA as diasSemana FROM METROVALENCIA.FGV_SERVICES fs WHERE FECHA_INICIO_VALIDEZ < :lastDayOfNextYear AND FECHA_FIN_VALIDEZ >= :currentDate;",
//                params, new BeanPropertyRowMapper<FgvServiceDto>(FgvServiceDto.class));
//    }
//
//    /**
//     * Gets Services filtered by next year.
//     * 
//     * @param dateServices
//     * @param services
//     * @param todayDate
//     * 
//     * @return
//     */
//    private Map<FgvServiceDto, LocalDate> getServicesByDate(List<FgvServiceDto> services,
//            List<FgvDateServicesDto> dateServices, LocalDate todayDate) {
//
//        Map<FgvServiceDto, LocalDate> result = new HashMap<>();
//
//        List<FgvServiceDto> servicesToLookup = services.stream().filter(s -> s.getDiasSemana().equals(StringUtils.EMPTY)
//                && s.getFechaFinValidez().after(Date.valueOf(todayDate))).toList();
//
//        for (FgvServiceDto service : servicesToLookup) {
//
//            List<FgvDateServicesDto> dateServicesFromService = dateServices.stream()
//                    .filter(ds -> ds.getServicioId().equals(service.getId())).toList();
//            Boolean serviceHasDate = !dateServicesFromService.isEmpty();
//
//            if (serviceHasDate) {
//                result.put(service, dateServicesFromService.get(0).getFecha().toLocalDate());
//            }
//
//        }
//
//        return result;
//    }
//
//}
