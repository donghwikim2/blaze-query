/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import software.amazon.awssdk.services.iam.model.User;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public record AwsIamGroupUser(
		String accountId,
		String groupName,
		String userName,
		String userArn
) {
	public static AwsIamGroupUser from(String accountId, String groupName, User user) {
		return new AwsIamGroupUser(
				accountId,
				groupName,
				user.userName(),
				user.arn()
		);
	}
}
