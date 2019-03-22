package kr.co.pjm.diving.batch.configuration.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.pjm.diving.batch.scheduler.batch.listener.LoginLogBatchJobListerer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

  @Autowired
  public JobBuilderFactory jobBuilderFactory;

  @Autowired
  public StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job loginLogDailyBatchJob() {
    return jobBuilderFactory.get("loginLogDailyBatchJob")
        .start(loginLogDailyBatchStep1(null))
        .next(loginLogDailyBatchStep2(null))
        .build();
  }

  @Bean
  @JobScope
  public Step loginLogDailyBatchStep1(@Value("#{jobParameters[billingDate]}") String billingDate) {
    return stepBuilderFactory.get("billingStep1").tasklet((contribution, chunkContext) -> {
      log.info(">>>>> This is Step1");
      log.info(">>>>> billingDate = {}", billingDate);

      return RepeatStatus.FINISHED;
    }).build();
  }

  @Bean
  @JobScope
  public Step loginLogDailyBatchStep2(@Value("#{jobParameters[billingDate]}") String billingDate) {
    return stepBuilderFactory.get("billingStep2").tasklet((contribution, chunkContext) -> {
      log.info(">>>>> This is Step2");
      log.info(">>>>> billingDate = {}", billingDate);

      return RepeatStatus.FINISHED;
    }).build();
  }
  
  @Bean
  public JobExecutionListener listener() {
      return new LoginLogBatchJobListerer();
  }

}
