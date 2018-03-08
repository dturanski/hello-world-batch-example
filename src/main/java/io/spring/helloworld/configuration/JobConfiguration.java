package io.spring.helloworld.configuration;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.task.configuration.DefaultTaskConfigurer;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.cloud.task.configuration.TaskConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author David Turanski
 **/

@Configuration
@EnableBatchProcessing
@EnableTask
public class JobConfiguration {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public BatchConfigurer batchConfigurer(DataSource dataSource) {
		return new DefaultBatchConfigurer(dataSource);
	}

	@Bean
	public TaskConfigurer taskConfigurer(DataSource dataSource) {
		return new DefaultTaskConfigurer(dataSource);
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").tasklet((stepContribution, chunkContext) -> {
			System.out.println("Hello, World");
			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2").tasklet((stepContribution, chunkContext) -> {
			System.out.println("Running step 2");
			stepContribution.setExitStatus(new ExitStatus("BAR"));
			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	public Step step3() {
		return stepBuilderFactory.get("step3").tasklet((stepContribution, chunkContext) -> {
			System.out.println("Running step 3");
			stepContribution.setExitStatus(new ExitStatus("FOO"));
			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	public Job helloWorldJob() {
		return jobBuilderFactory.get("helloworld")
			.incrementer(new RunIdIncrementer())
			.start(step1())
			.on("COMPLETED").to(step2())
			.from(step2())
				.on("BAR").to(step3())
			.from(step3())
				.on("FOO").end()
			.end()
			.build();
	}
}
