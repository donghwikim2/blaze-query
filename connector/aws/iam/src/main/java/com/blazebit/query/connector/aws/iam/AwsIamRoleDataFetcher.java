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
import software.amazon.awssdk.services.iam.model.Role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Data fetcher that exposes AWS IAM roles.
 *
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsIamRoleDataFetcher implements DataFetcher<AwsIamRole>, Serializable {

	public static final AwsIamRoleDataFetcher INSTANCE = new AwsIamRoleDataFetcher();

	private AwsIamRoleDataFetcher() {
	}

	@Override
	public List<AwsIamRole> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsIamRole> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				IamClientBuilder iamClientBuilder = IamClient.builder()
						// Any region is fine for IAM operations
						.region( account.getRegions().iterator().next() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					iamClientBuilder.httpClient( sdkHttpClient );
				}
				try (IamClient client = iamClientBuilder.build()) {
					for ( Role role : client.listRolesPaginator().roles() ) {
						StringTokenizer tokenizer = new StringTokenizer( user.arn(), ":" );
						// arn
						tokenizer.nextToken();
						// aws
						tokenizer.nextToken();
						// iam
						tokenizer.nextToken();
						// empty region
						tokenizer.nextToken();
						// resource id
						String resourceId = tokenizer.nextToken();

						list.add( new AwsIamRole(
								account.getAccountId(),
								resourceId,
								role
						) );
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch role list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsIamRole.class, AwsConventionContext.INSTANCE );
	}

	private static String resolveResourceId(String arn) {
		int colonCount = 0;
		for ( int i = 0; i < arn.length(); i++ ) {
			if ( arn.charAt( i ) == ':' ) {
				colonCount++;
				if ( colonCount == 5 && i + 1 < arn.length() ) {
					return arn.substring( i + 1 );
				}
			}
		}
		return arn;
	}
}
