/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.iam.model.Group;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsIamGroup extends AwsWrapper<Group> {
	public AwsIamGroup(String accountId, String resourceId, Group payload) {
		super( accountId, null, resourceId, payload );
	}

	@Override
	public Group getPayload() {
		return super.getPayload();
	}
}
