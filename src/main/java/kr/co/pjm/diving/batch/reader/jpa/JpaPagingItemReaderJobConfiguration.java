package kr.co.pjm.diving.batch.reader.jpa;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.pjm.diving.common.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaPagingItemReaderJobConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final EntityManagerFactory entityManagerFactory;
  
  private int chunkSize = 10;
  
  @Bean
  public Job jpaPagingItemReaderJob() {
      return jobBuilderFactory.get("jpaPagingItemReaderJob")
              .start(jpaPagingItemReaderStep())
              .build();
  }

  @Bean
  @JobScope
  public Step jpaPagingItemReaderStep() {
      return stepBuilderFactory.get("jpaPagingItemReaderStep")
              .<User, User>chunk(chunkSize)
              .reader(jpaPagingItemReader())
              .writer(jpaPagingItemWriter())
              .build();
  }
  
  @Bean
  @StepScope
  public JpaPagingItemReader<User> jpaPagingItemReader() {
      return new JpaPagingItemReaderBuilder<User>()
              .name("jpaPagingItemReader")
              .entityManagerFactory(entityManagerFactory)
              .pageSize(chunkSize)
              .queryString("SELECT u FROM User u")
              .build();
  }
  
  @Bean
  @StepScope
  private ItemWriter<User> jpaPagingItemWriter() {
    return list -> {
        for (User user: list) {
            log.info("user email={}", user.getEmail());
        }
    };
}
  
}
