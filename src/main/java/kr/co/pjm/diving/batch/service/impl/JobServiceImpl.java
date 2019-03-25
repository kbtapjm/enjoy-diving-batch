package kr.co.pjm.diving.batch.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import kr.co.pjm.diving.batch.service.JobService;
import kr.co.pjm.diving.common.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JobServiceImpl implements JobService {
    
    @Autowired
    @Lazy   // 지연로딩, 실제사용될 때 로딩
    private SchedulerFactoryBean schedulerFactoryBean;

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> getAllJobs() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            
            log.info("===> getMetaData : {}", scheduler.getMetaData());
            log.info("===> getCurrentlyExecutingJobs : {}", scheduler.getCurrentlyExecutingJobs());
            log.info("===> getSchedulerName : {}", scheduler.getSchedulerName());
            
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    String jobName = jobKey.getName();
                    String jobGroup = jobKey.getGroup();
                    
                    // get job's trigger
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    Date scheduleTime = triggers.get(0).getStartTime();
                    Date nextFireTime = triggers.get(0).getNextFireTime();
                    Date lastFiredTime = triggers.get(0).getPreviousFireTime();
                    String description = triggers.get(0).getDescription();
                    
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("jobName", jobName);
                    map.put("groupName", jobGroup);
                    map.put("scheduleTime", DateUtil.getInstance().getDateToString(scheduleTime, DateUtil.FORMAT_YYYY_MM_DD_HH_MM_SS));
                    map.put("lastFiredTime", DateUtil.getInstance().getDateToString(lastFiredTime, DateUtil.FORMAT_YYYY_MM_DD_HH_MM_SS));
                    map.put("nextFireTime", DateUtil.getInstance().getDateToString(nextFireTime, DateUtil.FORMAT_YYYY_MM_DD_HH_MM_SS));
                    map.put("description", description);
                    
                    if (this.isJobRunning(jobName)) {
                        map.put("jobStatus", "RUNNING");
                    } else {
                        map.put("jobStatus", this.getJobState(jobName));
                    }
                    
                    list.add(map);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    
        return list;
    }
    
    @Override
    public boolean unScheduleJob(String jobName) {
        boolean status = false;
        
        try {
            String jobKey = jobName;
            TriggerKey tkey = new TriggerKey(jobKey);
            System.out.println("Parameters received for unscheduling job : tkey :"+jobKey);
            
            status = schedulerFactoryBean.getScheduler().unscheduleJob(tkey);
            
            System.out.println("Trigger associated with jobKey :"+jobKey+ " unscheduled with status :"+status);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        
        log.info("status : {}", status);
        
        return status;
    }


    @Override
    public boolean deleteJob(String jobName) {
        System.out.println("Request received for deleting job.");

        String jobKey = jobName;
        String groupKey = "billingGroup";

        JobKey jkey = new JobKey(jobKey, groupKey); 
        System.out.println("Parameters received for deleting job : jobKey :"+jobKey);

        try {
            boolean status = schedulerFactoryBean.getScheduler().deleteJob(jkey);
            System.out.println("Job with jobKey :"+jobKey+ " deleted with status :"+status);
            return status;
        } catch (SchedulerException e) {
            System.out.println("SchedulerException while deleting job with key :"+jobKey + " message :"+e.getMessage());
            e.printStackTrace();
            return false;
        }
    }



    @Override
    public boolean pauseJob(String jobName) {
        // TODO Auto-generated method stub
        return false;
    }



    @Override
    public boolean resumeJob(String jobName) {
        // TODO Auto-generated method stub
        return false;
    }



    @Override
    public boolean startJobNow(String jobName) {
        // TODO Auto-generated method stub
        return false;
    }



    @Override
    public boolean isJobRunning(String jobName) {
        
        try {
            String jobKey = jobName;
            String groupKey = "billingGroup";

            List<JobExecutionContext> currentJobs = schedulerFactoryBean.getScheduler().getCurrentlyExecutingJobs();
            if (currentJobs != null) {
                for (JobExecutionContext jobCtx : currentJobs) {
                    String jobNameDB = jobCtx.getJobDetail().getKey().getName();
                    String groupNameDB = jobCtx.getJobDetail().getKey().getGroup();
                    
                    if (jobKey.equalsIgnoreCase(jobNameDB) && groupKey.equalsIgnoreCase(groupNameDB)) {
                        return true;
                    }
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    
        return false;
    }
    
    private String getJobState(String jobName) {

        try {
            String groupKey = "billingGroup";
            JobKey jobKey = new JobKey(jobName, groupKey);

            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);

            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
            if (triggers != null && triggers.size() > 0) {
                for (Trigger trigger : triggers) {
                    TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    
//                    switch (triggerState) {
//                    case PAUSED:
//                        break;
//                    case BLOCKED:
//                        break;
//                    case COMPLETE:
//                        break;
//                    case ERROR:
//                        break;
//                    case NONE:
//                        break;
//                    case NORMAL:
//                        break;
//                    default:
//                        break;
//                    }

                    if (TriggerState.PAUSED.equals(triggerState)) {
                        return "PAUSED";
                    } else if (TriggerState.BLOCKED.equals(triggerState)) {
                        return "BLOCKED";
                    } else if (TriggerState.COMPLETE.equals(triggerState)) {
                        return "COMPLETE";
                    } else if (TriggerState.ERROR.equals(triggerState)) {
                        return "ERROR";
                    } else if (TriggerState.NONE.equals(triggerState)) {
                        return "NONE";
                    } else if (TriggerState.NORMAL.equals(triggerState)) {
                        return "SCHEDULED";
                    }
                }
            }
        } catch (SchedulerException e) {
            System.out.println("SchedulerException while checking job with name and group exist:" + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    

}
