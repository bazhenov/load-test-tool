package com.farpost.ldt;

import org.testng.annotations.Test;

import static com.farpost.ldt.TestUtils.near;
import static org.easymock.EasyMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TestRunnerTest {

	@Test
	public void testRunnerCanCalculateTotalTime() throws InterruptedException {
		TestRunner runner = new TestRunner();
		TestResult result = runner.run(new SleepTask(15));

		assertThat(result.getConcurrencyLevel(), equalTo(1));
		assertThat(result.getTotalTime(), near(15L, 1));
	}

	@Test
	public void testRunnerCanRunTasksSeveralTimes() throws InterruptedException {
		TestRunner runner = new TestRunner();
		int samples = 5;
		long delay = 19;
		runner.setThreadSamplesCount(samples);
		runner.setConcurrencyLevel(3);
		TestResult result = runner.run(new SleepTask(delay));

		assertThat(result.getConcurrencyLevel(), equalTo(3));
		assertThat(result.getThreadSamplesCount(), equalTo(samples));
		assertThat(result.getTotalTime(), near(samples * delay, 10));
	}

	@Test
	public void testRunnerCanMeasureThroughput() throws InterruptedException {
		TestRunner runner = new TestRunner();
		runner.setConcurrencyLevel(2);
		runner.setThreadSamplesCount(10);

		TestResult result = runner.run(new SleepTask(10));
		assertThat(result.getThroughput(), equalTo(200f));
	}

  @Test
  public void testRunnerShouldCallPrepareMethod() throws Exception {
    Task t = createMock(Task.class);

    t.prepare();
    t.execute();
    t.execute();
    t.cleanup();

    replay(t);
    TestRunner runner = new TestRunner();
    runner.setConcurrencyLevel(2);
    runner.setThreadSamplesCount(1);

    runner.run(t);

    verify(t);
  }
}
