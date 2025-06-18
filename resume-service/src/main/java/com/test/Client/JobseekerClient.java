package com.test.Client;

import com.test.Pojo.JobseekerResume;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

@FeignClient(name = "jobseeker-service")
public interface JobseekerClient {

    @GetMapping("/api/profile/{userId}")
    ResponseEntity<JobseekerResume> getResumeByUserId(@PathVariable("userId") Integer userId);
} 