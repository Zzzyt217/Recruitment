package com.test.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@FeignClient(name = "jobseeker-service")
public interface JobseekerClient {

    @GetMapping("/api/profile/{userId}")
    ResponseEntity<Object> getResumeByUserId(
        @PathVariable("userId") Integer jobseekerUserId
    );
} 