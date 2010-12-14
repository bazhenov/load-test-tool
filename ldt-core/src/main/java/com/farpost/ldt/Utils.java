package com.farpost.ldt;

import java.util.Arrays;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.util.Arrays.sort;

public class Utils {

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
	 * Returns percentile value for given vector.
	 *
	 * @param vector	 value vector
	 * @param fraction fraction value of percentile greater than {@code 0} and less than {@code 1}
	 * @return percentile value
	 */
	public static long percentile(long[] vector, double fraction) {
		if (fraction <= 0 || fraction >= 1) {
			throw new IllegalArgumentException("Percentile should be greater than 0 and less than 1");
		}
		if (vector.length <= 0) {
			throw new IllegalArgumentException("Empty vector given");
		}
		sort(vector);
		int index = (int) (vector.length * fraction - 1);
		return vector[index];
	}
}
