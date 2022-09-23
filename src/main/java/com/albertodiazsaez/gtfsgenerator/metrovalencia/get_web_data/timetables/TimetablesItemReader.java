package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.timetables;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.util.retry.Retry;

@Slf4j
@Component
public class TimetablesItemReader extends ItemStreamSupport implements ItemReader<TimetableDto> {

    @Autowired
    private WebClient.Builder webClientBuilder;

    List<TimetableDto> itemsToRead;

    String metrovalenciaToken;

    public TimetablesItemReader(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
        itemsToRead = new ArrayList<>();
        for (Long i = 0L; i < 3L; i++) {
            TimetableDto timetableDto = new TimetableDto();
            timetableDto.setOriginStationID(i);
            itemsToRead.add(timetableDto);
        }

    }

    @Override
    public void open(ExecutionContext executionContext) {
        metrovalenciaToken = getMetrovalenciaToken();
        log.info("OPEN READING TOKEN: " + metrovalenciaToken);
        super.open(executionContext);
    }

    @Override
    public void close() {
        log.info("CLOSE READING");
        super.close();
    }

    @Override
    public void update(ExecutionContext executionContext) {
        log.info("UPDATE READING");
        super.update(executionContext);
    }

    @Override
    public TimetableDto read()
            throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        if (itemsToRead.isEmpty()) {
            log.info("NO ITEMS LEFT TO READ");
            return null;
        } else {
            TimetableDto itemToRead = itemsToRead.remove(0);
            log.info("READING: " + itemToRead.toString());
            return itemToRead;
        }
    }

    private String getMetrovalenciaToken() {

        String token = StringUtils.EMPTY;

        // @formatter:off
 

        WebClient.ResponseSpec responseSpec = webClientBuilder.build().get()
                .uri("https://www.metrovalencia.es/es/consulta-de-horarios-y-planificador/").retrieve();

        String stringResponseToken = responseSpec
                .bodyToMono(String.class)
                .timeout(Duration.ofMinutes(1))
                .retryWhen(Retry.fixedDelay(10, Duration.ofSeconds(3)))
                .block();
        
        // @formatter:on
        Document tokenDoc = Jsoup.parse(stringResponseToken);

        Elements tokenElements = tokenDoc.select(".pr15 > input:nth-child(1)");

        token = tokenElements.get(0).getElementsByAttribute("value").toString();

        return token;

    }

}
