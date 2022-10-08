package com.albertodiazsaez.gtfsgenerator.metrovalencia;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.lines.GetMetrovalenciaLinesData;
import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.stations.GetMetrovalenciaStationsData;
import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_api_data.sync.GetMetrovalenciaSyncData;
import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.services_lookup.ServicesLookupDto;
import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.services_lookup.ServicesLookupTasklet;
import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.timetables.TimetableDto;
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
    public DataSource dataSource;

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
//                .start(getMetrovalenciaTimetables())
                .start(getMetrovalenciaAPIStations())
                .next(getMetrovalenciaAPILines())
                .next(getMetrovalenciaAPISync())
                .next(getMetrovalenciaServicesLookup())
  //              .next(getMetrovalenciaTimetables())
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
                .<ServicesLookupDto, List<TimetableDto>>chunk(10)
                .reader(servicesLookupItemReader(dataSource, null))
                .writer(new TimetablesWriter())
                .build();
    }
    
    @Bean
    @StepScope
    public JdbcPagingItemReader<ServicesLookupDto> servicesLookupItemReader(DataSource dataSource, PagingQueryProvider queryProvider) {
        return new JdbcPagingItemReaderBuilder<ServicesLookupDto>()
                .name("servicesLookupItemReader")
                .dataSource(dataSource)
                .queryProvider(queryProvider)
                //.rowMapper(new ServicesLookupRowMapper())
                .build();
    }

//    @Bean
//    public PagingQueryProvider servicesLookupPagingQueryProvider(DataSource dataSource) {
//        
//        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
//        
//        factoryBean.setDataSource(dataSource);
//        factoryBean.setSelectClause("SELECT *");
//        factoryBean.setFromClause("FROM METROVALENCIA.BATCH_SERVICES_LOOKUP ");
//        factoryBean.setSortKey("SERVICE_DATE");
//        
//        try {
//            return factoryBean.getObject();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    @Bean
    public Step getMetrovalenciaServicesLookup() {
        return this.stepBuilderFactory.get("getMetrovalenciaServicesLookup")
                .tasklet(new ServicesLookupTasklet(jdbcTemplate, namedJdbcTemplate)).build();
    }

    // @formatter:on
}
