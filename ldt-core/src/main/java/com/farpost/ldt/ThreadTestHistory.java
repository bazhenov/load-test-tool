package com.farpost.ldt;

import java.util.ArrayList;
import java.util.List;

/**
 * Этот класс используется в {@link TestRunner} для того чтобы отслеживать результаты
 * тестов в отдельном потоке (worker'е). Результирующий {@link TestResult} конструируется на
 * основании объектов данного типа.
 * <p/>
 * <pre>
 * --- idle
 * *** test execution
 * <p/>
 *          5ms     9ms     4ms     13ms
 * T1  |---*****-*********-****-*************--|
 * <p/>
 *         3ms  6ms     7ms      11ms
 * T2  |---***-******-*******-***********------|
 * </pre>
 * Выше приведена история выполнения двух нитей, каждая из которых выполнила 4 теста с
 * соответствующими временными задержками. Данный класс описывает историю выполнения тестов
 * одной отдельно взятой нити.
 */
public class ThreadTestHistory {

	private final List<Long> samples = new ArrayList<Long>();

	/**
	 * @return total execution time in microseconds
	 */
	public long getTotalTime() {
		long totalExecutionTime = 0;
		for (long sample : samples) {
			totalExecutionTime += sample;
		}
		return totalExecutionTime;
	}

	public long[] getSamples() {
		long[] result = new long[samples.size()];
		for (int i = 0; i < samples.size(); i++) {
			result[i] = samples.get(i);
		}
		return result;
	}

	public void registerSample(long time) {
		samples.add(time);
	}
}
