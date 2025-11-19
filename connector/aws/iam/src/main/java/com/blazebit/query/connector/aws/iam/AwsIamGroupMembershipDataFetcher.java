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
import software.amazon.awssdk.services.iam.model.Group;
import software.amazon.awssdk.services.iam.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Data fetcher for IAM group memberships (which users belong to which groups).
 *
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsIamGroupMembershipDataFetcher implements DataFetcher<AwsIamGroupMembership>, Serializable {

	public static final AwsIamGroupMembershipDataFetcher INSTANCE = new AwsIamGroupMembershipDataFetcher();

	private AwsIamGroupMembershipDataFetcher() {
	}

	@Override
	public List<AwsIamGroupMembership> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsIamGroupMembership> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				IamClientBuilder iamClientBuilder = IamClient.builder()
						// Any region is fine for IAM operations
						.region( account.getRegions().iterator().next() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					iamClientBuilder.httpClient( sdkHttpClient );
				}
				try (IamClient client = iamClientBuilder.build()) {
					// Get all groups
					for ( Group group : client.listGroupsPaginator().groups() ) {
						// For each group, list users in the group
						for ( User user : client.getGroupPaginator(
								builder -> builder.groupName( group.groupName() )
						).users() ) {
							list.add( AwsIamGroupMembership.from(
									account.getAccountId(),
									group.groupName(),
									user
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch group memberships", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( AwsIamGroupMembership.class, AwsConventionContext.INSTANCE );
	}
}
