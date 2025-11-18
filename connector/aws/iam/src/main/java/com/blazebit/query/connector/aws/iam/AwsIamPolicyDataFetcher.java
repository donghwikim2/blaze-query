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
import software.amazon.awssdk.services.iam.model.ListPoliciesRequest;
import software.amazon.awssdk.services.iam.model.Policy;
import software.amazon.awssdk.services.iam.model.PolicyScopeType;

import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsIamPolicyDataFetcher implements DataFetcher<AwsIamPolicy>, Serializable {

	public static final AwsIamPolicyDataFetcher INSTANCE = new AwsIamPolicyDataFetcher();

	private AwsIamPolicyDataFetcher() {
	}

	@Override
	public List<AwsIamPolicy> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsIamPolicy> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				IamClientBuilder iamClientBuilder = IamClient.builder()
						// Any region is fine for IAM operations
						.region( account.getRegions().iterator().next() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					iamClientBuilder.httpClient( sdkHttpClient );
				}
				try (IamClient client = iamClientBuilder.build()) {
					// Fetch only customer managed policies (not AWS managed)
					var listPoliciesRequest = ListPoliciesRequest.builder()
							.scope( PolicyScopeType.LOCAL )
							.build();

					for ( Policy policy : client.listPoliciesPaginator( listPoliciesRequest ).policies() ) {
						// Get the default version of the policy
						var getPolicyVersionRequest = GetPolicyVersionRequest.builder()
								.policyArn( policy.arn() )
								.versionId( policy.defaultVersionId() )
								.build();

						var policyVersion = client.getPolicyVersion( getPolicyVersionRequest );
						String policyDocument = URLDecoder.decode(
								policyVersion.policyVersion().document(),
								StandardCharsets.UTF_8
						);

						list.add( AwsIamPolicy.fromJson(
								account.getAccountId(),
								policy.arn(),
								policy.policyName(),
								policy.policyId(),
								policy.defaultVersionId(),
								policyDocument
						) );
					}
				}
			}
			return list;
		}
		catch (Exception e) {
			throw new DataFetcherException( "Could not fetch IAM policy list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsIamPolicy.class, AwsConventionContext.INSTANCE );
	}
}
