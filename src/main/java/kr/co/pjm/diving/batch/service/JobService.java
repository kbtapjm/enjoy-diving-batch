package kr.co.pjm.diving.batch.service;

import java.util.List;
import java.util.Map;

public interface JobService {

  List<Map<String, Object>> getAllJobs();

  boolean unScheduleJob(String jobName);

  boolean deleteJob(String jobName);

  boolean pauseJob(String jobName);

  boolean resumeJob(String jobName);

  boolean startJobNow(String jobName);

  boolean isJobRunning(String jobName);

}
