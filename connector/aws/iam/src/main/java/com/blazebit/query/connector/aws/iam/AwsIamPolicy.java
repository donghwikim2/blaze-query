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
 * @author Donghwi Kim
 * @since 1.0.0
 */
public record AwsIamPolicy(
		String accountId,
		String arn,
		String policyName,
		String policyId,
		String defaultVersionId,
		String version,
		List<AwsIamPolicyStatement> statement
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static AwsIamPolicy fromJson(
			String accountId,
			String arn,
			String policyName,
			String policyId,
			String defaultVersionId,
			String policyDocument) {
		try {
			JsonNode json = MAPPER.readTree( policyDocument );
			return new AwsIamPolicy(
					accountId,
					arn,
					policyName,
					policyId,
					defaultVersionId,
					json.has( "Version" ) ? json.get( "Version" ).asText( "" ) : "",
					parseStatement( json )
			);
		}
		catch (Exception e) {
			throw new RuntimeException( "Error parsing JSON for AwsIamPolicy", e );
		}
	}

	private static List<AwsIamPolicyStatement> parseStatement(JsonNode json) {
		if ( !json.has( "Statement" ) ) {
			return List.of();
		}
		return StreamSupport.stream( json.get( "Statement" ).spliterator(), false )
				.map( edge -> AwsIamPolicyStatement.fromJson( edge.toString() ) )
				.collect( Collectors.toList() );
	}
}
