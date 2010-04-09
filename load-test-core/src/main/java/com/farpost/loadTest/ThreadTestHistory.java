package com.farpost.loadTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Этот класс используется в {@link TestRunner} для того чтобы отслеживать результаты
 * тестов в отдельном потоке (worker'е). Результирующий {@link TestResult} конструируется на
 * основании объектов данного типа.
 *
 * <pre>
 * --- idle
 * *** test execution
 *
 *          5ms     9ms     4ms     13ms
 * T1  |---*****-*********-****-*************--|
 *
 *         3ms  6ms     7ms      11ms
 * T2  |---***-******-*******-***********------|
 * </pre>
 * Выше приведена история выполнения двух нитей, каждая из которых выполнила 4 теста с
 * соответствующими временными задержками. Данный класс описывает историю выполнения тестов
 * одной отдельно взятой нити.
 */
public class ThreadTestHistory {

  private final long[] samples;
  private int currentSample = 0;

  public ThreadTestHistory(int samplesCount) {
    this.samples = new long[samplesCount];
  }

  public long getTotalTime() {
    long totalExecutionTime = 0;
    for (long sample : samples) {
      totalExecutionTime += sample;
    }
    return totalExecutionTime;
  }

  public long[] getSamples() {
    if ( !isFinished() ) {
      throw new UnfinishedThreadHistoryException();
    }
    return samples;
  }

  public boolean isFinished() {
    return currentSample >= samples.length;
  }

  public void registerSample(long time) {
    if (currentSample >= samples.length) {
      throw new UnexpectedSampleException();
    }
    samples[currentSample++] = time;
  }
}
