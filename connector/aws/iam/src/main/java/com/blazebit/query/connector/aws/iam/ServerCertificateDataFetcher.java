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
import software.amazon.awssdk.services.iam.model.ServerCertificateMetadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ServerCertificateDataFetcher implements DataFetcher<AwsServerCertificate>, Serializable {

	public static final ServerCertificateDataFetcher INSTANCE = new ServerCertificateDataFetcher();

	private ServerCertificateDataFetcher() {
	}

	@Override
	public List<AwsServerCertificate> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsServerCertificate> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				IamClientBuilder iamClientBuilder = IamClient.builder()
						// Any region is fine for IAM operations
						.region( account.getRegions().iterator().next() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					iamClientBuilder.httpClient( sdkHttpClient );
				}
				try (IamClient client = iamClientBuilder.build()) {
					for ( ServerCertificateMetadata cert : client.listServerCertificates().serverCertificateMetadataList() ) {
						StringTokenizer tokenizer = new StringTokenizer( cert.arn(), ":" );
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
						list.add( new AwsServerCertificate(
								account.getAccountId(),
								resourceId,
								cert
						) );
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch server certificate list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsServerCertificate.class, AwsConventionContext.INSTANCE );
	}
}
