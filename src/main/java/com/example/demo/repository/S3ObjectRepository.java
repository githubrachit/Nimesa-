package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.S3Object;

public interface S3ObjectRepository extends JpaRepository<S3Object, Long> {

	Long countByBucketName(String bucketName);

	List<S3Object> findByBucketNameAndObjectKeyLike(String bucketName, String pattern);
}
