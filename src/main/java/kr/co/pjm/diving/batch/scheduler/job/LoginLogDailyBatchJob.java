package kr.co.pjm.diving.batch.scheduler.job;

import java.time.LocalDateTime;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import kr.co.pjm.diving.common.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class LoginLogDailyBatchJob extends QuartzJobBean implements InterruptableJob {
  private volatile boolean toStopFlag = true;
  private String jobName;
  private JobLauncher jobLauncher;
  private JobLocator jobLocator;
  
  @Override
  protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    JobKey key = jobExecutionContext.getJobDetail().getKey();
    
    log.info("BillingJob Job started with key : " + key.getName() + ", Group : " + key.getGroup() + " " 
    + ", Thread Name : " + Thread.currentThread().getName() + " ,Time now : " + LocalDateTime.now());
    
    try {
      log.info("#############################################################################");
      JobParameters jobParameters = new JobParametersBuilder()
          .addString("batchDate", DateUtil.getInstance().getLocalDateToString(0, DateUtil.FORMAT_YYYY_MM_DD))
          .toJobParameters();
      
      Job job = jobLocator.getJob(jobName);
      JobExecution jobExecution = jobLauncher.run(job, jobParameters);
      log.info("======> LoginLogDailyBatchJob Status: {}, End Time : {}", jobExecution.getStatus(), jobExecution.getEndTime());
      log.info("#############################################################################");
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
        | JobParametersInvalidException | NoSuchJobException e) {
      e.printStackTrace();
      log.error("Error : {}", e);
    }
  }

  @Override
  public void interrupt() throws UnableToInterruptJobException {
    log.info("Stopping thread... ");
    toStopFlag = false;
  }
  
}
