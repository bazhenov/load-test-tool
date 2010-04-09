package com.farpost.loadTest;

import static java.lang.Thread.sleep;

public class SimpleTask extends AbstractTask {

	public void execute() throws Exception {
		sleep(15);
	}
}
