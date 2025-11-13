/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsAccount {

	private final String accountId;
	private final String arn;

	/**
	 * Creates a new AWS account.
	 *
	 * @param accountId The account id
	 * @param arn The account ARN
	 */
	public AwsAccount(String accountId, String arn) {
		this.accountId = accountId;
		this.arn = arn;
	}

	/**
	 * Returns the account id.
	 *
	 * @return the account id
	 */
	public String getAccountId() {
		return accountId;
	}

	/**
	 * Returns the account ARN.
	 *
	 * @return the account ARN
	 */
	public String getArn() {
		return arn;
	}
}
