package com.farpost.ldt;

/**
 * {@link com.farpost.ldt.TestInterruptionStrategy} implementation which interrupts test when
 * test overall execution time reaches given value. Timeframe specified in milliseconds.
 */
public class TimeFrameInterruptionStrategy implements TestInterruptionStrategy {

	private final ThreadLocal<Long> executionTime;
	private final long maximumExecutionTime;

	/**
	 * Create new timeframe based interruption strategy with given timeframe
	 *
	 * @param maximumExecutionTime timeframe in milliseconds
	 */
	public TimeFrameInterruptionStrategy(long maximumExecutionTime) {
		this.maximumExecutionTime = maximumExecutionTime;
		if ( maximumExecutionTime <= 0 ) {
			throw new IllegalArgumentException();
		}
		this.executionTime = new ThreadLocal<Long>() {
			@Override
			protected Long initialValue() {
				return 0L;
			}
		};
	}

	public boolean shouldContinue(long testExecutionTime) {
		executionTime.set(executionTime.get() + testExecutionTime);
		return (executionTime.get()/1000) < maximumExecutionTime;
	}
}
