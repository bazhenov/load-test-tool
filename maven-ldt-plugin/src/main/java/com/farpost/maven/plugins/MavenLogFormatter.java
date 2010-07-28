package com.farpost.maven.plugins;

import com.farpost.ldt.TestResult;
import com.farpost.ldt.formatter.ResultFormatter;
import org.apache.maven.plugin.logging.Log;

import java.io.PrintStream;

import static com.farpost.ldt.formatter.PlainResultFormatter.formatTime;

public class MavenLogFormatter implements ResultFormatter {
	private static final long MS = 1000;
	private static final long SEC = 1000000;
	private static final long MIN = 60000000;

	private final Log out;

	public MavenLogFormatter(Log out) {
		this.out = out;
	}

	public void format(TestResult result) {
		out.info("                     RESULTS                      ");
		out.info("--------------------------------------------------");
		out.info(String.format(" %-30s: %d", "Concurrency level", result.getConcurrencyLevel()));
		out.info(String.format(" %-30s: %d", "Samples count", result.getThreadSamplesCount()));
		out.info(String.format(" %-30s: %s", "Total time", formatTime(result.getTotalTime())));
		out.info(String.format(" %-30s: %s", "Min. time", formatTime(result.getMinTime())));
		out.info(String.format(" %-30s: %s", "Max. time", formatTime(result.getMaxTime())));
		out.info(String.format(" %-30s: %s", "Std. dev.", formatTime(result.getStandardDeviation())));
		float throughput = result.getThroughput();
		if (throughput > 100) {
			out.info(String.format(" %-30s: %.0f tps", "Throughput", throughput));
		} else if (throughput > 0) {
			out.info(String.format(" %-30s: %.1f tps", "Throughput", throughput));
		} else {
			out.info(String.format(" %-30s: inf.", "Throughput"));
		}
	}
}