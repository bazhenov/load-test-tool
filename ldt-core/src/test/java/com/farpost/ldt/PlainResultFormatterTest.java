package com.farpost.ldt;

import org.testng.annotations.Test;

import static com.farpost.ldt.PlainResultFormatter.formatTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PlainResultFormatterTest {

	@Test
	public void formatterCanFormatSeconds() throws InterruptedException {
		assertThat(formatTime(0), equalTo("<1mcs"));
		assertThat(formatTime(23), equalTo("~23mcs"));
		assertThat(formatTime(15000), equalTo("15ms"));
		assertThat(formatTime(1000000), equalTo("1.0s"));
		assertThat(formatTime(1235000), equalTo("1.235s"));
		assertThat(formatTime(65235000l), equalTo("1m 5s"));

	}
}
