package com.farpost.ldt;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class TestRunner {

	private volatile int concurrencyLevel = 1;
	private volatile int threadSamplesCount = 1;

	private static final Logger log = Logger.getLogger(TestRunner.class);

	public synchronized TestResult run(Task task) throws InterruptedException {
		prepare(task);
		try {
			CyclicBarrier sync = new CyclicBarrier(concurrencyLevel + 1);
			List<Worker> workers = spawnWorkers(task, concurrencyLevel, threadSamplesCount, sync);
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

	private List<Worker> spawnWorkers(Task task, int concurrencyLevel, int samplesCount, CyclicBarrier syncBarrier) {
		List<Worker> workerList = new ArrayList<Worker>(concurrencyLevel);
		for (int i = 0; i < concurrencyLevel; i++) {
			Worker worker = new Worker(task, samplesCount, syncBarrier);
			workerList.add(worker);
			new Thread(worker).start();
		}
		return workerList;
	}

	private void prepare(Task task) {
		try {
			task.prepare();
		} catch (Exception e) {
			throw new TaskPrepareFailedException(e);
		}
	}

	private void cleanup(Task task) {
		try {
			task.cleanup();
		} catch (Exception e) {
			log.error("Task cleanup failed", e);
		}
	}

	private static class Worker implements Runnable {

		private final Task task;
		private final int times;
		private final CyclicBarrier syncBarrier;
		private final ThreadTestHistory threadTestHistory;
		private final static Logger log = Logger.getLogger(Worker.class);

		private Worker(Task task, int times, CyclicBarrier syncBarrier) {
			this.task = task;
			this.times = times;
			this.syncBarrier = syncBarrier;
			threadTestHistory = new ThreadTestHistory(times);
		}

		public void run() {
			try {
				syncBarrier.await();
				for (int i = 0; i < times; i++) {
					long startTime = System.currentTimeMillis();
					try {
						task.execute();
						long endTime = System.currentTimeMillis();
						threadTestHistory.registerSample(endTime - startTime);
					} catch (Exception e) {
						long endTime = System.currentTimeMillis();
						threadTestHistory.registerSample(endTime - startTime);
						log.error("Task execution failed", e);
					}
				}
				syncBarrier.await();
			} catch (BrokenBarrierException e) {
				log.error(e.getMessage(), e);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}

		public ThreadTestHistory getTestResult() {
			return threadTestHistory;
		}
	}
}
