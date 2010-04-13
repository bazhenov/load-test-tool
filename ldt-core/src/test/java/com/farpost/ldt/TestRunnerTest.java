package com.farpost.ldt;

import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;

import static com.farpost.ldt.TestUtils.near;
import static org.easymock.EasyMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TestRunnerTest {

	@Test
	public void testRunnerCanCalculateTotalTime()
    throws InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException,
    IllegalAccessException {
		TestRunner runner = new TestRunner();
    Task task = new PojoTask<SleepTask>(new SleepTask(15), "execute");
    TestResult result = runner.run(task);

		assertThat(result.getConcurrencyLevel(), equalTo(1));
		assertThat(result.getTotalTime(), near(15000L, 1000));
	}

	@Test
	public void testRunnerCanRunTasksSeveralTimes() throws InterruptedException, NoSuchMethodException {
		TestRunner runner = new TestRunner();
		int samples = 5;
		long delay = 19;
		runner.setThreadSamplesCount(samples);
		runner.setConcurrencyLevel(3);

    Task task = new PojoTask<SleepTask>(new SleepTask(delay));
		TestResult result = runner.run(task);

		assertThat(result.getConcurrencyLevel(), equalTo(3));
		assertThat(result.getThreadSamplesCount(), equalTo(samples));
		assertThat(result.getTotalTime(), near(samples * delay * 1000, 10 * 1000));
	}

	@Test
	public void testRunnerCanMeasureThroughput() throws InterruptedException, NoSuchMethodException {
		TestRunner runner = new TestRunner();
		runner.setConcurrencyLevel(2);
		runner.setThreadSamplesCount(10);

    Task task = new PojoTask<SleepTask>(new SleepTask(10));
		TestResult result = runner.run(task);
		assertThat(result.getThroughput(), near(200f, 50f));
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
