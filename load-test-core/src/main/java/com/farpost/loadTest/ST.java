package com.farpost.loadTest;

import static java.lang.Thread.sleep;

public class ST extends AbstractTask {

	private final long delay;

	public ST(long delay) {
		this.delay = delay;
	}

  public ST() {
    delay = 300;
  }

  public void execute() throws Exception {
		sleep(delay);
	}
}