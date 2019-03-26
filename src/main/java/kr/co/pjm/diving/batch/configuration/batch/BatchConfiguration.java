package kr.co.pjm.diving.batch.configuration.batch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import kr.co.pjm.diving.common.domain.entity.UserLoginLog;
import kr.co.pjm.diving.common.repository.UserLoginLogRepasitory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

  @Autowired
  public JobBuilderFactory jobBuilderFactory;

  @Autowired
  public StepBuilderFactory stepBuilderFactory;

  @Autowired
  private UserLoginLogRepasitory userLoginLogRepasitory;

  // @Bean
  // public CustomItemReader reader() {
  // return new CustomItemReader();
  // }
  //
  // @Bean
  // public CustomItemWriter writer() {
  // return new CustomItemWriter();
  // }

  // @Bean
  // public Job loginLogDailBatchJob() {
  // return
  // jobBuilderFactory.get("loginLogDailBatchJob").start(loginLogDailBatchJobStep1(null))
  // .next(loginLogDailBatchJobStep2(null)).build();
  // }

  @Bean
  public Job loginLogDailBatchJob() {
    return jobBuilderFactory.get("loginLogDailBatchJob")
        .incrementer(new RunIdIncrementer())
        .flow(loginLogDailBatchJobStep())
        .end()
        .build();
  }

  @Bean
  public Step loginLogDailBatchJobStep() {
    return stepBuilderFactory.get("loginLogDailBatchJobStep")
        .<Object, Object>chunk(10)
        .reader(reader(null))
        .writer(writer())
        .build();
  }

  @Bean
  @StepScope
  public RepositoryItemReader<UserLoginLog> reader(@Value("#{jobParameters[time]}") Long time) {
    log.info("===============> time : {}", time);
    
    RepositoryItemReader<UserLoginLog> reader = new RepositoryItemReader<>();
    reader.setRepository(userLoginLogRepasitory);
    reader.setMethodName("findByLoginDate");
    
    List<Object> arguments = new ArrayList<>();
    arguments.add("2019-03-26");
    reader.setArguments(arguments);
    
    reader.setSort(Collections.singletonMap("email", Sort.Direction.ASC));

    return reader;
  }

  @Bean
  @StepScope
  public ItemWriter<? super Object> writer() {
    return items -> {
      for (Object obj : items) {
        UserLoginLog userLoginLog = (UserLoginLog) obj;
        log.info("userLoginLog : {}, {}", userLoginLog.getEmail(), userLoginLog.getLoginDateTime());
      }
    };
  }

  // @Bean
  // @JobScope
  // public Step loginLogDailBatchJobStep1(@Value("#{jobParameters[batchDate]}")
  // String batchDate) {
  // return stepBuilderFactory.get("billingStep1").tasklet((contribution,
  // chunkContext) -> {
  // log.info(">>>>> This is Step1");
  // log.info(">>>>> batchDate = {}", batchDate);
  //
  // return RepeatStatus.FINISHED;
  // }).build();
  // }
  //
  // @Bean
  // @JobScope
  // public Step loginLogDailBatchJobStep2(@Value("#{jobParameters[batchDate]}")
  // String batchDate) {
  // return stepBuilderFactory.get("billingStep2").tasklet((contribution,
  // chunkContext) -> {
  // log.info(">>>>> This is Step2");
  // log.info(">>>>> batchDate = {}", batchDate);
  //
  // return RepeatStatus.FINISHED;
  // }).build();
  // }

}
