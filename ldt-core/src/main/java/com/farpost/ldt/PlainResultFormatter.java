package com.farpost.ldt;

import java.io.PrintStream;

public class PlainResultFormatter implements ResultFormatter {
	private static final long MS = 1000;
	private static final long SEC = 1000000;
	private static final long MIN = 60000000;

	private final PrintStream out;


	public PlainResultFormatter(PrintStream out) {
		this.out = out;
	}

	public void format(TestResult result) {
		out.println("                     RESULTS                      ");
		out.println("--------------------------------------------------");
		out.println(String.format(" %-30s: %d", "Concurrency level", result.getConcurrencyLevel()));
		out.println(String.format(" %-30s: %d", "Samples count (per thread)", result.getThreadSamplesCount()));
		out.println(String.format(" %-30s: %s", "Total time", formatTime(result.getTotalTime())));
		out.println(String.format(" %-30s: %s", "Min. time", formatTime(result.getMinTime())));
		out.println(String.format(" %-30s: %s", "Max. time", formatTime(result.getMaxTime())));
		float throughput = result.getThroughput();
		if (throughput > 100) {
			out.println(String.format(" %-30s: %.0f tps", "Throughput", throughput));
		} else if (throughput > 0) {
			out.println(String.format(" %-30s: %.1f tps", "Throughput", throughput));
		} else {
			out.println(String.format(" %-30s: inf.", "Throughput"));
		}
	}

	static String formatTime(long time) {
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
}
