/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.accessanalyzer;

import software.amazon.awssdk.services.accessanalyzer.model.AnalyzerStatus;
import software.amazon.awssdk.services.accessanalyzer.model.AnalyzerSummary;
import software.amazon.awssdk.services.accessanalyzer.model.Type;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public final class TestObjects {

	private TestObjects() {
	}

	public static AwsAnalyzer analyzerWithTags() {
		AnalyzerSummary analyzer = AnalyzerSummary.builder()
				.arn( "arn:aws:access-analyzer:us-east-1:123456789012:analyzer/test-analyzer" )
				.name( "test-analyzer" )
				.type( Type.ACCOUNT )
				.createdAt( Instant.parse( "2024-01-15T10:00:00Z" ) )
				.lastResourceAnalyzedAt( Instant.parse( "2024-11-19T10:00:00Z" ) )
				.status( AnalyzerStatus.ACTIVE )
				.build();

		Map<String, String> tags = new HashMap<>();
		tags.put( "Environment", "Production" );
		tags.put( "Team", "Security" );
		tags.put( "Compliance", "Required" );

		return new AwsAnalyzer( "123456789012", "us-east-1", "analyzer/test-analyzer", analyzer, tags );
	}

	public static AwsAnalyzer analyzerWithoutTags() {
		AnalyzerSummary analyzer = AnalyzerSummary.builder()
				.arn( "arn:aws:access-analyzer:us-east-1:123456789012:analyzer/test-analyzer-no-tags" )
				.name( "test-analyzer-no-tags" )
				.type( Type.ACCOUNT )
				.createdAt( Instant.parse( "2024-01-15T10:00:00Z" ) )
				.status( AnalyzerStatus.ACTIVE )
				.build();

		return new AwsAnalyzer( "123456789012", "us-east-1", "analyzer/test-analyzer-no-tags", analyzer, new HashMap<>() );
	}

	public static AwsAnalyzer analyzerWithPartialTags() {
		AnalyzerSummary analyzer = AnalyzerSummary.builder()
				.arn( "arn:aws:access-analyzer:us-east-1:123456789012:analyzer/test-analyzer-partial" )
				.name( "test-analyzer-partial" )
				.type( Type.ACCOUNT )
				.createdAt( Instant.parse( "2024-01-15T10:00:00Z" ) )
				.status( AnalyzerStatus.ACTIVE )
				.build();

		Map<String, String> tags = new HashMap<>();
		tags.put( "Environment", "Development" );
		// Missing other required tags

		return new AwsAnalyzer( "123456789012", "us-east-1", "analyzer/test-analyzer-partial", analyzer, tags );
	}
}
