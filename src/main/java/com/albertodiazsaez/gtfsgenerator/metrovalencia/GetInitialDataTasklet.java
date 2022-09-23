package com.albertodiazsaez.gtfsgenerator.metrovalencia;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetInitialDataTasklet implements Tasklet {

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		log.trace("A TRACE Message");
		log.debug("A DEBUG Message");
		log.info("An INFO Message");
		log.warn("A WARN Message");
		log.error("An ERROR Message");
		return RepeatStatus.FINISHED;
	}

}
