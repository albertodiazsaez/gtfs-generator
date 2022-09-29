package com.albertodiazsaez.gtfsgenerator.metrovalencia;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.lines.GetMetrovalenciaLinesData;
import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.stations.GetMetrovalenciaStationsData;
import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.sync.GetMetrovalenciaSyncData;
import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.services_lookup.ServicesLookupTasklet;
import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.timetables.TimetableDto;
import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.timetables.TimetablesItemReader;
import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.timetables.TimetablesWriter;

@Configuration
@EnableBatchProcessing
public class MetrovalenciaConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public JdbcTemplate jdbcTemplate;

    @Autowired
    public NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public WebClient webClient;

    @Autowired
    public WebClient.Builder webClientBuilder;

    // @formatter:off
    @Bean
    public Job generateGTFSMetrovalencia() {
        return this.jobBuilderFactory
                .get("generateGTFSMetrovalencia")
                .incrementer(new RunIdIncrementer())
                .start(getMetrovalenciaServicesLookup())
//                .start(getMetrovalenciaAPIStations())
//                .next(getMetrovalenciaAPILines())
//                .next(getMetrovalenciaAPISync())
//                .next(getMetrovalenciaServicesLookup())
//                .next(getMetrovalenciaTimetables())
                .build();
    }

    @Bean
    public Step getMetrovalenciaAPIStations() {
        return this.stepBuilderFactory.get("getMetrovalenciaAPIStations")
                .tasklet(new GetMetrovalenciaStationsData(jdbcTemplate, webClient))
                .build();
    }

    @Bean
    public Step getMetrovalenciaAPILines() {
        return this.stepBuilderFactory.get("getMetrovalenciaAPILines")
                .tasklet(new GetMetrovalenciaLinesData(jdbcTemplate, webClient))
                .build();
    }
    
    @Bean
    public Step getMetrovalenciaAPISync() {
        return this.stepBuilderFactory.get("getMetrovalenciaAPISync")
                .tasklet(new GetMetrovalenciaSyncData(jdbcTemplate, webClientBuilder))
                .build();
    }
    
    @Bean
    public Step getMetrovalenciaTimetables() {
        return this.stepBuilderFactory.get("getMetrovalenciaTimetables")
                .<TimetableDto, TimetableDto>chunk(1)
                .reader(new TimetablesItemReader(webClientBuilder))
                .writer(new TimetablesWriter())
                .build();
    }
    
    @Bean
    public Step getMetrovalenciaServicesLookup() {
        return this.stepBuilderFactory.get("getMetrovalenciaServicesLookup")
                .tasklet(new ServicesLookupTasklet(jdbcTemplate, namedJdbcTemplate))
                .build();
    }

    // @formatter:on
}
