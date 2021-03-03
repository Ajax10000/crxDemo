package com.example.demo.api;

import com.example.demo.model.Job;
import com.example.demo.service.JobService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

@RequestMapping(value="api/v1/job", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.APPLICATION_JSON_VALUE})
@RestController
public class JobController {
    private final JobService jobService;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public int addJob(@RequestBody Job job) {
        int id = jobService.addJob(job);
        return id;
    }

    @GetMapping
    public int getStatus(int id) {
        int status = jobService.getStatus(id);
        return status;
    }

}
