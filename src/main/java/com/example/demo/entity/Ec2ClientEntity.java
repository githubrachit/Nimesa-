package com.example.demo.entity;

import software.amazon.awssdk.services.ec2.Ec2Client;

public class Ec2ClientEntity {

	 private Ec2Client ec2Client;

	    public Ec2ClientEntity(Ec2Client ec2Client) {
	        this.ec2Client = ec2Client;
	    }
}
