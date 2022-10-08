//package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.services_lookup;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//import org.springframework.jdbc.core.RowMapper;
//
//public class ServicesLookupRowMapper implements RowMapper<ServicesLookupDto> {
//
//    @Override
//    public ServicesLookupDto mapRow(ResultSet rs, int rowNum) throws SQLException {
//
//        ServicesLookupDto result = new ServicesLookupDto();
//
//        result.setConsultedDate(rs.getDate("CONSULTED_DATE").toLocalDate());
//        result.setDataStored(rs.getBoolean("DATA_STORED"));
//        result.setJobInstanceID(rs.getLong("JOB_INSTANCE_ID"));
//        result.setServiceDate(rs.getDate("SERVICE_DATE").toLocalDate());
//        result.setServiceID(rs.getLong("SERVICE_ID"));
//        result.setStationID(rs.getLong("STATION_ID"));
//        result.setStationWebID(rs.getLong("STATION_WEB_ID"));
//
//        return result;
//    }
//
//}
