package com.farpost.ldt;

import java.util.Map;

public abstract class AbstractTask implements Task {

	public String getDescription() {
		return null;
	}

  public void prepare() throws Exception {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void execute() throws Exception {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void cleanup() throws Exception {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void setParameters(Map<String, String> parameters) throws IllegalArgumentException {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
