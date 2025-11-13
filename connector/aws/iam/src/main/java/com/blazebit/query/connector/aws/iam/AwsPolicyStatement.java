/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public record AwsPolicyStatement(
		String sid,
		String effect,
		String principalJsonValue,
		String actionJsonValue,
		String resourceJsonValue,
		String conditionJsonValue
) {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static AwsPolicyStatement fromJson(String payload) {
		try {
			JsonNode json = MAPPER.readTree( payload );
			return new AwsPolicyStatement(
					json.has( "Sid" ) ? json.get( "Sid" ).asText( "" ) : "",
					json.has( "Effect" ) ? json.get( "Effect" ).asText( "" ) : "",
					json.has( "Principal" ) ? json.get( "Principal" ).toString() : "",
					json.has( "Action" ) ? json.get( "Action" ).toString() : "",
					json.has( "Resource" ) ? json.get( "Resource" ).toString() : "",
					json.has( "Condition" ) ? json.get( "Condition" ).toString() : ""
			);
		}
		catch (Exception e) {
			throw new RuntimeException( "Error parsing JSON for AwsPolicyStatement", e );
		}
	}
}
