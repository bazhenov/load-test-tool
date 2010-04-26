package com.farpost.ldt;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;

import static com.farpost.ldt.TestUtils.near;
import static org.easymock.EasyMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TestRunnerTest {

	private TestRunner runner;

	@BeforeMethod
	public void setUp() throws Exception {
		runner = new TestRunner();
	}

	@Test
	public void testRunnerCanCalculateTotalTime()
		throws InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException,
		IllegalAccessException {

		Task task = new PojoTask<SleepTask>(new SleepTask(15), "execute");
		TestResult result = runner.run(task);

		assertThat(result.getConcurrencyLevel(), equalTo(1));
		assertThat(result.getTotalTime(), near(15000L, 1000));
	}

	@Test
	public void testRunnerCanRunTasksSeveralTimes() throws InterruptedException, NoSuchMethodException {
		int samples = 5;
		long delay = 19;
		int concurrencyLevel = 3;
		runner.setThreadSamplesCount(samples);
		runner.setConcurrencyLevel(concurrencyLevel);

		Task task = new PojoTask<SleepTask>(new SleepTask(delay));
		TestResult result = runner.run(task);

		assertThat(result.getConcurrencyLevel(), equalTo(concurrencyLevel));
		assertThat(result.getThreadSamplesCount(), equalTo(samples * concurrencyLevel));
		assertThat(result.getTotalTime(), near(samples * delay * 1000, 10 * 1000));
	}

	@Test
	public void testRunnerCanMeasureThroughput() throws InterruptedException, NoSuchMethodException {
		runner.setConcurrencyLevel(2);
		runner.setThreadSamplesCount(10);

		Task task = new PojoTask<SleepTask>(new SleepTask(10));
		TestResult result = runner.run(task);
		assertThat(result.getThroughput(), near(200f, 50f));
	}

	@Test
	public void testRunnerShouldCallPrepareMethod() throws Exception {
		runner.setWarmUpThreshold(0);
		runner.setConcurrencyLevel(2);
		runner.setThreadSamplesCount(1);

		Task task = createMock(Task.class);

		task.prepare();
		task.execute();
		task.execute();
		task.cleanup();

		replay(task);

		runner.run(task);

		verify(task);
	}

	@Test
	public void testRunnerShouldWarmUpTest() throws Exception {
		runner.setWarmUpThreshold(2);
		runner.setThreadSamplesCount(1);

		Task task = createMock(Task.class);

		task.prepare();

		task.execute();
		task.execute();

		task.execute();

		task.cleanup();

		replay(task);
		runner.run(task);
		verify(task);
	}

	@Test
	public void testRunnerCanCalculateStandardDeviation() {
		long[] numbers = new long[]{2, 4, 4, 4, 5, 5, 7, 9};
		assertThat(TestRunner.calculateStdDev(numbers), equalTo(2d));
	}
}
