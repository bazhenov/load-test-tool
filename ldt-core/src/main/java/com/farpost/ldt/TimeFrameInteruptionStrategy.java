package com.farpost.ldt;

/**
 * {@link com.farpost.ldt.TestInterruptionStrategy} implementation which interrupts test when
 * test overall execution time reaches given value.
 */
public class TimeFrameInteruptionStrategy implements TestInterruptionStrategy {

	private final ThreadLocal<Long> executionTime;
	private final long maximumExecutionTime;

	public TimeFrameInteruptionStrategy(long maximumExecutionTime) {
		this.maximumExecutionTime = maximumExecutionTime;
		if ( maximumExecutionTime <= 0 ) {
			throw new IllegalArgumentException();
		}
		this.executionTime = new ThreadLocal<Long>() {
			@Override
			protected Long initialValue() {
				return 0l;
			}
		};
	}

	public boolean shouldContinue(long testExecutionTime) {
		Long executionTimeSum = executionTime.get() + testExecutionTime;
		executionTime.set(executionTimeSum);
		return executionTimeSum < maximumExecutionTime;
	}
}
