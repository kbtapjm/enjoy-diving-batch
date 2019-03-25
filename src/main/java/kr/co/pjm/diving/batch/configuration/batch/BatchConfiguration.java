package kr.co.pjm.diving.batch.configuration.batch;

import org.springframework.batch.core.Job;
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

import kr.co.pjm.diving.batch.scheduler.batch.reader.CustomItemReader;
import kr.co.pjm.diving.batch.scheduler.batch.writer.CustomItemWriter;
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
  public CustomItemReader reader() {
    return new CustomItemReader();
  }

  @Bean
  public CustomItemWriter writer() {
    return new CustomItemWriter();
  }

  @Bean
  public Job loginLogDailBatchJob() {
    return jobBuilderFactory.get("loginLogDailBatchJob")
        .start(loginLogDailBatchJobStep1(null))
        .next(loginLogDailBatchJobStep2(null))
        .build();
  }

  @Bean
  @JobScope
  public Step loginLogDailBatchJobStep1(@Value("#{jobParameters[batchDate]}") String batchDate) {
    return stepBuilderFactory.get("billingStep1").tasklet((contribution, chunkContext) -> {
      log.info(">>>>> This is Step1");
      log.info(">>>>> batchDate = {}", batchDate);

      return RepeatStatus.FINISHED;
    }).build();
  }

  @Bean
  @JobScope
  public Step loginLogDailBatchJobStep2(@Value("#{jobParameters[batchDate]}") String batchDate) {
    return stepBuilderFactory.get("billingStep2").tasklet((contribution, chunkContext) -> {
      log.info(">>>>> This is Step2");
      log.info(">>>>> batchDate = {}", batchDate);

      return RepeatStatus.FINISHED;
    }).build();
  }

  // @Bean
  // public Job testJob() {
  // return jobBuilderFactory.get("testJob")
  // .incrementer(new RunIdIncrementer())
  // .flow(step1())
  // .end()
  // .build();
  // }
  //
  // @Bean
  // public Step step1() {
  // return stepBuilderFactory.get("step1")
  // .<Object, Object>chunk(10)
  // .reader(reader())
  // .writer(writer())
  // .build();
  // }

}
