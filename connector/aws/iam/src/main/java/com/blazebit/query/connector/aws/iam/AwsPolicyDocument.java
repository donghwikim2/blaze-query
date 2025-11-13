/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Represents an IAM policy document with its statements.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public record AwsPolicyDocument(
		String accountId,
		String policyArn,
		String policyName,
		String versionId,
		String version,
		List<AwsPolicyStatement> statement
) {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static AwsPolicyDocument fromJson(String accountId, String policyArn, String policyName, String versionId, String payload) {
		try {
			JsonNode json = MAPPER.readTree( payload );
			return new AwsPolicyDocument(
					accountId,
					policyArn,
					policyName,
					versionId,
					json.has( "Version" ) ? json.get( "Version" ).asText( "" ) : "",
					parseStatement( json )
			);
		}
		catch (Exception e) {
			throw new RuntimeException( "Error parsing JSON for AwsPolicyDocument", e );
		}
	}

	private static List<AwsPolicyStatement> parseStatement(JsonNode json) {
		if ( !json.has( "Statement" ) ) {
			return List.of();
		}
		return StreamSupport.stream( json.get( "Statement" ).spliterator(), false )
				.map( edge -> AwsPolicyStatement.fromJson( edge.toString() ) )
				.collect( Collectors.toList() );
	}
}
