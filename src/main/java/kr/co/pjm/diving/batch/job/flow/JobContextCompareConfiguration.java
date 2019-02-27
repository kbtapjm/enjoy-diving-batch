package kr.co.pjm.diving.batch.job.flow;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobContextCompareConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job jobContextCompareJob() {
      return jobBuilderFactory.get("jobContextCompareActionJob")
              .start(jobContextCompareJobStep1())
              .next(jobContextCompareJobStep2())
              .next(jobContextCompareJobStep3())
              .build();
  }

  @Bean
  public Step jobContextCompareJobStep1() {
      return stepBuilderFactory.get("step1")
              .tasklet((contribution, chunkContext) -> {
                  log.info(">>>>> This is stepNextConditionalJob Step1");
                  throw new IllegalArgumentException("Step1에서 에러 발생!");
//                  return RepeatStatus.FINISHED;
              })
              .build();
  }

  @Bean
  public Step jobContextCompareJobStep2() {
      return stepBuilderFactory.get("jobContextCompareJobStep2")
              .tasklet((contribution, chunkContext) -> {
                  log.info(">>>>> This is stepNextConditionalJob Step2");
                  return RepeatStatus.FINISHED;
              })
              .build();
  }

  @Bean
  public Step jobContextCompareJobStep3() {
      return stepBuilderFactory.get("jobContextCompareJobStep3")
              .tasklet((contribution, chunkContext) -> {
                  log.info(">>>>> This is stepNextConditionalJob Step3");
                  return RepeatStatus.FINISHED;
              })
              .build();
  }
}
