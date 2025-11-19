/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AwsIAMSchemaProviderTest {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new AwsIAMSchemaProvider() );
		builder.registerSchemaObjectAlias( AwsIamUser.class, "AwsIAMUser" );
		builder.registerSchemaObjectAlias( AwsIamRole.class, "AwsIAMRole" );
		builder.registerSchemaObjectAlias( AwsIamGroup.class, "AwsIAMGroup" );
		builder.registerSchemaObjectAlias( AwsIamPasswordPolicy.class, "AwsIAMPasswordPolicy" );
		builder.registerSchemaObjectAlias( AwsIamMfaDevice.class, "AwsIAMMFADevice" );
		builder.registerSchemaObjectAlias( AwsIamVirtualMfaDevice.class, "AwsIAMVirtualMFADevice" );
		builder.registerSchemaObjectAlias( AwsIamAccountSummary.class, "AwsIAMAccountSummary" );
		builder.registerSchemaObjectAlias( AwsIamUserAttachedPolicy.class, "AwsIAMUserAttachedPolicy" );
		builder.registerSchemaObjectAlias( AwsIamUserInlinePolicy.class, "AwsIAMUserInlinePolicy" );
		builder.registerSchemaObjectAlias( AwsIamGroupAttachedPolicy.class, "AwsIAMGroupAttachedPolicy" );
		builder.registerSchemaObjectAlias( AwsIamRoleAttachedPolicy.class, "AwsIAMRoleAttachedPolicy" );
		builder.registerSchemaObjectAlias( AwsIamGroupMembership.class, "AwsIAMGroupMembership" );
		CONTEXT = builder.build();
	}

	@Test
	void should_return_users() {
		try (var session = CONTEXT.createSession()) {
			session.put(
					AwsIamUser.class, Collections.singletonList( TestObjects.userWithMfa() ) );

			var typedQuery =
					session.createQuery( "select u.* from AwsIAMUser u", new TypeReference<Map<String, Object>>() {
					} );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

	@Test
	void should_return_roles() {
		try (var session = CONTEXT.createSession()) {
			session.put(
					AwsIamRole.class, Collections.singletonList( TestObjects.role() ) );

			var typedQuery =
					session.createQuery( "select r.* from AwsIAMRole r", new TypeReference<Map<String, Object>>() {
					} );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

	@Test
	void should_return_password_policy() {
		try (var session = CONTEXT.createSession()) {
			session.put( AwsIamPasswordPolicy.class, TestObjects.defaultAccountPasswordPolicy() );

			var typedQuery =
					session.createQuery( "select p.* from AwsIAMPasswordPolicy p",
							new TypeReference<Map<String, Object>>() {
							} );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

	@Test
	void should_return_mfa_device() {
		try (var session = CONTEXT.createSession()) {
			session.put( AwsIamMfaDevice.class, Collections.singletonList( TestObjects.mfaDevice() ) );

			var typedQuery =
					session.createQuery(
							"select m.* from AwsIAMMFADevice m", new TypeReference<Map<String, Object>>() {
							} );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

	@Test
	void should_return_account_summary() {
		try (var session = CONTEXT.createSession()) {
			session.put( AwsIamAccountSummary.class, Collections.singletonList( TestObjects.accountSummary() ) );

			var typedQuery =
					session.createQuery(
							"select a.* from AwsIAMAccountSummary a", new TypeReference<Map<String, Object>>() {
							} );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

	@Test
	void should_return_user_attached_policy() {
		try (var session = CONTEXT.createSession()) {
			session.put( AwsIamUserAttachedPolicy.class, Collections.singletonList( TestObjects.userAttachedPolicy() ) );

			var typedQuery =
					session.createQuery(
							"select p.* from AwsIAMUserAttachedPolicy p", new TypeReference<Map<String, Object>>() {
							} );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

	@Test
	void should_return_user_inline_policy() {
		try (var session = CONTEXT.createSession()) {
			session.put( AwsIamUserInlinePolicy.class, Collections.singletonList( TestObjects.userInlinePolicy() ) );

			var typedQuery =
					session.createQuery(
							"select p.* from AwsIAMUserInlinePolicy p", new TypeReference<Map<String, Object>>() {
							} );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

	@Test
	void should_return_virtual_mfa_device() {
		try (var session = CONTEXT.createSession()) {
			session.put( AwsIamVirtualMfaDevice.class, Collections.singletonList( TestObjects.virtualMfaDeviceForUser() ) );

			var typedQuery =
					session.createQuery(
							"select v.* from AwsIAMVirtualMFADevice v", new TypeReference<Map<String, Object>>() {
							} );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

	@Test
	void should_return_groups() {
		try (var session = CONTEXT.createSession()) {
			session.put( AwsIamGroup.class, Collections.singletonList( TestObjects.group() ) );

			var typedQuery =
					session.createQuery(
							"select g.* from AwsIAMGroup g", new TypeReference<Map<String, Object>>() {
							} );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

	@Test
	void should_return_group_attached_policy() {
		try (var session = CONTEXT.createSession()) {
			session.put( AwsIamGroupAttachedPolicy.class, Collections.singletonList( TestObjects.groupAttachedPolicy() ) );

			var typedQuery =
					session.createQuery(
							"select p.* from AwsIAMGroupAttachedPolicy p", new TypeReference<Map<String, Object>>() {
							} );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

	@Test
	void should_return_role_attached_policy() {
		try (var session = CONTEXT.createSession()) {
			session.put( AwsIamRoleAttachedPolicy.class, Collections.singletonList( TestObjects.roleAttachedPolicy() ) );

			var typedQuery =
					session.createQuery(
							"select p.* from AwsIAMRoleAttachedPolicy p", new TypeReference<Map<String, Object>>() {
							} );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

	@Test
	void should_return_group_membership() {
		try (var session = CONTEXT.createSession()) {
			session.put( AwsIamGroupMembership.class, Collections.singletonList( TestObjects.groupMembership() ) );

			var typedQuery =
					session.createQuery(
							"select m.* from AwsIAMGroupMembership m", new TypeReference<Map<String, Object>>() {
							} );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}
}
