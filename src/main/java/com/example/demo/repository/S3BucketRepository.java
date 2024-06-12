package com.example.demo.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.S3Bucket;

public interface S3BucketRepository  extends JpaRepository<S3Bucket, Long>{

}
