package com.farpost.ldt;

public class CallCountInterruptionStrategy implements TestInterruptionStrategy {

	private ThreadLocal<Integer> callCount;

	public CallCountInterruptionStrategy(final int callCount) {
		if ( callCount < 1 ) {
			throw new IllegalArgumentException();
		}
		this.callCount = new ThreadLocal<Integer>() {
			@Override
			protected Integer initialValue() {
				return callCount;
			}
		};
	}

	public boolean shouldContinue(long testExecutionTime) {
		Integer count = callCount.get() - 1;
		callCount.set(count);
		return count > 0;
	}
}
