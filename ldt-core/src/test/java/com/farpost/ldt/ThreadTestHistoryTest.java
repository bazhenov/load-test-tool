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
		history.registerSample(2);

		assertThat(history.getMaximumExecutionTime(), equalTo(145L));
		assertThat(history.getMinimumExecutionTime(), equalTo(2L));
	}
}
