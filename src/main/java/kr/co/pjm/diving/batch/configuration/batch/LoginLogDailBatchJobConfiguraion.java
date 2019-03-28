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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import kr.co.pjm.diving.common.domain.entity.UserLoginLog;
import kr.co.pjm.diving.common.domain.entity.UserLoginLogDaily;
import kr.co.pjm.diving.common.repository.UserLoginLogDailyRepasitory;
import kr.co.pjm.diving.common.repository.UserLoginLogRepasitory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableBatchProcessing
public class LoginLogDailBatchJobConfiguraion {
  
  @Autowired
  public JobBuilderFactory jobBuilderFactory;

  @Autowired
  public StepBuilderFactory stepBuilderFactory;

  @Autowired
  private UserLoginLogRepasitory userLoginLogRepasitory;
  
  @Autowired
  private UserLoginLogDailyRepasitory userLoginLogDailyRepasitory;
  
  private int chunkSize = 2;

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
        .<Object, Object>chunk(chunkSize)
        .reader(reader(null))
        .processor(processor())
        .writer(writer())
        .build();
  }

  @Bean
  @StepScope
  public RepositoryItemReader<UserLoginLog> reader(@Value("#{jobParameters[batchDate]}") String batchDate) {
    if (log.isInfoEnabled()) {
      log.info("===> batchDate : {}", batchDate);
    }
    
    RepositoryItemReader<UserLoginLog> reader = new RepositoryItemReader<>();
    reader.setRepository(userLoginLogRepasitory);
    reader.setMethodName("findByLoginDate");
    
    List<Object> arguments = new ArrayList<>();
    arguments.add(batchDate);
    reader.setArguments(arguments);
    reader.setSort(Collections.singletonMap("email", Sort.Direction.ASC));
    
    return reader;
  }
  
  @Bean
  @StepScope
  public ItemProcessor<Object, ? super Object> processor() {
    return item -> {
      return item;
    };
  }

  
  @Bean
  @StepScope
  public ItemWriter<? super Object> writer() {
    return items -> {
      for (Object obj : items) {
        UserLoginLog userLoginLog = (UserLoginLog) obj;
        
        UserLoginLogDaily userLoginLogDaily = UserLoginLogDaily.builder()
            .user(userLoginLog.getUser())
            .loginCount(userLoginLog.getLoginCount())
            .loginDate(userLoginLog.getLoginDate())
            .build();
        
        userLoginLogDailyRepasitory.save(userLoginLogDaily);
      }
    };
  }

}
