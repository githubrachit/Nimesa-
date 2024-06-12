package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.demo.entity.EC2Instance;
import com.example.demo.entity.Job;
import com.example.demo.entity.S3Bucket;
import com.example.demo.repository.EC2InstanceRepository;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.S3BucketRepository;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

import java.util.concurrent.CompletableFuture;

@Service
public class AwsService {
	    @Autowired
	    private S3Client s3Client;

	    @Autowired
	    private Ec2Client ec2Client;

	    @Autowired
	    private JobRepository jobRepository;

	    @Autowired
	    private EC2InstanceRepository ec2InstanceRepository;

	    @Autowired
	    private S3BucketRepository s3BucketRepository;

	    @Async
	    public CompletableFuture<Void> discoverEC2Instances(Long jobId) {
	        try {
	            DescribeInstancesRequest request = DescribeInstancesRequest.builder().build();
	            DescribeInstancesResponse response = ec2Client.describeInstances(request);
	            response.reservations().forEach(reservation -> 
	                reservation.instances().forEach(instance -> {
	                    EC2Instance ec2Instance = new EC2Instance();
	                    ec2Instance.setInstanceId(instance.instanceId()); 
	                    ec2InstanceRepository.save(ec2Instance);
	                })
	            );
	            updateJobStatus(jobId, "Success");
	        } catch (Exception e) {
	            updateJobStatus(jobId, "Failed");
	        }
	        return CompletableFuture.completedFuture(null);
	    }

	    @Async
	    public CompletableFuture<Void> discoverS3Buckets(Long jobId) {//void
	        try {
	            ListBucketsResponse response = s3Client.listBuckets();
	            response.buckets().forEach(bucket -> {
	            	  S3Bucket s3Bucket = new S3Bucket();
	                  s3Bucket.setBucketName(bucket.name());
	                  s3BucketRepository.save(s3Bucket);
	            });
	            updateJobStatus(jobId, "Success");
	        } catch (Exception e) {
	            updateJobStatus(jobId, "Failed");
	        }
	        return CompletableFuture.completedFuture(null);
	    }

	    private void updateJobStatus(Long jobId, String status) {
	        Job job = jobRepository.findById(jobId).orElseThrow();
	        job.setStatus(status);
	        jobRepository.save(job);
	    }
}
