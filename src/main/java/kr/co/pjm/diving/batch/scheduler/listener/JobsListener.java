package kr.co.pjm.diving.batch.scheduler.listener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JobsListener implements JobListener {

  @Override
  public String getName() {
    return "globalJob";
  }

  @Override
  public void jobToBeExecuted(JobExecutionContext arg0) {
    log.info("%%% JobsListener.jobToBeExecuted()");
  }

  @Override
  public void jobExecutionVetoed(JobExecutionContext arg0) {
    log.info("%%% JobsListener.jobExecutionVetoed()");
  }

  @Override
  public void jobWasExecuted(JobExecutionContext arg0, JobExecutionException arg1) {
    log.info("%%% JobsListener.jobWasExecuted()");
  }

}
