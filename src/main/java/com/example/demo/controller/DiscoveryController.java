package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.repository.EC2InstanceRepository;
import com.example.demo.repository.S3BucketRepository;

@RestController
@RequestMapping("/api")
public class DiscoveryController {
	 @Autowired
	    private EC2InstanceRepository ec2InstanceRepository;

	    @Autowired
	    private S3BucketRepository s3BucketRepository;

	    @GetMapping("/getDiscoveryResult/{service}")
	    public List<?> getDiscoveryResult(@PathVariable String service) {
	        if ("EC2".equalsIgnoreCase(service)) {
	            return ec2InstanceRepository.findAll();
	        } else if ("S3".equalsIgnoreCase(service)) {
	            return s3BucketRepository.findAll();
	        }
	        throw new IllegalArgumentException("Invalid service name");
	    }
}

