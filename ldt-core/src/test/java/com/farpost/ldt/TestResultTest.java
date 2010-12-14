package com.farpost.ldt;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TestResultTest {

	@Test
	public void testResultAggregateInfoFromThreadTestHistory() {
		ThreadTestHistory t1 = new ThreadTestHistory() {{
			registerSample(15);
			registerSample(17);
			registerSample(18);
		}};

		ThreadTestHistory t2 = new ThreadTestHistory() {{
			registerSample(19);
			registerSample(6);
		}};

		TestResult result = new TestResult(t1, t2);
		assertThat(result.getMinTime(), equalTo(6L));
		assertThat(result.getMaxTime(), equalTo(19L));
		assertThat(result.getSamplesCount(), equalTo(5));
	}
}
