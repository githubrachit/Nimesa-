package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Job;
import com.example.demo.entity.S3Object;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.S3ObjectRepository;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class S3Controller {

	@Autowired
    private S3Client s3Client;

    @Autowired
    private S3ObjectRepository s3ObjectRepository;

    @Autowired
    private JobRepository jobRepository;

    @PostMapping("/getS3BucketObjects")
    public Long getS3BucketObjects(@RequestParam String bucketName) {
        Job job = new Job();
        job.setStatus("In Progress");
        jobRepository.save(job);

        Long jobId = job.getId();
        discoverS3BucketObjects(bucketName, jobId);

        return jobId;
    }

    @Async
    public void discoverS3BucketObjects(String bucketName, Long jobId) {
        try {
            ListObjectsRequest request = ListObjectsRequest.builder().bucket(bucketName).build();
            ListObjectsResponse response = s3Client.listObjects(request);

            response.contents().forEach(s3Object -> {
                S3Object object = new S3Object();
                object.setBucketName(bucketName);
                object.setObjectKey(s3Object.key());
                s3ObjectRepository.save(object);
            });

            updateJobStatus(jobId, "Success");
        } catch (Exception e) {
            updateJobStatus(jobId, "Failed");
        }
    }

    private void updateJobStatus(Long jobId, String status) {
        Job job = jobRepository.findById(jobId).orElseThrow();
        job.setStatus(status);
        jobRepository.save(job);
    }

    @GetMapping("/getS3BucketObjectCount")
    public Long getS3BucketObjectCount(@RequestParam String bucketName) {
        return s3ObjectRepository.countByBucketName(bucketName);
    }

    @GetMapping("/getS3BucketObjectLike")
    public List<String> getS3BucketObjectLike(@RequestParam String bucketName, @RequestParam String pattern) {
        return s3ObjectRepository.findByBucketNameAndObjectKeyLike(bucketName, "%" + pattern + "%")
                .stream()
                .map(S3Object::getObjectKey)
                .collect(Collectors.toList());
    }
}
	
