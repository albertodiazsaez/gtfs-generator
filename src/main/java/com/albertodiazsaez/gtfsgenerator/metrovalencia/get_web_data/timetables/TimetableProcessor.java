package com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.timetables;

import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamSupport;

import com.albertodiazsaez.gtfsgenerator.metrovalencia.get_web_data.services_lookup.ServicesLookupDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimetableProcessor extends ItemStreamSupport
        implements ItemProcessor<ServicesLookupDto, List<TimetableDto>> {

    @Override
    public List<TimetableDto> process(ServicesLookupDto item) throws Exception {
        log.error("ITEM: " + item.toString());
        return null;
    }

}
