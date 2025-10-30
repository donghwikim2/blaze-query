/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.s3;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyResponse;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsBucketPolicy extends AwsWrapper<GetBucketPolicyResponse> {
	public AwsBucketPolicy(String accountId, String region, String resourceId, GetBucketPolicyResponse payload) {
		super( accountId, region, resourceId, payload );
	}

	@Override
	public GetBucketPolicyResponse getPayload() {
		return super.getPayload();
	}
}
