package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.timetables;

import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.batch.item.ItemWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimetablesWriter extends ItemStreamSupport implements ItemWriter<TimetableDto> {

    @Override
    public void open(ExecutionContext executionContext) {
        log.info("OPEN WRITTING");
        super.open(executionContext);
    }

    @Override
    public void close() {
        log.info("CLOSE WRITTING");
        super.close();
    }

    @Override
    public void update(ExecutionContext executionContext) {
        log.info("UPDATE WRITTING");
        super.update(executionContext);
    }

    @Override
    public void write(List<? extends TimetableDto> items) throws Exception {
        log.info("WRITING: " + items.toString());
    }

}
