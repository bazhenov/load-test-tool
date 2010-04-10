package com.farpost.ldt;

/**
 * This is base interface for load testing task
 * <p/>
 * Method {@link Task#execute()} should be thread safe, as test runner will call this method from different
 * threads if concurrency level greater than 1.
 */
public interface Task {

  /**
   * Prepate environment for task execution.
   * <p/>
   * This method guarantied called by test runner only once before task execution in any thread.
   * So, any action happend in {@link #prepare()} method happened before any action in {@link #execute()} method.
   * <p/>
   * If task preparing fails, task will never been scheduled for execution.
   *
   * @throws Exception if task prepating fails.
   */
  void prepare() throws Exception;

  /**
   * Execute test task. Method implementaion shoud be thread safe.
   *
   * @throws Exception if task execution fails
   */
  void execute() throws Exception;

  /**
   * Cleanup environment after test execution.
   * <p/>
   * This method called once after all test invokation are completed (successfully or not). Any action given place
   * in {@link #execute()} method happend before any action {@link #cleanup()}.
   *
   * @throws Exception if task cleanup failed
   */
  void cleanup() throws Exception;
}
