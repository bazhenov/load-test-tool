package com.farpost.ldt.formatter;

import com.farpost.ldt.TestResult;
import com.farpost.ldt.ThreadTestHistory;

import static com.farpost.ldt.Utils.percentile;
import static java.lang.System.arraycopy;

public abstract class AbstractPlainResultFormatter implements ResultFormatter {

	private static final long MS = 1000;
	private static final long SEC = 1000000;
	private static final long MIN = 60000000;

	public void format(TestResult result) {
		long standardDeviation = result.getStandardDeviation();
		int concurrencyLevel = result.getConcurrencyLevel();
		int samplesCount = result.getSamplesCount();
		int failedSamplesCount = result.getFailedSamplesCount();
		long totalTime = result.getTotalTime();
		long minTime = result.getMinTime();
		long maxTime = result.getMaxTime();
		float throughput = result.getThroughput();

		long[] executionTimeVector = mergeExecutionTimes(result);
		long percentile99 = percentile(executionTimeVector, .99);
		long percentile95 = percentile(executionTimeVector, .95);
		long percentile90 = percentile(executionTimeVector, .90);

		writeLine("                     RESULTS                      ");
		writeLine("--------------------------------------------------");
		writeLine(String.format(" %-30s: %d", "Concurrency level", concurrencyLevel));
		if (failedSamplesCount > 0) {
			writeLine(String.format(" %-30s: %d (failed: %d, %d%%)", "Samples count", samplesCount, failedSamplesCount,
				failedSamplesCount * 100 / samplesCount ));
		} else {
			writeLine(String.format(" %-30s: %d", "Samples count", samplesCount));
		}
		writeLine(String.format(" %-30s: %s", "Total time", formatTime(totalTime)));
		writeLine(String.format(" %-30s: %s", "Min. time", formatTime(minTime)));
		writeLine(String.format(" %-30s: %s", "Max. time", formatTime(maxTime)));
		writeLine(String.format(" %-30s: %s", "Std. dev.", formatTime(standardDeviation)));
		writeLine(String.format(" %-30s: %s/%s/%s", "Percentile (90/95/99%)",
			formatTime(percentile90), formatTime(percentile95), formatTime(percentile99)));

		if (throughput > 100) {
			writeLine(String.format(" %-30s: %.0f tps", "Throughput", throughput));
		} else if (throughput > 0) {
			writeLine(String.format(" %-30s: %.1f tps", "Throughput", throughput));
		} else {
			writeLine(String.format(" %-30s: inf.", "Throughput"));
		}
	}

	/**
	 * Returns vector conctatenated execution times of all thread histories in test result.
	 *
	 * @param result test result
	 * @return merged execution times vector
	 */
	private static long[] mergeExecutionTimes(TestResult result) {
		long[] vector = new long[result.getSamplesCount()];
		int offset = 0;
		for (ThreadTestHistory history : result.getThreadsHistory()) {
			long[] threadVector = history.getSamples();
			arraycopy(threadVector, 0, vector, offset, threadVector.length);
			offset += threadVector.length;
		}
		return vector;
	}

	public static String formatTime(long time) {
		if (time >= 1 * MIN) {
			long minutes = time / MIN;
			long seconds = (time % MIN) / SEC;
			return minutes + "m " + seconds + "s";
		} else if (time >= 1 * SEC) {
			double seconds = time / (double)SEC;
			return String.format("%.2fs", seconds);
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
