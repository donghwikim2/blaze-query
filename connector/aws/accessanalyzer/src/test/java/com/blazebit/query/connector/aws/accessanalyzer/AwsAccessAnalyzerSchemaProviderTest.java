/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.accessanalyzer;

import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AwsAccessAnalyzerSchemaProviderTest {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new AwsAccessAnalyzerSchemaProvider() );
		builder.registerSchemaObjectAlias( AwsAnalyzer.class, "AwsAnalyzer" );
		CONTEXT = builder.build();
	}

	@Test
	void should_return_analyzer_with_tags() {
		try (var session = CONTEXT.createSession()) {
			session.put(
					AwsAnalyzer.class, Collections.singletonList( TestObjects.analyzerWithTags() ) );

			var typedQuery =
					session.createQuery( "select a.* from AwsAnalyzer a", new TypeReference<Map<String, Object>>() {
					} );

			var results = typedQuery.getResultList();
			assertThat( results ).isNotEmpty();
			assertThat( results.get( 0 ) ).containsKey( "tags" );
		}
	}

	@Test
	void should_return_analyzer_without_tags() {
		try (var session = CONTEXT.createSession()) {
			session.put(
					AwsAnalyzer.class, Collections.singletonList( TestObjects.analyzerWithoutTags() ) );

			var typedQuery =
					session.createQuery( "select a.* from AwsAnalyzer a", new TypeReference<Map<String, Object>>() {
					} );

			var results = typedQuery.getResultList();
			assertThat( results ).isNotEmpty();
		}
	}

	@Test
	void should_filter_analyzers_by_tag_keys() {
		try (var session = CONTEXT.createSession()) {
			session.put(
					AwsAnalyzer.class,
					java.util.Arrays.asList(
							TestObjects.analyzerWithTags(),
							TestObjects.analyzerWithoutTags(),
							TestObjects.analyzerWithPartialTags() ) );

			// Query to find analyzers that have the "Environment" tag
			var typedQuery =
					session.createQuery(
							"select a.accountId, a.payload.name, a.tags from AwsAnalyzer a where a.tags['Environment'] is not null",
							new TypeReference<Map<String, Object>>() {
							} );

			var results = typedQuery.getResultList();
			assertThat( results ).hasSize( 2 ); // analyzerWithTags and analyzerWithPartialTags
		}
	}
}
