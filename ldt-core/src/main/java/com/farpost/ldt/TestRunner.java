package com.farpost.ldt;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.System.nanoTime;
import static java.lang.Thread.currentThread;

/**
 * This class runs the tests, measure test elapsed time and build thread execution history.
 *
 * @see TestInterruptionStrategy
 */
public class TestRunner {

	private volatile int concurrencyLevel = 1;
	private volatile int threadSamplesCount = 1;
	private volatile int warmUpThreshold = 10;
	private volatile TestInterruptionStrategy testInterruptionStarategy = new CallCountInterruptionStrategy(1);

	private static final Logger log = Logger.getLogger(TestRunner.class);

	public synchronized TestResult run(Task task) throws InterruptedException {
		prepare(task);
		// Saving concurrency level locally, so now we are safe agains concurrent changes of this.concurrencyLevel
		int threadPoolSize = concurrencyLevel;
		try {
			CyclicBarrier sync = new CyclicBarrier(threadPoolSize + 1);
			List<Worker> workers = spawnWorkers(task, threadPoolSize, warmUpThreshold, sync);
			startAndWaitForComplete(sync);

			List<ThreadTestHistory> threadHistory = collectExecutionHistory(workers);
			return new TestResult(threadHistory);
		} finally {
			cleanup(task);
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
		List<ThreadTestHistory> threadsHistory = new ArrayList<ThreadTestHistory>(concurrencyLevel);
		for (Worker worker : workerList) {
			threadsHistory.add(worker.getTestResult());
		}
		return threadsHistory;
	}

	private List<Worker> spawnWorkers(Task task, int threadPoolSize, int warmUpThreshold, CyclicBarrier sync) {
		List<Worker> workerList = new ArrayList<Worker>(threadPoolSize);
		for (int i = 0; i < threadPoolSize; i++) {
			Worker worker = new Worker(task, testInterruptionStarategy, warmUpThreshold, sync);
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

	public static double calculateStdDev(long[][] numbers) {
		long sum = 0;
		long mean = 0;
		int count = 0;
		for (long[] row : numbers) {
			count += row.length;
			for (long number : row) {
				sum += number;
				mean += pow(number, 2);
			}
		}
		double avarage = sum / count;
		return sqrt(mean / count - pow(avarage, 2));
	}

	/**
	 * Worker thread runnable which perform actual measurements
	 */
	private static class Worker implements Runnable {

		private final Task task;
		private final TestInterruptionStrategy strategy;
		private final int warmUpThreshold;
		private final CyclicBarrier syncBarrier;
		private final ThreadTestHistory threadTestHistory;
		private final static Logger log = Logger.getLogger(Worker.class);

		private Worker(Task task, TestInterruptionStrategy strategy, int warmUpThreshold, CyclicBarrier syncBarrier) {
			this.task = task;
			this.strategy = strategy;
			this.warmUpThreshold = warmUpThreshold;
			this.syncBarrier = syncBarrier;
			threadTestHistory = new ThreadTestHistory();
		}

		public void run() {
			for (int i = 0; i < warmUpThreshold; i++) {
				runAndMeasure(task);
			}
			try {
				syncBarrier.await();
				long runningTime;
				do {
					runningTime = runAndMeasure(task);
					threadTestHistory.registerSample(runningTime);
				} while (strategy.shouldContinue(runningTime));
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
		 * @return task execution time in microseconds
		 */
		private long runAndMeasure(Task task) {
			long endTime, startTime = nanoTime();
			try {
				task.execute();
			} catch (Exception e) {
				log.error("Task execution failed", e);
			} finally {
				endTime = nanoTime();
			}
			return (endTime - startTime) / 1000;
		}

		public ThreadTestHistory getTestResult() {
			return threadTestHistory;
		}
	}
}
