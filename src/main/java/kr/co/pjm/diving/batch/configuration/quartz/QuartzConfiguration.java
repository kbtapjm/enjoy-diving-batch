package kr.co.pjm.diving.batch.configuration.quartz;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import kr.co.pjm.diving.batch.scheduler.job.LoginLogDailyBatchJob;
import kr.co.pjm.diving.batch.scheduler.listener.JobsListener;
import kr.co.pjm.diving.batch.scheduler.listener.TriggerListner;

@Configuration
public class QuartzConfiguration {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private JobLocator jobLocator;

  @Autowired
  private TriggerListner triggerListner;

  @Autowired
  private JobsListener jobsListener;

  @Bean
  public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
    JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
    jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);

    return jobRegistryBeanPostProcessor;
  }

  @Bean
  public JobDetailFactoryBean jobDetailFactoryBean() {
    JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
    jobDetailFactoryBean.setJobClass(LoginLogDailyBatchJob.class);
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("jobName", "loginLogDailBatchJob");
    map.put("jobLauncher", jobLauncher);
    map.put("jobLocator", jobLocator);

    jobDetailFactoryBean.setJobDataAsMap(map);
    jobDetailFactoryBean.setGroup("enjoy-diving-batch");
    jobDetailFactoryBean.setName("loginLogDailBatchJob");
    jobDetailFactoryBean.setDescription("Daily login count");

    return jobDetailFactoryBean;
  }

  @Bean
  public CronTriggerFactoryBean cronTriggerFactoryBean() {
    CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
    cronTriggerFactoryBean.setJobDetail(jobDetailFactoryBean().getObject());
    // run every 10 seconds
    cronTriggerFactoryBean.setCronExpression("*/10 * * * * ? *");
    //cronTriggerFactoryBean.setCronExpression("0 28 17 ? * *");
    cronTriggerFactoryBean.setDescription("loginLogDailBatchJob CronTriggerFactoryBean");

    return cronTriggerFactoryBean;
  }
  
  public static SimpleTriggerFactoryBean simpleTriggerFactoryBean(JobDetail jobDetail, long pollFrequencyMs) {
    SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
    simpleTriggerFactoryBean.setJobDetail(jobDetail);
    simpleTriggerFactoryBean.setStartDelay(0L);
    simpleTriggerFactoryBean.setRepeatInterval(pollFrequencyMs);
    simpleTriggerFactoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
    simpleTriggerFactoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
    
    return simpleTriggerFactoryBean;
}

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean() {
    SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
    schedulerFactoryBean.setTriggers(cronTriggerFactoryBean().getObject());
    schedulerFactoryBean.setGlobalJobListeners(jobsListener);
    schedulerFactoryBean.setGlobalTriggerListeners(triggerListner);

    return schedulerFactoryBean;
  }

  @Bean
  public Properties quartzProperties() throws IOException {
    PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
    propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
    propertiesFactoryBean.afterPropertiesSet();

    return propertiesFactoryBean.getObject();
  }
}
