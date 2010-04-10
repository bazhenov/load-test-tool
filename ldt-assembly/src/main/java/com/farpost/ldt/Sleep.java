package com.farpost.ldt;

import static java.lang.Thread.sleep;

public class Sleep {

	private long delay;

  public Sleep() {
    delay = 300;
  }

  public Sleep(long delay) {
		this.delay = delay;
	}

  public void setDelay(long delay) {
    this.delay = delay;
  }

  public void execute() throws Exception {
		sleep(delay);
	}
}