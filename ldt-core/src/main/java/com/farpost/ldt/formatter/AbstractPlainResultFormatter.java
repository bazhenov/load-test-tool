package com.farpost.ldt.formatter;

import com.farpost.ldt.TestResult;

public abstract class AbstractPlainResultFormatter implements ResultFormatter {

	private static final long MS = 1000;
	private static final long SEC = 1000000;
	private static final long MIN = 60000000;

	public void format(TestResult result) {
		writeLine("                     RESULTS                      ");
		writeLine("--------------------------------------------------");
		writeLine(String.format(" %-30s: %d", "Concurrency level", result.getConcurrencyLevel()));
		writeLine(String.format(" %-30s: %d", "Samples count", result.getThreadSamplesCount()));
		writeLine(String.format(" %-30s: %s", "Total time", formatTime(result.getTotalTime())));
		writeLine(String.format(" %-30s: %s", "Min. time", formatTime(result.getMinTime())));
		writeLine(String.format(" %-30s: %s", "Max. time", formatTime(result.getMaxTime())));
		writeLine(String.format(" %-30s: %s", "Std. dev.", formatTime(result.getStandardDeviation())));
		float throughput = result.getThroughput();
		if (throughput > 100) {
			writeLine(String.format(" %-30s: %.0f tps", "Throughput", throughput));
		} else if (throughput > 0) {
			writeLine(String.format(" %-30s: %.1f tps", "Throughput", throughput));
		} else {
			writeLine(String.format(" %-30s: inf.", "Throughput"));
		}
	}

	public static String formatTime(long time) {
		if (time >= 1 * MIN) {
			long minutes = time / MIN;
			long seconds = (time % MIN) / SEC;
			return minutes + "m " + seconds + "s";
		} else if (time >= 1 * SEC) {
			long seconds = time / SEC;
			long miliseconds = (time % SEC) / MS;
			return seconds + "." + miliseconds + "s";
		} else if (time >= 1 * MS) {
			return (time / MS) + "ms";
		} else if (time >= 1) {
			return "~" + time + "mcs";
		} else {
			return "<1mcs";
		}
	}

	protected abstract void writeLine(String message);
}
