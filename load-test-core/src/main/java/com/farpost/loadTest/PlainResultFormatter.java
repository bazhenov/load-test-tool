package com.farpost.loadTest;

import java.io.PrintStream;

public class PlainResultFormatter implements ResultFormatter {
  private final PrintStream out;

  public PlainResultFormatter(PrintStream out) {
    this.out = out;
  }

  public void format(TestResult result) {
    out.println("                     RESULTS                      ");
    out.println("==================================================");
    out.println(String.format("%30s: %d", "Concurrency level", result.getConcurrencyLevel()));
    out.println(String.format("%30s: %d", "Samples count (per thread)", result.getThreadSamplesCount()));
    out.println(String.format("%30s: %d ms", "Total time", result.getTotalTime()));
    out.println(String.format("%30s: %d ms", "Min. time", result.getMinTime()));
    out.println(String.format("%30s: %d ms", "Max. time", result.getMaxTime()));
    out.println(String.format("%30s: %.1f r/s", "Throughput", result.getThroughput()));
    out.println("==================================================");
  }
}
