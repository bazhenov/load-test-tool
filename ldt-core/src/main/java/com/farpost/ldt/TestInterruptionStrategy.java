package com.farpost.ldt;

/**
 * Implementation if this interface determine when test should be stopped.
 * <p/>
 * Method {@link #shouldContinue(long)} called from multiple threads, but should return interruption status
 * for <b>current thread</b>. Typical implementation pattern is to use {@link ThreadLocal} values for acheiving goal.
 */
public interface TestInterruptionStrategy {

	/**
	 * Returns wherever test runner should execute test one more time.
	 * <p/>
	 * Test runner call this method after each execution and continue
	 * to run tests until this method returns <code>false</code>.
	 *
	 * @param testExecutionTime execution time of last test
	 * @return <code>true</code> if test should be executed again. <code>false</code> otherwise.
	 */
	boolean shouldContinue(long testExecutionTime);
}
