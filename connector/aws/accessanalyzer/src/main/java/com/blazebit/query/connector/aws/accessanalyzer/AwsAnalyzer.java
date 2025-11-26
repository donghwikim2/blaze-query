/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.accessanalyzer;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.accessanalyzer.model.AnalyzerSummary;

import java.util.Map;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsAnalyzer extends AwsWrapper<AnalyzerSummary> {

	private final Map<String, String> tags;

	public AwsAnalyzer(String accountId, String region, String resourceId, AnalyzerSummary payload, Map<String, String> tags) {
		super( accountId, region, resourceId, payload );
		this.tags = tags;
	}

	@Override
	public AnalyzerSummary getPayload() {
		return super.getPayload();
	}

	public Map<String, String> tags() {
		return tags;
	}
}
