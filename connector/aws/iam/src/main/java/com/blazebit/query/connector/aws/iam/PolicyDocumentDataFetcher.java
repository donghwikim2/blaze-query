/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.IamClientBuilder;
import software.amazon.awssdk.services.iam.model.GetPolicyVersionRequest;
import software.amazon.awssdk.services.iam.model.Policy;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches IAM policy documents by retrieving the policy version for each policy.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class PolicyDocumentDataFetcher implements DataFetcher<AwsPolicyDocument>, Serializable {

	public static final PolicyDocumentDataFetcher INSTANCE = new PolicyDocumentDataFetcher();

	private PolicyDocumentDataFetcher() {
	}

	@Override
	public List<AwsPolicyDocument> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsPolicyDocument> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				IamClientBuilder iamClientBuilder = IamClient.builder()
						// Any region is fine for IAM operations
						.region( account.getRegions().iterator().next() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					iamClientBuilder.httpClient( sdkHttpClient );
				}
				try (IamClient client = iamClientBuilder.build()) {
					for ( Policy policy : client.listPolicies().policies() ) {
						try {
							// Fetch the policy document from the default version
							GetPolicyVersionRequest request = GetPolicyVersionRequest.builder()
									.policyArn( policy.arn() )
									.versionId( policy.defaultVersionId() )
									.build();

							String document = client.getPolicyVersion( request ).policyVersion().document();
							if ( document != null ) {
								// The policy document is URL-encoded, so decode it
								String decodedDocument = URLDecoder.decode( document, "UTF-8" );
								list.add( AwsPolicyDocument.fromJson(
										account.getAccountId(),
										policy.arn(),
										policy.policyName(),
										policy.defaultVersionId(),
										decodedDocument
								) );
							}
						}
						catch (UnsupportedEncodingException e) {
							throw new RuntimeException( "Failed to decode policy document for " + policy.arn(), e );
						}
						catch (Exception e) {
							// Log and continue if a specific policy fails
							System.err.println( "Failed to fetch policy document for " + policy.arn() + ": " + e.getMessage() );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch policy document list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsPolicyDocument.class, AwsConventionContext.INSTANCE );
	}
}
