package com.farpost.ldt;

public class CallCountInterruptionStrategy implements TestInterruptionStrategy {

	private final ThreadLocal<Integer> callCount;

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
		assert this.callCount.get() > 0;
	}

	public boolean shouldContinue(long testExecutionTime) {
		assert this.callCount.get() > 0 : "OOps: "+this.callCount.get();
		Integer count = callCount.get() - 1;
		callCount.set(count);
		return count > 0;
	}
}
