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
public class LoginLogDailyJob extends QuartzJobBean implements InterruptableJob {
  private volatile boolean toStopFlag = true;
  private String jobName;
  private JobLauncher jobLauncher;
  private JobLocator jobLocator;

  @Override
  public void interrupt() throws UnableToInterruptJobException {
    log.info("Stopping thread... ");
    toStopFlag = false;
  }

  @Override
  protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    JobKey key = jobExecutionContext.getJobDetail().getKey();

    log.info("BillingJob Job started with key : " + key.getName() + ", Group :" + key.getGroup() + " "
        + ", Thread Name :" + Thread.currentThread().getName() + " ,Time now : " + LocalDateTime.now());

    try {
      JobParameters jobParameters = new JobParametersBuilder()
          .addString("billingDate", DateUtil.getInstance().getLocalDateToString(1, DateUtil.FORMAT_YYYY_MM_DD))
          .toJobParameters();

      Job job = jobLocator.getJob(jobName);

      JobExecution jobExecution = jobLauncher.run(job, jobParameters);

      // TODO: 배치 상태를 결과로 받으니깐 결과에 따른 로직 처리
      log.info("########### Status: {}", jobExecution.getStatus());
    } catch (NoSuchJobException | JobExecutionAlreadyRunningException | JobRestartException
        | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
      e.printStackTrace();
    }
  }

}
