package com.farpost.ldt;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TimeFrameInteruptionStrategyTest {

	@Test(expectedExceptions = {IllegalArgumentException.class})
	public void shoulfGenerateExceptionOnNegativeTime() {
		new TimeFrameInteruptionStrategy(-1);
	}

	@Test(expectedExceptions = {IllegalArgumentException.class})
	public void shoulfGenerateExceptionOnZeroTime() {
		new TimeFrameInteruptionStrategy(0);
	}

	@Test
	public void shouldInterruptWhenExecutionTimeSumReachesGivenValue() {
		TestInterruptionStrategy strategy = new TimeFrameInteruptionStrategy(6000);
		assertThat(strategy.shouldContinue(3000000), equalTo(true));
		assertThat(strategy.shouldContinue(2000000), equalTo(true));
		assertThat(strategy.shouldContinue(2000000), equalTo(false));
	}

}
