package com.farpost.ldt;

import java.util.Map;

import static java.lang.Thread.sleep;

public class ST extends AbstractTask {

	private final long delay;

	public ST(long delay) {
		this.delay = delay;
	}

  public ST() {
    delay = 300;
  }

  public void prepare() throws Exception {

  }

  public void cleanup() throws Exception {

  }

  public void setParameters(Map<String, String> parameters) throws IllegalArgumentException {
    
  }

  public void execute() throws Exception {
		sleep(delay);
	}
}