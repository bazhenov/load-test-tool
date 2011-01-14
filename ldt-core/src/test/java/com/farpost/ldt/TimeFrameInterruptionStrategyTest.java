package com.farpost.ldt;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TimeFrameInterruptionStrategyTest {

	@Test(expectedExceptions = {IllegalArgumentException.class})
	public void shoulfGenerateExceptionOnNegativeTime() {
		new TimeFrameInterruptionStrategy(-1);
	}

	@Test(expectedExceptions = {IllegalArgumentException.class})
	public void shoulfGenerateExceptionOnZeroTime() {
		new TimeFrameInterruptionStrategy(0);
	}

	@Test
	public void shouldInterruptWhenExecutionTimeSumReachesGivenValue() {
		TestInterruptionStrategy strategy = new TimeFrameInterruptionStrategy(6000);
		assertThat(strategy.shouldContinue(3000000), equalTo(true));
		assertThat(strategy.shouldContinue(2000000), equalTo(true));
		assertThat(strategy.shouldContinue(2000000), equalTo(false));
	}

	@Test
	public void shouldInterruptTestEvenIfItIsCompleteVeryQuickly() {
		TestInterruptionStrategy strategy = new TimeFrameInterruptionStrategy(1);
		assertThat(strategy.shouldContinue(486), equalTo(true));
		assertThat(strategy.shouldContinue(535), equalTo(false));
	}

}
