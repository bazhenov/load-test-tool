package com.farpost.ldt;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static java.lang.System.nanoTime;
import static java.lang.Thread.currentThread;

/**
 * This class runs the tests, measure test elapsed time and build thread execution history.
 *
 * @see TestInterruptionStrategy
 */
public class TestRunner {

	private volatile int concurrencyLevel = 1;
	private volatile int warmUpThreshold = 10;
	private volatile TestInterruptionStrategy testInterruptionStarategy = new CallCountInterruptionStrategy(1);

	private static final Logger log = Logger.getLogger(TestRunner.class);

	public synchronized TestResult run(Task task) throws InterruptedException {
		// Saving concurrency level locally, so now we are safe agains concurrent changes of this.concurrencyLevel
		int threadPoolSize = concurrencyLevel;
		prepare(task);
		warmUp(task);
		try {
			CyclicBarrier sync = new CyclicBarrier(threadPoolSize + 1);
			List<Worker> workers = spawnWorkers(task, threadPoolSize, sync);
			startAndWaitForComplete(sync);

			List<ThreadTestHistory> threadHistory = collectExecutionHistory(workers);
			return new TestResult(threadHistory);
		} finally {
			cleanup(task);
		}
	}

	private void warmUp(Task task) {
		for (int i = 0; i < warmUpThreshold; i++) {
			try {
				task.execute();
			} catch (Exception e) {
				log.error("Task warm up failed", e);
			}
		}
	}

	public void setConcurrencyLevel(int concurrencyLevel) {
		this.concurrencyLevel = concurrencyLevel;
	}

	public void setThreadSamplesCount(int samplesCount) {
		setTestInterruptionStarategy(new CallCountInterruptionStrategy(samplesCount));
	}

	public void setTestInterruptionStarategy(TestInterruptionStrategy strategy) {
		this.testInterruptionStarategy = strategy;
	}

	/**
	 * Release all working threads and waits them to complete their job using shared {@link CyclicBarrier}.
	 *
	 * @param sync shared barrier
	 * @throws InterruptedException if thread was interrupted
	 */
	private void startAndWaitForComplete(CyclicBarrier sync) throws InterruptedException {
		try {
			// Releasing worker threads
			sync.await();
			// Waiting to complete
			sync.await();
		} catch (BrokenBarrierException e) {
			throw new TestExecutionException(e);
		}
	}

	private List<ThreadTestHistory> collectExecutionHistory(List<Worker> workerList) {
		List<ThreadTestHistory> threadsHistory = new ArrayList<ThreadTestHistory>(workerList.size());
		for (Worker worker : workerList) {
			threadsHistory.add(worker.getTestResult());
		}
		return threadsHistory;
	}

	private List<Worker> spawnWorkers(Task task, int threadPoolSize, CyclicBarrier sync) {
		List<Worker> workerList = new ArrayList<Worker>(threadPoolSize);
		for (int i = 0; i < threadPoolSize; i++) {
			Worker worker = new Worker(task, testInterruptionStarategy, sync);
			workerList.add(worker);
			new Thread(worker, "LDT Thread #" + i).start();
		}
		return workerList;
	}

	private static void prepare(Task task) {
		try {
			task.prepare();
		} catch (Exception e) {
			throw new TaskPrepareFailedException(e);
		}
	}

	private static void cleanup(Task task) {
		try {
			task.cleanup();
		} catch (Exception e) {
			log.error("Task cleanup failed", e);
		}
	}

	/**
	 * Setting warm up threshold for test runner.
	 * <p/>
	 * Warm up threshold is positive number how many times test will be executed <i>by each worker thread</i>
	 * before measurements are started. Warm up period allow more accuratly measure execution statistic as
	 * first test executions may be diversed by HotSpot compiller and other JVM activity.
	 * <p/>
	 * Warm up default value is 10.
	 *
	 * @param times warm up threshold
	 * @throws IllegalArgumentException if invalid warm up threshold number given
	 */
	public void setWarmUpThreshold(int times) {
		if (times < 0) {
			throw new IllegalArgumentException();
		}
		warmUpThreshold = times;
	}

	/**
	 * Worker thread runnable which perform actual measurements
	 */
	private static class Worker implements Runnable {

		private final Task task;
		private final TestInterruptionStrategy strategy;
		private final CyclicBarrier syncBarrier;
		private final static Logger log = Logger.getLogger(Worker.class);
		private ThreadTestHistory threadTestHistory;

		private Worker(Task task, TestInterruptionStrategy strategy, CyclicBarrier syncBarrier) {
			this.task = task;
			this.strategy = strategy;
			this.syncBarrier = syncBarrier;
		}

		public void run() {
			try {
				syncBarrier.await();
				threadTestHistory = runAndMeasure(task);
				syncBarrier.await();
			} catch (BrokenBarrierException e) {
				log.error(e.getMessage(), e);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
				currentThread().interrupt();
			}
		}

		/**
		 * Run task and return it's execution time in microseconds.
		 * <p/>
		 * {@link System#nanoTime()} used for measurments. This method provides nothrow guarantee.
		 *
		 * @param task testing task
		 */
		private ThreadTestHistory runAndMeasure(Task task) {
			ThreadTestHistory history = new ThreadTestHistory();
			long endTime, elapsedTime, startTime;
			Exception cause;
			do {
				startTime = nanoTime();
				cause = null;
				try {
					task.execute();
				} catch (Exception e) {
					cause = e;
				} finally {
					endTime = nanoTime();
				}
				elapsedTime = (endTime - startTime) / 1000;

				if (cause == null) {
					history.registerSample(elapsedTime);
				} else {
					history.registerFailedSample(elapsedTime);
					log.error("Task execution failed", cause);
				}
			} while (strategy.shouldContinue(elapsedTime));
			return history;
		}

		public ThreadTestHistory getTestResult() {
			return threadTestHistory;
		}
	}
}
