package com.farpost.maven.plugins;

import com.farpost.ldt.*;
import com.farpost.ldt.formatter.ResultFormatter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import static com.farpost.ldt.TaskFactory.createTask;

/**
 * Implementation of ldt:test goal.
 *
 * This goal uses ldt API for executing given task.
 *
 * @goal test
 * @phase process-sources
 */
public class TestMojo extends AbstractMojo {

	/**
	 * Test name
	 *
	 * @parameter expression="${ldt.testName}"
	 * @required
	 */
	private String testName;

	/**
	 * Concurrency level
	 *
	 * @parameter expression="${ldt.concurrencyLevel}"
	 */
	private int concurrencyLevel = 1;

	/**
	 * Call count
	 *
	 * @parameter expression="${ldt.callCount}"
	 */
	private int callCount = 0;

	/**
	 * Test time
	 *
	 * @parameter expression="${ldt.testTime}"
	 */
	private int time = 0;

	public void execute() throws MojoExecutionException {
		TestRunner runner = new TestRunner();
		runner.setConcurrencyLevel(concurrencyLevel);
		if (callCount > 0) {
			runner.setTestInterruptionStarategy(new CallCountInterruptionStrategy(callCount));
		}else if (time > 0) {
			runner.setTestInterruptionStarategy(new TimeFrameInterruptionStrategy(time));
		}else{
			throw new RuntimeException("Missing interruption strategy. Please set testTime or callCount parameter");
		}

		Log log = getLog();
		log.debug("Running tests for type: " + testName);
		try {
			Task task = createTask(testName);
			TestResult result = runner.run(task);
			ResultFormatter formatter = new MavenLogFormatter(getLog());
			formatter.format(result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
