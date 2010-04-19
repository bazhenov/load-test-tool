package com.farpost.ldt;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CallCountInterruptionStrategyTest {

	@Test(expectedExceptions = {IllegalArgumentException.class})
	public void shouldGenerateExceptionOnNegativeCallCount() {
		new CallCountInterruptionStrategy(-1);
	}

	@Test(expectedExceptions = {IllegalArgumentException.class})
	public void shouldGenerateExceptionOnZeroCallCount() {
		new CallCountInterruptionStrategy(0);
	}

	@Test
	public void shouldInterruptExecutionAfterCallCountReached() {
		TestInterruptionStrategy strategy = new CallCountInterruptionStrategy(3);
		assertThat(strategy.shouldContinue(1), equalTo(true));
		assertThat(strategy.shouldContinue(1), equalTo(true));
		assertThat(strategy.shouldContinue(1), equalTo(false));

	}
}
