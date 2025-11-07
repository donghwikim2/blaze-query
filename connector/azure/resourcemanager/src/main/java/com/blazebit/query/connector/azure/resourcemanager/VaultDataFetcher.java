/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.keyvault.models.Vault;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class VaultDataFetcher implements DataFetcher<AzureResourceVault>, Serializable {

	public static final VaultDataFetcher INSTANCE = new VaultDataFetcher();

	private VaultDataFetcher() {
	}

	@Override
	public List<AzureResourceVault> fetch(DataFetchContext context) {
		List<AzureResourceManager> resourceManagers;
		try {
			resourceManagers = AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll( context );
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch Azure Resource Managers", e );
		}

		List<AzureResourceVault> list = new ArrayList<>();
		List<? extends AzureResourceManagerResourceGroup> resourceGroups;
		try {
			resourceGroups = context.getSession().getOrFetch( AzureResourceManagerResourceGroup.class );
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch resource groups", e );
		}

		for ( AzureResourceManager resourceManager : resourceManagers ) {
			for ( AzureResourceManagerResourceGroup resourceGroup : resourceGroups ) {
				if ( resourceManager.tenantId().equals( resourceGroup.getTenantId() ) ) {
					try {
						for ( Vault vault : resourceManager.vaults()
								.listByResourceGroup( resourceGroup.getResourceGroupName() ) ) {
							try {
								list.add( new AzureResourceVault(
										resourceManager.tenantId(),
										vault.id(),
										vault.innerModel()
								) );
							}
							catch (RuntimeException e) {
								throw new DataFetcherException(
										String.format( "Could not process vault '%s' in resource group '%s'",
												vault.id(), resourceGroup.getResourceGroupName() ),
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
								String.format( "Could not list vaults in resource group '%s' for tenant '%s'",
										resourceGroup.getResourceGroupName(), resourceManager.tenantId() ),
								e );
					}
				}
			}
		}
		return list;
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AzureResourceVault.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}
}
