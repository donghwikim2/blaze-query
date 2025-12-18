/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.base;

import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcherConfig;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.regions.Region;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Configuration properties for the AWS {@link com.blazebit.query.spi.DataFetcher} instances.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AwsConnectorConfig {

	/**
	 * Specifies the {@link Account} to use for querying data.
	 */
	public static final DataFetcherConfig<Account> ACCOUNT = DataFetcherConfig.forPropertyName( "awsAccount" );
	/**
	 * Specifies the {@link Account} to use for querying data from global services like IAM and Route53.
	 * This config automatically deduplicates accounts by account ID, since global services
	 * return the same data regardless of region.
	 */
	public static final DataFetcherConfig<Account> GLOBAL_ACCOUNT = new DataFetcherConfig<>() {
		@Override
		public String getPropertyName() {
			return "awsAccount";
		}

		@Override
		public Account find(DataFetchContext context) {
			return context.findProperty( "awsAccount" );
		}

		@Override
		public List<Account> findAll(DataFetchContext context) {
			List<Account> accounts = ACCOUNT.findAll( context );
			Map<String, Account> unique = new LinkedHashMap<>();
			for ( Account account : accounts ) {
				unique.putIfAbsent( account.getAccountId(), account );
			}
			return new ArrayList<>( unique.values() );
		}

		@Override
		public String toString() {
			return "DataFetcherConfig(awsGlobalAccount)";
		}
	};
	/**
	 * Specifies the {@link SdkHttpClient} to use for querying data.
	 */
	public static final DataFetcherConfig<SdkHttpClient> HTTP_CLIENT = DataFetcherConfig.forPropertyName(
			"awsHttpClient" );

	private AwsConnectorConfig() {
	}

	/**
	 * Account configuration.
	 *
	 * @author Christian Beikov
	 * @since 1.0.0
	 */
	public static final class Account {
		private final String accountId;
		private final Set<Region> regions;
		private final AwsCredentialsProvider credentialsProvider;

		/**
		 * Create a new account.
		 *
		 * @param regions The region to use for the account.
		 * @param credentialsProvider The credentials to use.
		 */
		public Account(String accountId, Set<Region> regions, AwsCredentialsProvider credentialsProvider) {
			this.accountId = accountId;
			this.regions = regions;
			this.credentialsProvider = credentialsProvider;
		}

		/**
		 * Returns the account id.
		 *
		 * @return the account id
		 */
		public String getAccountId() {
			return accountId;
		}

		/**
		 * Returns the AWS regions for the account.
		 *
		 * @return the AWS regions for the account
		 */
		public Set<Region> getRegions() {
			return regions;
		}

		/**
		 * Returns the AWS credentials provider for the account.
		 *
		 * @return the AWS credentials provider for the account
		 */
		public AwsCredentialsProvider getCredentialsProvider() {
			return credentialsProvider;
		}
	}
}
