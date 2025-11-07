/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.storage.models.StorageAccount;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class StorageAccountDataFetcher implements DataFetcher<AzureResourceStorageAccount>, Serializable {

	public static final StorageAccountDataFetcher INSTANCE = new StorageAccountDataFetcher();

	private StorageAccountDataFetcher() {
	}

	@Override
	public List<AzureResourceStorageAccount> fetch(DataFetchContext context) {
		List<AzureResourceManager> resourceManagers;
		try {
			resourceManagers = AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll( context );
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch Azure Resource Managers", e );
		}

		List<AzureResourceStorageAccount> list = new ArrayList<>();
		for ( AzureResourceManager resourceManager : resourceManagers ) {
			try {
				for ( StorageAccount storageAccount : resourceManager.storageAccounts().list() ) {
					try {
						list.add( new AzureResourceStorageAccount(
								resourceManager.tenantId(),
								storageAccount.id(),
								storageAccount.innerModel()
						) );
					}
					catch (RuntimeException e) {
						throw new DataFetcherException(
								String.format( "Could not process storage account '%s'", storageAccount.id() ),
								e );
					}
				}
			}
			catch (DataFetcherException e) {
				// Re-throw DataFetcherException as-is to preserve detailed error messages
				throw e;
			}
			catch (RuntimeException e) {
				throw new DataFetcherException(
						String.format( "Could not list storage accounts for tenant '%s'", resourceManager.tenantId() ),
						e );
			}
		}
		return list;
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AzureResourceStorageAccount.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}
}
