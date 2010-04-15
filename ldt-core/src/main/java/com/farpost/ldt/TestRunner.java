package com.farpost.ldt;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class TestRunner {

	private int concurrencyLevel = 1;
	private int threadSamplesCount = 1;
	private int warmUpThreshold = 10;

	private static final Logger log = Logger.getLogger(TestRunner.class);

	public synchronized TestResult run(Task task) throws InterruptedException {
		prepare(task);
		try {
			CyclicBarrier sync = new CyclicBarrier(concurrencyLevel + 1);
			List<Worker> workers = spawnWorkers(task, concurrencyLevel, threadSamplesCount, warmUpThreshold, sync);
			startAndWaitForComplete(sync);

			List<ThreadTestHistory> threadHistory = collectExecutionHistory(workers);
			return new TestResult(threadHistory, threadSamplesCount);
		} finally {
			cleanup(task);
		}
	}

	public void setConcurrencyLevel(int concurrencyLevel) {
		this.concurrencyLevel = concurrencyLevel;
	}

	public void setThreadSamplesCount(int samplesCount) {
		this.threadSamplesCount = samplesCount;
	}

	private void startAndWaitForComplete(CyclicBarrier syncBarrier) throws InterruptedException {
		try {
			syncBarrier.await();
			syncBarrier.await();
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

	private List<Worker> spawnWorkers(Task task, int concurrencyLevel, int samplesCount, int warmUpThreshold, CyclicBarrier syncBarrier) {
		List<Worker> workerList = new ArrayList<Worker>(concurrencyLevel);
		for (int i = 0; i < concurrencyLevel; i++) {
			Worker worker = new Worker(task, samplesCount, warmUpThreshold, syncBarrier);
			workerList.add(worker);
			new Thread(worker).start();
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
	 * Warm up threshold is positive number how many times test will be executed before measurements are started.
	 * Warm up period allow more accuratly measure execution statistic as first test executions may be diversed
	 * by HotSpot compiller and other JVM activity.
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
	 * Worker Ñ a thread runnable which perform actual measurements
	 */
	private static class Worker implements Runnable {

		private final Task task;
		private final int times;
		private final int warmUpThreshold;
		private final CyclicBarrier syncBarrier;
		private final ThreadTestHistory threadTestHistory;
		private final static Logger log = Logger.getLogger(Worker.class);

		private Worker(Task task, int times, int warmUpThreshold, CyclicBarrier syncBarrier) {
			this.task = task;
			this.times = times;
			this.warmUpThreshold = warmUpThreshold;
			this.syncBarrier = syncBarrier;
			threadTestHistory = new ThreadTestHistory(times);
		}

		public void run() {
			for (int i = 0; i < warmUpThreshold; i++) {
				runAndMeasure(task);
			}
			try {
				syncBarrier.await();
				for (int i = 0; i < times; i++) {
					long runningTime = runAndMeasure(task);
					threadTestHistory.registerSample(runningTime);
				}
				syncBarrier.await();
			} catch (BrokenBarrierException e) {
				log.error(e.getMessage(), e);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}

		/**
		 * Run task and return it's execution time in microseconds.
		 *
		 * {@link System#nanoTime()} used for measurments. This method provides nothrow guarantee.
		 *
		 * @param task testing task
		 * @return task execution time
		 */
		private long runAndMeasure(Task task) {
			long startTime = System.nanoTime();
			try {
				task.execute();
				long endTime = System.nanoTime();
				return (endTime - startTime) / 1000;

			} catch (Exception e) {
				long endTime = System.nanoTime();
				log.error("Task execution failed", e);
				return (endTime - startTime) / 1000;
			}
		}

		public ThreadTestHistory getTestResult() {
			return threadTestHistory;
		}
	}
}
