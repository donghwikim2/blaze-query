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

	/**
	 * Constructs an AwsAnalyzer with the specified parameters.
	 *
	 * @param accountId The AWS account ID
	 * @param region The AWS region
	 * @param resourceId The resource ID
	 * @param payload The analyzer summary from AWS SDK
	 * @param tags The tags associated with the analyzer
	 */
	public AwsAnalyzer(String accountId, String region, String resourceId, AnalyzerSummary payload, Map<String, String> tags) {
		super( accountId, region, resourceId, payload );
		this.tags = tags;
	}

	@Override
	public AnalyzerSummary getPayload() {
		return super.getPayload();
	}

	/**
	 * Returns the tags associated with this analyzer.
	 *
	 * @return A map of tag keys to tag values
	 */
	public Map<String, String> tags() {
		return tags;
	}
}
