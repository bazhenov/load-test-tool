package com.farpost.ldt;

import java.util.*;

public class TestResult {

	private final int concurrencyLevel;
	private final List<ThreadTestHistory> threadsHistory;
	private final long stdDev;
	private long totalTime;

	private int threadSamplesCount;
	private long maxTime = 0;
	private long minTime = Long.MAX_VALUE;

	public TestResult(List<ThreadTestHistory> threadsHistory) {
		this.threadsHistory = threadsHistory;
		this.concurrencyLevel = threadsHistory.size();
		long[][] executionTimes = new long[concurrencyLevel][];
		for (int i = 0; i < threadsHistory.size(); i++) {
			ThreadTestHistory threadHistory = threadsHistory.get(i);
			executionTimes[i] = threadHistory.getSamples();
			mergeHistory(threadHistory);
		}
		stdDev = (long) TestRunner.calculateStdDev(executionTimes);
	}

	public int getConcurrencyLevel() {
		return concurrencyLevel;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public long getMinTime() {
		return minTime;
	}

	public long getMaxTime() {
		return maxTime;
	}

	private void mergeHistory(ThreadTestHistory threadHistory) {
		threadSamplesCount += threadHistory.getSamples().length;
		if (threadHistory.getTotalTime() > totalTime) {
			totalTime = threadHistory.getTotalTime();
		}
		for (long time : threadHistory.getSamples()) {
			if (time < minTime) {
				minTime = time;
			}
			if (time > maxTime) {
				maxTime = time;
			}
		}
	}

	public List<ThreadTestHistory> getThreadsHistory() {
		return threadsHistory;
	}

	public long getStandardDeviation() {
		return stdDev;
	}

	public int getThreadSamplesCount() {
		return threadSamplesCount;
	}

	public float getThroughput() {
		return totalTime == 0
			? 0
			: 1000000f / totalTime * threadSamplesCount;
	}
}
