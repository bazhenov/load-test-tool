package com.farpost.ldt;

import java.util.*;

public class TestResult {

	private final int concurrencyLevel;
	private final List<ThreadTestHistory> threadsHistory;
	private long totalTime;
	private final int threadSamplesCount;
  private long maxTime = 0;
	private long minTime = Long.MAX_VALUE;

	public TestResult(List<ThreadTestHistory> threadsHistory, int threadSamplesCount) {
		this.threadsHistory = threadsHistory;
		this.concurrencyLevel = threadsHistory.size();
		this.threadSamplesCount = threadSamplesCount;
		for ( ThreadTestHistory threadHistory : threadsHistory ) {
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

	public List<ThreadTestHistory> getThreadsHistory() {
		return threadsHistory;
	}

	public int getThreadSamplesCount() {
		return threadSamplesCount;
	}

	public float getThroughput() {
		long avarageTime = (maxTime + minTime)/2;
		return totalTime == 0
			? 0
			: 1000f / totalTime * threadSamplesCount * concurrencyLevel;
	}
}
