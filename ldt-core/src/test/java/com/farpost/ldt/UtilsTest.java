package com.farpost.ldt;

import org.testng.annotations.Test;

import static com.farpost.ldt.Utils.calculateStdDev;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UtilsTest {

	@Test
	public void testRunnerCanCalculateStandardDeviation() {
		long[][] numbers = new long[][]{{2, 4, 4, 4, 5, 5, 7, 9}};
		assertThat(calculateStdDev(numbers), equalTo(2d));
	}

	@Test
	public void utilsCanCalculatePercentaileStatistic() {
		long[] vector = new long[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		assertThat(Utils.percentile(vector, 0.9), equalTo(9L));
		assertThat(Utils.percentile(vector, 0.99), equalTo(9L));
		assertThat(Utils.percentile(vector, 0.01), equalTo(1L));
	}
}
