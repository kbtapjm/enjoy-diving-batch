package kr.co.pjm.diving.batch.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.pjm.diving.batch.service.JobService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = JobController.RESOURCE_PATH)
public class JobController {
    
    static final String RESOURCE_PATH = "/scheduler";
    
    private JobService jobService;
    
    @GetMapping(value = "/jobs")
    public ResponseEntity<?> getAllJobs() throws Exception {
        return ResponseEntity.ok(jobService.getAllJobs());
    }
    
    @PutMapping(value = "/jobs/{jobName}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateJobs(@PathVariable("jobName") String jobName)
        throws Exception {
      
        jobService.unScheduleJob(jobName);

        return ResponseEntity.ok(jobService.getAllJobs());
    }
    
    

}
