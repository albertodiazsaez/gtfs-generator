package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.services_lookup;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.sync.dto.FgvServiceDto;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ServicesLookupTasklet implements Tasklet {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        List<Integer> stationsIDs = getStationsIDs();
        List<FgvServiceDto> services = new ArrayList<>();

        // services.addAll(getServicesByWeekDay());
        // services.addAll(getServicesByDate());

        return null;
    }

    private List<Integer> getStationsIDs() {
        return jdbcTemplate.queryForList("SELECT ESTACION_ID_FGV FROM METROVALENCIA.FGV_STATIONS fs ;", Integer.class);

    }

    /**
     * Gets services by day
     * 
     * @return
     */
    private List<FgvServiceDto> getServicesByWeekDay() {
        // TODO Auto-generated method stub
        return null;
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
