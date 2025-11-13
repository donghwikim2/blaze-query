/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.iam.model.Policy;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsPolicy extends AwsWrapper<Policy> {
	public AwsPolicy(String accountId, String resourceId, Policy payload) {
		super( accountId, null, resourceId, payload );
	}

	@Override
	public Policy getPayload() {
		return super.getPayload();
	}
}
