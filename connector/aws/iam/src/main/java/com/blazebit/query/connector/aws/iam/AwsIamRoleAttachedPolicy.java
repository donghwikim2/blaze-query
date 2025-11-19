/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import software.amazon.awssdk.services.iam.model.AttachedPolicy;

/**
 * Represents a managed policy attached directly to an IAM role.
 *
 * @author Donghwi Kim
 * @since 1.0.0
 */
public record AwsIamRoleAttachedPolicy(
		String accountId,
		String roleName,
		String policyName,
		String policyArn
) {
	public static AwsIamRoleAttachedPolicy from(String accountId, String roleName, AttachedPolicy attachedPolicy) {
		return new AwsIamRoleAttachedPolicy(
				accountId,
				roleName,
				attachedPolicy.policyName(),
				attachedPolicy.policyArn()
		);
	}
}
