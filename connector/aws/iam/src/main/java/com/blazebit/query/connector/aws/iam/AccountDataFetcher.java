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
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.StsClientBuilder;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AccountDataFetcher implements DataFetcher<AwsAccount>, Serializable {

	public static final AccountDataFetcher INSTANCE = new AccountDataFetcher();

	private AccountDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsAccount.class, AwsConventionContext.INSTANCE );
	}

	@Override
	public List<AwsAccount> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsAccount> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				StsClientBuilder stsClientBuilder = StsClient.builder()
						.region( account.getRegions().iterator().next() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					stsClientBuilder.httpClient( sdkHttpClient );
				}
				try (StsClient client = stsClientBuilder.build()) {
					GetCallerIdentityResponse response = client.getCallerIdentity();
					list.add( new AwsAccount(
							response.account(),
							response.arn()
					) );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch account information", e );
		}
	}
}
