package kr.co.pjm.diving.batch.scheduler.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginLogDailBatchJobListener extends JobExecutionListenerSupport {
  
  @Override
  public void beforeJob(JobExecution jobExecution) {
    log.info("===> LoginLogDailBatchJobListener beforeJob getStatus : {}", jobExecution.getStatus());
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    log.info("===> LoginLogDailBatchJobListener afterJob getStatus : {}", jobExecution.getStatus());
  }

}
