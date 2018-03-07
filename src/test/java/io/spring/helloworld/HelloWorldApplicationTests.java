package io.spring.helloworld;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = HelloWorldApplicationTests.TestConfig.class,
	properties = "spring.batch.job.enabled=false")
public class HelloWorldApplicationTests {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Test
	public void contextLoads() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

		Map<String, StepExecution> steps = jobExecution.getStepExecutions().stream()
			.collect(Collectors.toMap(se -> se.getStepName(), se -> se));

		assertThat(steps.keySet()).containsExactlyInAnyOrder("step1", "step2", "step3");

		assertThat(steps.get("step1").getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
		assertThat(steps.get("step2").getExitStatus().getExitCode()).isEqualTo("BAR");
		assertThat(steps.get("step3").getExitStatus().getExitCode()).isEqualTo("FOO");

	}

	@Configuration
	@SpringBootApplication
	static class TestConfig {
		@Bean
		JobLauncherTestUtils jobLauncherTestUtils() {
			return new JobLauncherTestUtils();
		}
	}
}

