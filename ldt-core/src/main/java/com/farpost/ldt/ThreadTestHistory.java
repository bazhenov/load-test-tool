package com.farpost.ldt;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used by {@link com.farpost.ldt.TestRunner} to track thread execution results.
 * Resulting {@link com.farpost.ldt.TestResult} object us built upon a collection of such objects.
 * <pre>
 * --- idle
 * *** test execution
 *          5ms     9ms     4ms     13ms
 * T1  |-*****-*********-****-*************-|
 *         3ms  6ms     7ms      11ms
 * T2  |-***-******-*******-***********-----|
 * </pre>
 * There is sample thread execution history above. There is exactly two <code>ThreadTestHistory</code>
 * objects with 4 test execution history each.
 */
public class ThreadTestHistory {

	private final List<Long> samples = new ArrayList<Long>();
	private long maximumExecutionTime = 0;
	private long minimumExecutionTime = 0;

	/**
	 * @return total execution time in microseconds
	 */
	public long getTotalTime() {
		long totalExecutionTime = 0;
		for (long sample : samples) {
			totalExecutionTime += sample;
		}
		return totalExecutionTime;
	}

	public long[] getSamples() {
		long[] result = new long[samples.size()];
		for (int i = 0; i < samples.size(); i++) {
			result[i] = samples.get(i);
		}
		return result;
	}

	public void registerSample(long time) {
		if (samples.isEmpty()) {
			maximumExecutionTime = minimumExecutionTime = time;
		}else if (time > maximumExecutionTime) {
			maximumExecutionTime = time;
		}else if (time < minimumExecutionTime) {
			minimumExecutionTime = time;
		}
		samples.add(time);
	}

	public long getMaximumExecutionTime() {
		return maximumExecutionTime;
	}

	public long getMinimumExecutionTime() {
		return minimumExecutionTime;
	}

	public int getSamplesCount() {
		return samples.size();
	}
}
