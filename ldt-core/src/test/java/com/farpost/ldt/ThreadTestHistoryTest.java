package com.farpost.ldt;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ThreadTestHistoryTest {

	@Test
	public void threadHistoryContainsMaximumAndMinimumExecutionTime() throws Exception {
		ThreadTestHistory history = new ThreadTestHistory();
		history.registerSample(13);
		history.registerSample(145);
		history.registerSample(8);
		history.registerFailedSample(16);
		history.registerSample(2);

		assertThat(history.getMaxTime(), equalTo(145L));
		assertThat(history.getMinTime(), equalTo(2L));
		assertThat(history.getSamplesCount(), equalTo(5));
		assertThat(history.getFailedSamplesCount(), equalTo(1));
	}
}
