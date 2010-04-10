package com.farpost.ldt;

/**
 * This is base interface for load testing task
 * <p/>
 * Method {@link Task#execute()} should be thread safe, as test runner will call this method from different
 * threads if concurrency level greater than 1. 
 */
public interface Task {

  /**
   * Execute test task. Method implementaion shoud be thread safe.
   *
   * @throws Exception if task execution fails
   */
	void execute() throws Exception;
}
