package com.farpost.ldt;

import static java.lang.Thread.sleep;

public class SleepTask {

	private long delay;

	public SleepTask(long delay) {
		this.delay = delay;
	}

  public SleepTask() {
    this(10);
  }

  public void setDelay(long delay) {
    this.delay = delay;
  }

  public void execute() throws Exception {
		sleep(delay);
	}
}
