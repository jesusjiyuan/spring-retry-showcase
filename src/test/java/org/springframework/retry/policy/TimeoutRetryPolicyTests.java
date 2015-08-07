/*
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.retry.policy;

import org.junit.Test;
import org.springframework.retry.RetryContext;

import static org.junit.Assert.*;

public class TimeoutRetryPolicyTests {

	@Test
	public void testTimeoutPreventsRetry() throws Exception {
		TimeoutRetryPolicy policy = new TimeoutRetryPolicy();
		policy.setTimeout(100);
		RetryContext context = policy.open(null);
		policy.registerThrowable(context, new Exception());
		assertTrue(policy.canRetry(context));
		Thread.sleep(200);
		assertFalse(policy.canRetry(context));
		policy.close(context);
	}

	@Test
	public void testRetryCount() throws Exception {
		TimeoutRetryPolicy policy = new TimeoutRetryPolicy();
		RetryContext context = policy.open(null);
		assertNotNull(context);
		policy.registerThrowable(context, null);
		assertEquals(0, context.getRetryCount());
		policy.registerThrowable(context, new RuntimeException("foo"));
		assertEquals(1, context.getRetryCount());
		assertEquals("foo", context.getLastThrowable().getMessage());
		policy.registerThrowable(context, new Exception("exceptionMessage"));
		assertEquals(2, context.getRetryCount());
		assertEquals("exceptionMessage", context.getLastThrowable().getMessage());
	}

	@Test
	public void testParent() throws Exception {
		TimeoutRetryPolicy policy = new TimeoutRetryPolicy();
		RetryContext context = policy.open(null);
		RetryContext child = policy.open(context);
		assertNotSame(child, context);
		assertSame(context, child.getParent());
	}

}
