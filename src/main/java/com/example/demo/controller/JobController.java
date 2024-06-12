package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Job;
import com.example.demo.repository.JobRepository;
import com.example.demo.service.AwsService;

@RestController
@RequestMapping("/api")
public class JobController {
	    @Autowired
	    private AwsService awsService;

	    @Autowired
	    private JobRepository jobRepository;

	    @PostMapping("/discoverServices")
	    public Long discoverServices(@RequestBody List<String> services) {
	        Job job = new Job();
	        job.setStatus("In Progress");
	        jobRepository.save(job);

	        Long jobId = job.getId();

	        if (services.contains("EC2")) {
	            awsService.discoverEC2Instances(jobId);
	        }
	        if (services.contains("S3")) {
	            awsService.discoverS3Buckets(jobId);
	        }

	        return jobId;
	    }

	    @GetMapping("/getJobResult/{jobId}")
	    public String getJobResult(@PathVariable Long jobId) {
	        return jobRepository.findById(jobId).orElseThrow().getStatus();
	    }
}

