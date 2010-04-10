package com.farpost.ldt;

import java.io.PrintStream;
import java.util.List;

public class ElapsedTimeLogResultFormatter implements ResultFormatter {

	private PrintStream out;

	public ElapsedTimeLogResultFormatter(PrintStream out) {
		this.out = out;
	}

	public void format(TestResult result) {
		List<ThreadTestHistory> threadsHistory = result.getThreadsHistory();
		int historyLength = result.getThreadSamplesCount();
		for (int i = 0; i < historyLength; i++) {
			for (ThreadTestHistory threadHistory : threadsHistory) {
				out.println(threadHistory.getSamples()[i]);
			}
		}
	}
}
