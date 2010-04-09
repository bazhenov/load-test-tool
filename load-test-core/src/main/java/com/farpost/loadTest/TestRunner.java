package com.farpost.loadTest;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class TestRunner {

	private volatile int concurrencyLevel = 1;
  private volatile int threadSamplesCount = 1;

	public void setConcurrencyLevel(int concurrencyLevel) {
		this.concurrencyLevel = concurrencyLevel;
	}

  public void setThreadSamplesCount(int samplesCount) {
    this.threadSamplesCount = samplesCount;
  }

  public synchronized TestResult run(Task task) throws InterruptedException {
    List<Worker> workerList = new ArrayList<Worker>(concurrencyLevel);
    CyclicBarrier syncBarrier = new CyclicBarrier(concurrencyLevel+1);
    for (int i = 0; i < concurrencyLevel; i++) {
      Worker worker = new Worker(task, threadSamplesCount, syncBarrier);
      workerList.add(worker);
      new Thread(worker).start();
    }
    try {
      syncBarrier.await();
      syncBarrier.await();
    } catch (BrokenBarrierException e) {
      throw new TestExecutionException(e);
    }

	  List<ThreadTestHistory> threadHistory = new ArrayList<ThreadTestHistory>(concurrencyLevel);
    for (Worker worker : workerList) {
      threadHistory.add(worker.getTestResult());
    }
	  return new TestResult(threadHistory, threadSamplesCount);
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
        for ( int i = 0; i < times; i++ ) {
          long startTime = System.currentTimeMillis();
          try {
            task.execute();
            long endTime = System.currentTimeMillis();
            threadTestHistory.registerSample(endTime-startTime);
          } catch ( Exception e ) {
            long endTime = System.currentTimeMillis();
            threadTestHistory.registerSample(endTime-startTime);
            log.error("Task execution failed", e);
          }
        }
        syncBarrier.await();
      } catch ( BrokenBarrierException e ) {
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
