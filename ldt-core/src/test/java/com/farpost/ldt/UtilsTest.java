package com.farpost.ldt;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UtilsTest {

	@Test
	public void testRunnerCanCalculateStandardDeviation() {
		long[][] numbers = new long[][]{{2, 4, 4, 4, 5, 5, 7, 9}};
		assertThat(Utils.calculateStdDev(numbers), equalTo(2d));
	}
}
