package kr.co.pjm.diving.batch.scheduler.listener;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TriggerListner implements TriggerListener {

  @Override
  public String getName() {
    return "globalTrigger";
  }

  @Override
  public void triggerMisfired(Trigger trigger) {
    log.info("@@@ TriggerListner.triggerMisfired()");
    String jobName = trigger.getJobKey().getName();
    log.info("Job name: " + jobName + " is misfired");
  }

  @Override
  public void triggerFired(Trigger trigger, JobExecutionContext context) {
    log.info("@@@ TriggerListner.triggerFired()");
  }

  @Override
  public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
    log.info("@@@ TriggerListner.vetoJobExecution()");
    return false;
  }

  @Override
  public void triggerComplete(Trigger trigger, JobExecutionContext context,
      CompletedExecutionInstruction triggerInstructionCode) {
    log.info("@@@ TriggerListner.triggerComplete()");
  }

}
