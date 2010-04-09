package com.farpost.loadTest;

import java.util.*;

public class TestResult {

	private final int concurrencyLevel;
	private final List<ThreadTestHistory> history;
	private long totalTime;
	private final int threadSamplesCount;
  private long maxTime = 0;
	private long minTime = Long.MAX_VALUE;

	public TestResult(List<ThreadTestHistory> history, int threadSamplesCount) {
		this.history = history;
		this.concurrencyLevel = history.size();
		this.threadSamplesCount = threadSamplesCount;
		for ( ThreadTestHistory threadHistory : history ) {
			mergeHistory(threadHistory);
		}
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
		if ( threadHistory.getTotalTime() > totalTime ) {
			totalTime = threadHistory.getTotalTime();
		}
		for ( long time : threadHistory.getSamples() ) {
			if ( time < minTime ) {
				minTime = time;
			}
			if ( time > maxTime ) {
				maxTime = time;
			}
		}
	}

	public int getThreadSamplesCount() {
		return threadSamplesCount;
	}

	public float getThroughput() {
		long avarageTime = (maxTime + minTime)/2;
		return avarageTime == 0
			? 0
			: 1000f / avarageTime * concurrencyLevel;
	}
}
