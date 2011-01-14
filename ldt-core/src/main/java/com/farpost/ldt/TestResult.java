package com.farpost.ldt;

import java.util.*;

import static com.farpost.ldt.Utils.calculateStdDev;
import static java.util.Arrays.asList;

public class TestResult {

	private final int concurrencyLevel;
	private final List<ThreadTestHistory> threadsHistory;
	private final long stdDev;
	private long totalTime;

	private int samplesCount;
	private long maxTime = 0;
	private long minTime = Long.MAX_VALUE;
	private int failedSamplesCount;

	public TestResult(List<ThreadTestHistory> threadsHistory) {
		this.threadsHistory = threadsHistory;
		this.concurrencyLevel = threadsHistory.size();
		long[][] executionTimes = new long[concurrencyLevel][];
		for (int i = 0; i < threadsHistory.size(); i++) {
			ThreadTestHistory threadHistory = threadsHistory.get(i);
			executionTimes[i] = threadHistory.getSamples();
			mergeHistory(threadHistory);
		}
		stdDev = (long) calculateStdDev(executionTimes);
	}

	public TestResult(ThreadTestHistory... threadsHistory) {
		this(asList(threadsHistory));
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
		samplesCount += threadHistory.getSamplesCount();
		failedSamplesCount += threadHistory.getFailedSamplesCount();

		if (threadHistory.getTotalTime() > totalTime) {
			totalTime = threadHistory.getTotalTime();
		}
		if (threadHistory.getMaxTime() > maxTime) {
			maxTime = threadHistory.getMaxTime();
		}
		if (threadHistory.getMinTime() < minTime) {
			minTime = threadHistory.getMinTime();
		}
	}

	public List<ThreadTestHistory> getThreadsHistory() {
		return threadsHistory;
	}

	public long getStandardDeviation() {
		return stdDev;
	}

	public int getSamplesCount() {
		return samplesCount;
	}

	public int getFailedSamplesCount() {
		return failedSamplesCount;
	}

	public float getThroughput() {
		return totalTime == 0
			? 0
			: 1000000f / totalTime * samplesCount;
	}
}
