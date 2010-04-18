package com.farpost.ldt;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ThreadTestResultTest {

  @Test
  public void sampleCanRegisterExecutionTime() throws InterruptedException {
    long[] samples = {13, 14, 16};
    ThreadTestHistory sample = new ThreadTestHistory();
    sample.registerSample(13);
    sample.registerSample(14);
    sample.registerSample(16);
    assertThat(sample.getSamples(), equalTo(samples));
    assertThat(sample.getTotalTime(), equalTo(43L));
  }
}
