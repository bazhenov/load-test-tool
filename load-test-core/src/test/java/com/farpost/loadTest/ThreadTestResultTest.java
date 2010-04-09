package com.farpost.loadTest;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ThreadTestResultTest {

  @Test
  public void sampleCanRegisterExecutionTime() throws InterruptedException {
    long[] samples = {13, 14, 16};
    ThreadTestHistory sample = new ThreadTestHistory(3);
    sample.registerSample(13);
    sample.registerSample(14);
    sample.registerSample(16);
    assertThat(sample.getSamples(), equalTo(samples));
    assertThat(sample.getTotalTime(), equalTo(43L));
  }

  @Test(expectedExceptions = UnexpectedSampleException.class)
  public void sampleShouldGenerateExceptionIfSampleCountOverflow() {
    ThreadTestHistory sample = new ThreadTestHistory(1);
    sample.registerSample(15);
    sample.registerSample(16);
  }

  @Test(expectedExceptions = UnfinishedThreadHistoryException.class)
  public void sampleShouldGenerateExceptionIfHistoryNotFinished() {
    ThreadTestHistory sample = new ThreadTestHistory(1);
    sample.getSamples();
  }

  @Test
  public void threadHistoryCanBeFinishedOrUnfinished() {
    ThreadTestHistory history = new ThreadTestHistory(1);
    assertThat(history.isFinished(), equalTo(false));
    history.registerSample(15);
    assertThat(history.isFinished(), equalTo(true));
  }
}
