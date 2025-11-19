/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import software.amazon.awssdk.services.iam.model.User;
import software.amazon.awssdk.services.iam.model.VirtualMFADevice;

import java.time.Instant;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsIamVirtualMfaDevice {
	private final String accountId;
	private final String serialNumber;
	private final User user;
	private final Instant enableDate;
	private final String userArn;

	public AwsIamVirtualMfaDevice(String accountId, String resourceId, VirtualMFADevice payload) {
		this.accountId = accountId;
		this.serialNumber = payload.serialNumber();
		this.user = payload.user();
		this.enableDate = payload.enableDate();
		this.userArn = payload.user() != null ? payload.user().arn() : null;
	}

	public String getAccountId() {
		return accountId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public User getUser() {
		return user;
	}

	public Instant getEnableDate() {
		return enableDate;
	}

	public String getUserArn() {
		return userArn;
	}
}
